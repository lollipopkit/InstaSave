package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
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
import com.flurgle.blurkit.*;
import fjy.ins.*;
import fjy.ins.model.*;
import java.io.*;
import java.text.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import fjy.ins.R;

public class ImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;
    private ImageView iv;
    private Bitmap bm;
    private String PHOTO_NAME;
    private ProgressDialog pd;
    private String url;
    private String path;
    private String size;
    private boolean directback;
    private DBManager db;
	private File f;

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
		fab = $(R.id.fab_about);
        setSupportActionBar(toolbar);
        
        db = new DBManager(this);
        Bundle b = getIntent().getExtras();
        url = b.getString("url", null);
        path = b.getString("path", null);
        
        
        if(url != null){
            fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch(saveImage(bm)){
                            case 0:
                                db.addToDB(PHOTO_NAME, url, getTime(), "$^@&#^#&#", size);
                                Snackbar.make(toolbar, "成功下载！已保存至图库", 0).show();
                                directback = false;
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
            f = new File(Environment.getExternalStorageDirectory() + "/InstaSave/" + path);
            if(f.exists()){
				fab.setImageDrawable(getDrawable(R.drawable.ic_share));
				fab.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View v){
							if (f != null && f.exists() && f.isFile()) {
								Intent intent = new Intent(Intent.ACTION_SEND);
								intent.setType("image/jpg");
								Uri u = Uri.fromFile(f);
								intent.putExtra(Intent.EXTRA_STREAM, u);
								intent.putExtra(Intent.EXTRA_TEXT, "我从InstaSave下载了Instagram上喜欢的照片");
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(Intent.createChooser(intent, "InstaSave"));
							}
						}
				});
                Glide.with(this).load(f).into(iv);
            }else{
                Snackbar.make(toolbar, "图片不存在\n可能您已经删除了它ʕ•ٹ•ʔ", 0).show();
            }
        }
        
		if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT){
			getWindow().setNavigationBarColor(Color.TRANSPARENT);
		}
        
        BlurKit.init(this);
        getWindow().setBackgroundDrawable(new BitmapDrawable(
                                              BlurKit.getInstance()
                                              .blur(((BitmapDrawable)
                                                    WallpaperManager.getInstance(this)
                                                    .getDrawable())
                                                    .getBitmap(), 16)));
    }
	
	private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm E");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
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
                size = String.valueOf(file.length()/1024) + "kb";
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
