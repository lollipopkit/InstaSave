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

public class QuickActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        final EditText et = new EditText(QuickActivity.this);
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
                    if(str != null && !str.equals("")){
                        new NetTask().execute(str);
                    }else{
                        Toast.makeText(QuickActivity.this,"请输入网址('・ω・') \nThis element must not be null!", 0).show();
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
            startActivity(new Intent(QuickActivity.this , ImageActivity.class).putExtra("url", result));
            finish();
        }
    }
}
