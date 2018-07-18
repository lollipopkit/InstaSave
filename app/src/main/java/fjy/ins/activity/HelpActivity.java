package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;

import com.flurgle.blurkit.BlurKit;
import fjy.ins.*;

public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;

    public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }
	
	public void Sna(String msg){
		Snackbar.make(toolbar, msg, 0).show();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        init();
    }

    private void init(){
        toolbar = $(R.id.toolbar_scroll);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
		fab = $(R.id.fab_about);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				Snackbar.make(toolbar, "action_阿里PayPal", 0).show();
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
                                                    .getBitmap(), 16)));
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
	
	
}
