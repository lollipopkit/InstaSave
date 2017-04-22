package fjy.ins.activity;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.*;
import android.media.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;

import android.support.v7.app.AlertDialog;
import fjy.ins.R;
import fjy.ins.MainActivity;
import com.flurgle.blurkit.*;
import android.graphics.*;

public class SplashActivity extends Activity
{
	private Handler handler = new Handler();
	
	String[] PER_ALL = {"android.permission.WRITE_EXTERNAL_STORAGE"};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		BlurKit.init(this);
		getWindow().setBackgroundDrawable(new BitmapDrawable(
		BlurKit.getInstance()
				.blur(((BitmapDrawable)
		WallpaperManager.getInstance(this)
				        .getDrawable())
				        .getBitmap(), 20)));
						
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().setNavigationBarColor(Color.TRANSPARENT);
		}
																						
		if(needPm()){
			Notice();
		}
		else{
			LoadAc();
		}
	}
	
	private void PmRequest(String[] PER_NAME, int PER_CODE){
		requestPermissions(PER_NAME, PER_CODE);
    }
	private boolean needPm(){
		if (Build.VERSION.SDK_INT >= 23){
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
				return true;
			}
        }
		return false;
	}
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch (permsRequestCode){
            case 666:
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
					LoadAc();
				}else{
					finish();
				}
		}
	}
	
	private void Notice(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("(๑•̀ㅁ•́๑)✧");
		builder.setMessage("\n自Android6.0，由于新增权限API\n\n若要正常使用随记，请接受以下权限申请\n");
		builder.setNegativeButton("明白", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PmRequest(PER_ALL,666);
				}
			});

		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		builder.create().show();
	}
	
	private void LoadAc(){
		handler.postDelayed(new Runnable(){
				public void run(){
					startActivity(new Intent(SplashActivity.this,MainActivity.class));
					finish();
				}
			}
		,377);		
	}
}
