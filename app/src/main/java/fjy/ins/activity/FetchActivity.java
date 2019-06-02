package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.bitmap.*;
import fjy.ins.*;
import fjy.ins.model.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import android.support.v7.widget.Toolbar;
import java.util.*;
import com.flurgle.blurkit.*;
import fjy.ins.R;
import android.graphics.drawable.*;
import android.graphics.*;
import android.transition.*;
import android.view.inputmethod.*;
import android.view.View.*;
import com.github.ybq.android.spinkit.style.*;

public class FetchActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private FloatingActionButton fab;
    private ImageView iv;
	private EditText et;
	private TextInputLayout til;
	private static List<Note> noteDataList = new ArrayList<>();
	private Window window;
	
    public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }

	public void Sna(String msg){
		Snackbar.make(toolbar, msg, 0).show();
	}

	public static void a(Activity ma, List<Note> noteList){
		ma.startActivity(new Intent(ma, FetchActivity.class), ActivityOptions.makeSceneTransitionAnimation(ma).toBundle());
		noteDataList = noteList;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fetch);

		initView();
		initListener();
		initEffect();
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					til.setVisibility(0);
				}
			}, 700);
    }

	public void initView(){
		til = $(R.id.inputLayout_fetch);
		fab = $(R.id.fab_fetch);
		iv = $(R.id.iv_fetch_bg);
		et = $(R.id.et_fetch);
		toolbar = $(R.id.toolbar_fetch);
		window = getWindow();
		
		toolbar.setTitle("InstaSave");
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.parseColor("#00000000"));
		}
		
		BlurKit.init(this);
        getWindow().setBackgroundDrawable(new BitmapDrawable(
                                              BlurKit.getInstance()
                                              .blur(((BitmapDrawable)
                                                    WallpaperManager.getInstance(this)
                                                    .getDrawable())
                                                    .getBitmap(), 6)));
		
		
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					App.glideWithBg(noteDataList, iv, FetchActivity.this);
				}
			}, 50);
	}
	
	public void initListener(){
		fab.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					String str = et.getText().toString();
					if(str.contains("instagram")){
						new NetTask().execute(str);
					}else if(str != null && !str.equals("")){
						App.helpSna(toolbar, "请输入Instagram链接 ʕ•̀ω•́ʔ✧\nPlease input INS url!", FetchActivity.this);
					}else{
						App.helpSna(toolbar, "请输入链接('・ω・') \nThis element must not be null!", FetchActivity.this);
					}
					hideKeyboard();
				}
			});
	}
	
	public void initEffect(){
		window.setEnterTransition(new Explode().setDuration(500));
	}
	
	public void initInput(){
		ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		try {
			ClipData data = cm.getPrimaryClip();
			ClipData.Item item = data.getItemAt(0);
			String clipStr = item.getText().toString();
			if(clipStr.contains("instagram")){
				et.setText(item.getText());
				App.Sna(toolbar, "已自动复制内容至输入框");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void hideKeyboard(){
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken()  
												   ,InputMethodManager.HIDE_NOT_ALWAYS);  
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initInput();
	}

	@Override
	public void onBackPressed()
	{
		til.setVisibility(8);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					superBack();
				}
			}, 50);
	}
	
	private void superBack(){
		super.onBackPressed();
	}
	
	private class NetTask extends AsyncTask<String, Integer, String> 
    {
        
        @Override
        protected void onPreExecute()
        {
            MultiplePulse mp = new MultiplePulse();
			fab.setImageDrawable(mp);
			mp.start();
        }

        @Override
        protected String doInBackground(String... params) 
        {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    long total = entity.getContentLength();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int count = 0;
                    int length = -1;
                    while ((length = is.read(buf)) != -1) {
                        baos.write(buf, 0, length);
                        count += length;
                        publishProgress((int) (((count / (float) total) * 100) - 1));

                    }

                    return new String(baos.toByteArray(), "utf-8");
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) 
        {
			fab.setAlpha(100 - progresses[0]);
        }

        @Override
        protected void onPostExecute(String result) 
        {
			if(result != null & result != ""){
				String i = Praser.RegexString(result, "(?<=\"og.title\" content[=]\").*");
				String sum = Praser.RegexString(i, "(?<=“).*");
				String title = Praser.RegexString(i, ".*(?= Instagram)").replace(" on", "");
				String image = Praser.RegexString(result, "(?<=\"og.image\" content[=]\").*(?=\")");
				String video = Praser.RegexString(result, "(?<=\"og.video\" content[=]\").*(?=\")");

				Info info = new Info();
				info.setSum(sum);
				info.setTitle(title);
				if(video.equals("Nothing Found!")){
					info.setImgUrl(image);
					info.setType(0);
				}else{
					info.setImgUrl(image);
					info.setVideoUrl(video);
					info.setType(1);
				}
				if(App.isPhotoDownloaded(noteDataList, image) && !image.equals("Nothing Found!")){
					App.Sna(toolbar, "这图貌似下载过(# ﾟДﾟ)");
				}else{
					ImageActivity.a(FetchActivity.this, info);
					finish();
				}
			}else{
				App.Sna(toolbar, "Please make sure whether your network is disabled");
			}
        }
    }
}
