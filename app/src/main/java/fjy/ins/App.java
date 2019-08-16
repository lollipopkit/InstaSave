package fjy.ins;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.bitmap.*;
import fjy.ins.activity.*;
import fjy.ins.model.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import android.net.*;

public class App extends Application 
{ 
    private static Context context;
	
    @Override
	public void onCreate() { 
	    context = getApplicationContext();
	} 
	
	public static void glideWithBg(List<Note> noteDataList, ImageView iv,Activity act){
		File test;
		for(int i = noteDataList.size() - 1;i >= 0;i--){
			test = new File("/sdcard/InstaSave/" + noteDataList.get(i).getPath());
			if(test.exists()){
				Glide.with(act)
					.load(test)
					.crossFade(600)
					.transform(new CenterCrop(act), new GlideRoundTransform(act, 12))
					.skipMemoryCache(true)
					.into(iv);
				break;
			}
		}
	}
	
	public static void Sna(View v, String sna){
		Snackbar.make(v, sna, 0).show();
	}
	
	public static void helpSna(View v, String str, final Activity ac){
		Snackbar.make(v, str, Snackbar.LENGTH_SHORT)
			.setAction("帮助", new View.OnClickListener(){
				@Override
				public void onClick(View v1){
					Uri uri = Uri.parse("https://lollipopkit.github.io/2019/08/16/post-instasavehelp/");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					ac.startActivity(intent);
				}
			}).show();
	}
	
	public static String extractDrawable(Context context, int id) 
	{
        String imagePath = "/sdcard/InstaSave/BG.jpg";
        File file = new File(imagePath);

        try {

            if (!file.exists()) {
                file.createNewFile();
            }
			Bitmap pic = BitmapFactory.decodeResource(context.getResources(), id);
			FileOutputStream fos = new FileOutputStream(file);
			pic.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();

        } catch(Throwable t) {
            t.printStackTrace();
            imagePath = null;
        }
		return imagePath;
    }
	
	public static boolean isPhotoDownloaded(List<Note> noteDataList, String image){
		for(int test = noteDataList.size() - 1; test > 0; test--){
			if(noteDataList.get(test).getUrl().equals(image)){
				return true;
			}
		}
		return false;
	}
	
	public static String getProcessName(int pid) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
			String processName = reader.readLine();
			if (!TextUtils.isEmpty(processName)) {
				processName = processName.trim();
			}
			return processName;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return null;
	}
	
	public static Context getContext(){ 
	    return context;
	}  
	
	public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();  //返回可用于储存此位图像素的最小字节数

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        bitmap.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区

        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];

        int count = rgba.length / 4;

        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {

            pixels[i * 3] = rgba[i * 4];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B

        }
        return pixels;
    }
}
