package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.transition.*;
import android.view.*;
import android.widget.*;
import com.flurgle.blurkit.*;
import fjy.ins.*;
import fjy.ins.fragment.*;
import fjy.ins.model.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import fjy.ins.R;

public class SettingsActivity extends AppCompatActivity {

	Toolbar tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager()
		.beginTransaction()
		.replace(R.id.frag_settings,new PrefFragment())
		.commit();
		
		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_setting);
		
		DBManager dm = new DBManager(this); 
		final List<Note> noteDataList = new ArrayList<>();
		dm.readFromDB(noteDataList);
		final ImageView iv= (ImageView)findViewById(R.id.iv_setting_bg);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					App.glideWithBg(noteDataList, iv, SettingsActivity.this);
				}
			}, 1200);
		tb = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(tb);
		
		fab.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText("2036293523@qq.com");
					Sna("邮箱已复制到剪切板，若有问题，请联系");
				}
			});
		BlurKit.init(this);
        getWindow().setBackgroundDrawable(new BitmapDrawable(
                                              BlurKit.getInstance()
                                              .blur(((BitmapDrawable)
                                                    WallpaperManager.getInstance(this)
                                                    .getDrawable())
                                                    .getBitmap(), 6)));
		getWindow().setEnterTransition(new Explode().setDuration(500));
    }

	private void Sna(String p0)
	{
		Snackbar.make(tb,p0,0).show();
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
