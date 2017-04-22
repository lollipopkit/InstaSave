package fjy.ins.activity;

import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.request.animation.*;
import com.bumptech.glide.request.target.*;
import fjy.ins.*;
import java.io.*;

import android.support.v7.widget.Toolbar;

public class ImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;
    private ImageView iv;
    private Bitmap bm;

    public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }
	
	public void Sna(String msg){
		Snackbar.make(toolbar, msg, 0).show();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewing);
        init();
		AcManager.getInstance().addActivity(this);
    }
    
    private SimpleTarget target = new SimpleTarget<Bitmap>() {  
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            // do something with the bitmap
            // for demonstration purposes, let's just set it to an ImageView
            iv.setImageBitmap(bitmap);
            bm = bitmap;
        }
    };

    private void init(){
        toolbar = $(R.id.toolbar_scroll);
        toolbar.setTitleTextColor(Color.WHITE);
        
        setSupportActionBar(toolbar);
        
		fab = $(R.id.fab_about);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				
				switch(saveImage(bm)){
					case 0:
						Snackbar.make(toolbar, "Success!", 0).show();
						break;
					case 1:
						Snackbar.make(toolbar, "File not found!", 0).show();
						break;
					case 2:
						Snackbar.make(toolbar, "IO Expecption trigered", 0).show();
						break;
				}
            }
        });
        
        iv = $(R.id.im_view);
        
        Glide.with(this).load(getIntent().getExtras().getString("url", null)).asBitmap().into(target);
        
        //new ImageTask().execute();
        
		if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT){
			getWindow().setNavigationBarColor(Color.parseColor("#8594FF"));
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
			//case R.id.action_web:
				
		}
        return super.onOptionsItemSelected(item);
    }
	
    public static int saveImage(Bitmap bmp) {
		File appDir = new File(Environment.getExternalStorageDirectory(), "InstaSave");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".png";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			return 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}
	}
}
