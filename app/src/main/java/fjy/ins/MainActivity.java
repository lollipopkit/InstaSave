package fjy.ins;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.flurgle.blurkit.*;
import fjy.ins.activity.*;
import fjy.ins.adapter.*;
import fjy.ins.model.*;
import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.content.res.*;
import android.transition.*;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.bitmap.*;
import android.widget.CompoundButton.*;
import com.github.ybq.android.spinkit.style.*;
import com.youth.banner.*;
import com.github.brnunes.swipeablerecyclerview.*;

public class MainActivity extends AppCompatActivity
{
	private RecyclerView recyclerView;
	
	private DBManager dm;
	private List<Note> noteDataList = new ArrayList<>();
	private MultiplePulse db = new MultiplePulse();
    private MyAdapter adapter;
	private FloatingActionButton fab;
	private Banner iv;
	private Toolbar tb;
	private long firstTime = 0;
	public static boolean dataChanged = false;
	private boolean shouldDel = true;

	public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }
	
	public static void setChanged(){
		dataChanged = true;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		init();
    }
	
	private void init(){
		tb = $(R.id.toolbar);
		tb.setTitle("InstaSave");
		tb.setTitleTextColor(Color.WHITE);
		
		iv = $(R.id.iv_main_bg);
		recyclerView = $(R.id.recyclerView);
		
        dm = new DBManager(this); 
		dm.readFromDB(noteDataList);
			
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		
		updateView();
		
        fab = $(R.id.fab);
        initFab();	
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
			getWindow().setStatusBarColor(Color.TRANSPARENT);
			getWindow().setNavigationBarColor(Color.parseColor("#00000000"));
		}
		setSupportActionBar(tb);
        
        BlurKit.init(this);
        getWindow().setBackgroundDrawable(new BitmapDrawable(
                                              BlurKit.getInstance()
                                              .blur(((BitmapDrawable)
                                                    WallpaperManager.getInstance(this)
                                                    .getDrawable())
                                                    .getBitmap(), 6)));
		getWindow().setEnterTransition(new Slide().setDuration(500));
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					initBg();
				}
			}, 800);
    }

	private void updateView() {
        if (noteDataList.isEmpty()) {
			App.helpSna(tb, "貌似是第一次使用？\n是否查看使用帮助？", this);
        } else{
			noteDataList.clear();
		}
		dm.readFromDB(noteDataList);
		adapter = new MyAdapter(this, noteDataList);
		adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener(){
				@Override
				public void onItemClick(View view, int position)
				{
					final Note note = adapter.getItem(position);
					if(note.getTitle().equals("简介")){
						App.Sna(tb, "这只是个简介( ﾟдﾟ)\n点不进去的，不用试了(・へ・)");
					}else{
						Info info = new Info();
						info.setPath(note.getPath());
						info.setSum(note.getContent());
						info.setTitle(note.getTitle());
						if(new File("/sdcard/InstaSave/" + note.getPath().replace(".png", ".mp4")).exists()){
							info.setType(1);
						}else{info.setType(0);}
						ImageActivity.a(MainActivity.this, info);
					}
				}
			});
		recyclerView.setAdapter(adapter);
		SwipeableRecyclerViewTouchListener srvt = new SwipeableRecyclerViewTouchListener(recyclerView, new SwipeableRecyclerViewTouchListener.SwipeListener(){

				@Override
				public boolean canSwipeLeft(int p1)
				{
					return true;
				}

				@Override
				public boolean canSwipeRight(int p1)
				{
					return true;
				}

				@Override
				public void onDismissedBySwipeLeft(RecyclerView p1, int[] p2)
				{
					final int i = p2[0];
					final Note n = noteDataList.get(i);
					Snackbar.make(tb, "你抛弃了一张可爱的照片.", Snackbar.LENGTH_SHORT)
						.setAction("撤销", new View.OnClickListener(){
							@Override
							public void onClick(View p1)
							{
								shouldDel = false;
								dataRec(i, n);
							}
						}).show();
					for (int position : p2) {
						noteDataList.remove(position);
						adapter.notifyItemRemoved(position);
					}
					adapter.notifyDataSetChanged();
					new Handler().postDelayed(new Runnable(){

							@Override
							public void run()
							{
								if(shouldDel){
									final String nsme = "/sdcard/InstaSave/" + n.getPath();
									DBManager.getInstance(MainActivity.this).deleteNote(n.getId());
									new File(nsme).delete();
									new File(nsme.replace(".png", ".mp4")).delete();
									initBg();
								}
							}
						}, 2000);
				}

				@Override
				public void onDismissedBySwipeRight(RecyclerView p1, final int[] p2)
				{
					final int i = p2[0];
					final Note n = noteDataList.get(i);
					Snackbar.make(tb, "你抛弃了一条珍贵的记录.", Snackbar.LENGTH_SHORT)
						.setAction("撤销", new View.OnClickListener(){
							@Override
							public void onClick(View p1)
							{
								shouldDel = false;
								dataRec(i, n);
							}
						}).show();
					for (int position : p2) {
						noteDataList.remove(position);
						adapter.notifyItemRemoved(position);
					}
					adapter.notifyDataSetChanged();
					new Handler().postDelayed(new Runnable(){

							@Override
							public void run()
							{
								if(shouldDel){
									DBManager.getInstance(MainActivity.this).deleteNote(n.getId());
									initBg();
								}
							}
						}, 2000);
				}
			});
		recyclerView.addOnItemTouchListener(srvt);
    }
	
	private void dataRec(int position, Note note){
		noteDataList.add(position, note);
		adapter.notifyItemInserted(position);
		adapter.notifyDataSetChanged();
	}
	
	private void initBg(){
		List<String> images = new ArrayList<>();
		for(int i = noteDataList.size() - 1;i >= 0;i--){
			images.add(noteDataList.size() - i - 1, "/sdcard/InstaSave/" + noteDataList.get(i).getPath());
		}
		iv.setImages(images)
		  .setImageLoader(new GlideImageLoader())
		  .setDelayTime(2000)
		  .setBannerStyle(BannerConfig.NOT_INDICATOR)
		  .start();
	}
	private void initFab(){
		db.start();
		fab.setImageDrawable(db);
		fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
                    FetchActivity.a(MainActivity.this, noteDataList);
				}
			});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this,ScrollingActivity.class), ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                break;
			case R.id.action_setting:
				startActivity(new Intent(MainActivity.this, SettingsActivity.class), ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		db.start();
		if(dataChanged){
			updateView();
			initBg();
			dataChanged = false;
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		db.stop();
	}


	@Override
	public void onBackPressed()
	{
		long secondTime = System.currentTimeMillis();
		if (secondTime - firstTime > 2000) {
			firstTime = secondTime;
			App.Sna(tb, "再次点击来确定是否离开我(｡>﹏<｡)");
		} else {
			finish();
		}
	}
}
