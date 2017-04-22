package fjy.ins.activity;

import android.content.*;
import android.graphics.*;
import android.net.*;
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
import android.app.*;

public class ImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;
    private ImageView iv;
    private Bitmap bm;
    private String PHOTO_NAME;
    private ProgressDialog pd;
    private String url;
    private String path;

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
            pd.dismiss();
            iv.setImageBitmap(bitmap);
            bm = bitmap;
        }
    };

    private void init(){
        toolbar = $(R.id.toolbar_scroll);
        toolbar.setTitleTextColor(Color.WHITE);   
        iv = $(R.id.im_view);
        setSupportActionBar(toolbar);
        
        url = getIntent().getExtras().getString("url", null);
        path = getIntent().getExtras().getString("path", null);
        if(url != null){
            fab = $(R.id.fab_about);
            fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch(saveImage(bm)){
                            case 0:
                                Snackbar.make(toolbar, "成功下载！已保存至图库", 0).show();
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/InstaSave/" + PHOTO_NAME))));
                                break;
                            case 1:
                                Snackbar.make(toolbar, "未找到文件！File not found!", 0).show();
                                break;
                            case 2:
                                Snackbar.make(toolbar, "IO问题！请联系开发者\nIO Exception,Please contact me!", 0).show();
                                break;
                            case 4:
                                Snackbar.make(toolbar, "图片还未加载完成哦！请稍等", 0).show();
                        }
                    }
            });

            Glide.with(this).load(url).asBitmap().into(target);
            pd = new ProgressDialog(this);
            pd.setTitle("ʕ•̀ω•́ʔ✧");
            pd.setMessage("图片加载中！\n建议使用VPN（host较慢）");
            //pd.setCancelable(false);
            pd.show();
        }else if(path != null){
            Glide.with(this).load(new File(Environment.getExternalStorageDirectory() + path + ".png")).crossFade().into(iv);
        }
        
		if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT){
			getWindow().setNavigationBarColor(Color.parseColor("#8594FF"));
		}
    }
    
    public int saveImage(Bitmap bmp) {
		File appDir = new File(Environment.getExternalStorageDirectory(), "InstaSave");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		PHOTO_NAME = System.currentTimeMillis() + ".png";
		File file = new File(appDir, PHOTO_NAME);
		if(bmp != null){
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
        }else{
            return 4;
        }
	}
}
