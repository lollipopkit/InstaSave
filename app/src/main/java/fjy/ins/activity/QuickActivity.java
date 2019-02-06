package fjy.ins.activity;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import fjy.ins.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import fjy.ins.model.*;
import java.util.*;

public class QuickActivity extends Activity
{

	private EditText et;
	private List<Note> note = new ArrayList<>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		DBManager dm = new DBManager(this);
		dm.readFromDB(note);
        
        et = new EditText(QuickActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickActivity.this);
        builder.setTitle("(๑•̀ㅁ•́๑)✧网址");
        builder.setView(et);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener(){

                @Override
                public void onDismiss(DialogInterface p1)
                {
                    finish();
                }
            });
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = String.valueOf(et.getText());
                    if(str != null && !str.equals("") && str.contains("instagram")){
                        new NetTask().execute(str);
                    }else{
                        Toast.makeText(QuickActivity.this,"请输入zhen que de网址('・ω・') \nThis element must not be wrong!", 0).show();
                    }
                }
            });     
        builder.create().show();
    }

    private class NetTask extends AsyncTask<String, Integer, String> 
    {
        ProgressDialog pd = new ProgressDialog(QuickActivity.this);
        @Override
        protected void onPreExecute()
        {
            pd.setTitle(getString(R.string.pd_title));
            pd.setMessage(getString(R.string.pd_msg));
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
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

                    return Praser.RegexString(new String(baos.toByteArray(), "utf-8"), "(?<=\"og.image\" content[=]\").*(?=\")");
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) 
        {
            pd.setMessage(getString(R.string.pd_msg) + getString(R.string.pd_pro) + progresses[0] + "%");
        }

        @Override
        protected void onPostExecute(String result) 
        {
            pd.dismiss();
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
				if(App.isPhotoDownloaded(note, image)){
					App.Sna(et,"这图貌似下载过(# ﾟДﾟ)");
				}else{
					ImageActivity.a(QuickActivity.this, info);
					finish();
				}
			}else{
				App.Sna(et, "Please make sure whether your network is disabled");
			}
        }
    }
}
