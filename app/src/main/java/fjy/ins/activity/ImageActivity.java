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
import com.tomer.fadingtextview.*;
import android.transition.*;
import com.bumptech.glide.load.engine.*;
import com.bumptech.glide.request.*;
import java.util.concurrent.*;

public class ImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;
	private FadingTextView ftv;
    private ImageView iv;
    private Bitmap bm;
	private Window window;
	private static Info info;
    private String PHOTO_NAME;
    private String imgUrl;
	private String videoUrl;
    private String path;
    private String size;
	private String title;
	private String sum;
	private String PNG = ".png";
	private String MP4 = ".mp4";
	private String SD;
	private int type;
	private boolean isAlive;
    private DBManager db;
	private File savefile;
	private File f;

    public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }
	
	public static void a(Activity ma, Info inf){
		ma.startActivity(new Intent(ma, ImageActivity.class), ActivityOptions.makeSceneTransitionAnimation(ma).toBundle());
		info = inf;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewing);
		
		initData();      
		initView();
		initListener();
		initEffect();
    }
    
    private SimpleTarget target = new SimpleTarget<Bitmap>() {  
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            iv.setImageBitmap(bitmap);
            bm = bitmap;
        }
    };

    private void initData(){
		imgUrl = info.getImgUrl();
		videoUrl = info.getVideoUrl();
		title = info.getTitle();
        path = info.getPath();
		sum = info.getSum();
		type = info.getType();
		SD = Environment.getExternalStorageDirectory() + "/InstaSave/";
		PHOTO_NAME = title.replaceAll(" ", "-") + "-" + getSimpleTime();
		
		db = new DBManager(this);
    }
	
	private void initView(){
		toolbar = $(R.id.toolbar_scroll);
        toolbar.setTitleTextColor(Color.WHITE);   
		toolbar.setTitle("Instagram");
        iv = $(R.id.im_view);
		fab = $(R.id.fab_about);
        setSupportActionBar(toolbar);

		ftv = $(R.id.ftv_view);
		if(path == null){
			path = "请先保存\n保存后即可查看路径";
		}
		String[] strArray = {"作者" + "\n" + title, "详情" + "\n" + sum, "路径" + "\n" + "/sdcard/InstaSave" + "\n/" + path};
		ftv.setTexts(strArray);
		ftv.setTimeout(3000);
	}
	
	private void initEffect(){
		window = getWindow();
		if(Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT){
			window.setNavigationBarColor(Color.TRANSPARENT);
		}

        BlurKit.init(this);
        window.setBackgroundDrawable(new BitmapDrawable(
                                              BlurKit.getInstance()
                                              .blur(((BitmapDrawable)
                                                    WallpaperManager.getInstance(this)
                                                    .getDrawable())
                                                    .getBitmap(), 6)));
		window.setEnterTransition(new Explode().setDuration(500));
	}
	
	private void initListener(){
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					if(imgUrl != null){
						if(type == 0){
							loadOnlineImg();
						}else if(type == 1){
							loadOnlineVideo();
						}
					}else if(path != null){
						if(type == 0){
							loadLocalImg();
						}else if(type == 1){
							loadLocalVideo();
						}
					}
				}
			}, 200);
	}
	
	private void loadOnlineImg(){
		iv.setImageResource(R.drawable.ins_logo);
		App.Sna(toolbar, "Loading.....\nPlease wait for serveral seconds....");
		Glide.with(this).load(imgUrl).asBitmap().into(target);

		fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					switch(saveImage(bm)){
						case 0:
							db.addToDB(title, sum, getTime(), PHOTO_NAME + PNG, size, imgUrl);
							MainActivity.setChanged();
							App.Sna(toolbar, "成功下载！已保存至图库");
							sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/InstaSave/" + PHOTO_NAME))));
							break;
						case 1:
							App.Sna(toolbar, "未找到文件！File not found!");
							break;
						case 2:
							App.Sna(toolbar, "IO问题！请联系开发者\nIO Exception,Please contact me!");
							break;
						case 4:
							App.Sna(toolbar, "图片还未加载完成哦！请稍等");
					}
				}
			});
	}
	
	private void loadLocalImg(){
		f = new File(SD + path);
		if(f.exists()){
			fab.setImageDrawable(getDrawable(R.drawable.ic_share));
			fab.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v){
						if (f != null && f.exists() && f.isFile()) {
							shareImg(f);
						}
					}
				});
			Glide.with(this).load(f).skipMemoryCache(true).into(iv);
		}else{
			App.Sna(toolbar, "图片不存在\n可能您已经删除了它ʕ•ٹ•ʔ");
		}
	}
	
	private void loadLocalVideo(){
		f = new File(SD + path.replace(PNG, MP4));
		if(f.exists()){
			fab.setImageDrawable(getDrawable(R.drawable.ic_share));
			fab.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v){
						if (f != null && f.exists() && f.isFile()) {
							info.setPath(f.getAbsolutePath());
							VideoActivity.a(ImageActivity.this, info);
						}
					}
				});
			Glide.with(this).load(new File(SD + path)).skipMemoryCache(true).into(iv);
		}else{
			App.Sna(toolbar, "不存在\n可能您已经删除了它ʕ•ٹ•ʔ");
		}
	}
	
	private void loadOnlineVideo(){			
		Glide.with(this).load(imgUrl).crossFade(500).into(target);
		
		fab.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					switch(saveImage(bm)){
						case 0:
							MainActivity.setChanged();
							db.addToDB(title, sum, getTime(), PHOTO_NAME + PNG, size, imgUrl);
							sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/InstaSave/" + PHOTO_NAME))));
							break;
						case 1:
							App.Sna(toolbar, "未找到文件！File not found!");
							break;
						case 2:
							App.Sna(toolbar, "IO问题！请联系开发者\nIO Exception,Please contact me!");
							break;
						case 4:
							App.Sna(toolbar, "图片还未加载完成哦！请稍等");
					}
					final String imagePath = SD + PHOTO_NAME + MP4;
					if(!new File(imagePath).exists()){
						new Thread(new Runnable() {
								@Override
								public void run() {
									isAlive = true;
									String path =  getVideoPath(videoUrl);
									copyFile(path, imagePath);	
								}
							}).start();
						while(!isAlive){
							sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(Uri.fromFile(new File(imagePath))));
							intendActivity();
						}
					}else{
						intendActivity();
					}								
				}
			});
	}
	
	private void intendActivity(){
		info.setPath(SD + PHOTO_NAME + MP4);
		VideoActivity.a(ImageActivity.this, info);
	}
	
	private String getVideoPath(String imgUrl) {
        String path = null;
        FutureTarget<File> future = Glide.with(this)
			.load(imgUrl)
			.downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
				isAlive = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void shareImg(File file){
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		builder.detectFileUriExposure();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/jpg");
		Uri u = Uri.fromFile(file);
		intent.putExtra(Intent.EXTRA_STREAM, u);
		intent.putExtra(Intent.EXTRA_TEXT, "我从InstaSave下载了Instagram上喜欢的照片");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, "InstaSave"));
	}
	
	private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-HH:mm-E");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }
	
	private String getSimpleTime(){
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd-HH-mm");
		Date curDate = new Date();
        String str = format.format(curDate);
        return str;
	}
    
    public int saveImage(Bitmap bmp) {
		File appDir = new File(Environment.getExternalStorageDirectory(), "InstaSave");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		savefile = new File(appDir, PHOTO_NAME + PNG);
		if(bmp != null){
            try {
                FileOutputStream fos = new FileOutputStream(savefile);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                size = String.valueOf(savefile.length()/1024) + "kb";
                return 0;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
				Toast.makeText(this, e.toString(), 0).show();
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
