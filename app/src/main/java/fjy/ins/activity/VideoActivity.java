package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.flurgle.blurkit.*;
import com.tomer.fadingtextview.*;
import fjy.ins.*;
import fjy.ins.model.*;
import java.io.*;

import fjy.ins.R;

public class VideoActivity extends AppCompatActivity
{
	private static Info info;
	private static String videoPath;
	//private String title;
	//private String sum;
	//private String path;
	private VideoView vv;
	//private FadingTextView ftv;
	private FloatingActionButton fab;
	private MediaController mc;
	private Window window;
	
	public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }

	public static void a(ImageActivity ia, Info in){
		ia.startActivity(new Intent(ia, VideoActivity.class));
		info = in;
	}
	
	@Override
	protected void onCreate(Bundle bundlesaved){
		super.onCreate(bundlesaved);
		setContentView(R.layout.activity_video);
		
		initObject();
		initData();
		initListener();
		initEffect();
		initView();
	}
	
	private void initObject(){
		vv = $(R.id.vv);
		//ftv = $(R.id.ftv_view);
		fab = $(R.id.fab_video);
		mc = new MediaController(this);
	}
	
	private void initData(){
		videoPath = info.getPath();
		//title = info.getTitle();
		//sum = info.getSum();
		//path = info.getPath();
		//String[] strArray = {"作者" + "\n" + title, "详情" + "\n" + sum, "路径" + "\n" + path};
		//ftv.setTexts(strArray);
		//ftv.setTimeout(3000);
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
	}
	
	private void initView(){
		vv.setVideoPath(videoPath);
		vv.setMediaController(mc);
		vv.seekTo(0);
		vv.requestFocus();
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					vv.start();
				}
			}, 300);
	}
	
	private void initListener(){
		fab.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					shareVideo(new File(videoPath));
				}
			});
	}
	
	private void shareVideo(File file){
		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		builder.detectFileUriExposure();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("audio/*");
		Uri u = Uri.fromFile(file);
		intent.putExtra(Intent.EXTRA_STREAM, u);
		intent.putExtra(Intent.EXTRA_TEXT, "我从InstaSave下载了Instagram上喜欢的video");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, "InstaSave"));
	}
}
