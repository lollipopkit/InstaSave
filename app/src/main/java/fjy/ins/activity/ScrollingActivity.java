package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.transition.*;
import android.view.*;
import android.widget.*;
import com.flurgle.blurkit.*;
import com.tomer.fadingtextview.*;
import fjy.ins.*;
import fjy.ins.model.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import fjy.ins.R;

public class ScrollingActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;
	private FadingTextView ftv;

    public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }
	
	public void Sna(String msg){
		Snackbar.make(toolbar, msg, 0).show();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        init();
    }

    private void init(){
		ftv = $(R.id.tv_scroll);
        toolbar = $(R.id.toolbar_scroll);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
		DBManager dm = new DBManager(this); 
		final List<Note> noteDataList = new ArrayList<>();
		dm.readFromDB(noteDataList);
		final ImageView iv= $(R.id.iv_about_bg);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					App.glideWithBg(noteDataList, iv, ScrollingActivity.this);
				}
			}, 1200);
		fab = $(R.id.fab_about);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText("lollipopkit");
				Snackbar.make(toolbar, "支付宝用户名已复制，感谢您的捐赠", 0).show();
            }
        });
		if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT){
			getWindow().setNavigationBarColor(Color.TRANSPARENT);
		}
        
        getWindow().setBackgroundDrawable(new BitmapDrawable(
                                              BlurKit.getInstance()
                                              .blur(((BitmapDrawable)
                                                    WallpaperManager.getInstance(this)
                                                    .getDrawable())
                                                    .getBitmap(), 6)));
		getWindow().setEnterTransition(new Explode().setDuration(500));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
			
		}
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onBackPressed(){
		ftv.setVisibility(8);
		ftv.clearAnimation();
		ftv.stop();
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					superBack();
				}
			}, 100);
	}
	
	private void superBack(){
		super.onBackPressed();
	}
}
