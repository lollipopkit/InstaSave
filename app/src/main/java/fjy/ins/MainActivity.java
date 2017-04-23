package fjy.ins;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import fjy.ins.*;
import fjy.ins.activity.*;
import fjy.ins.adapter.*;
import fjy.ins.model.*;
import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import android.support.v7.widget.Toolbar;
import android.support.v7.app.AlertDialog;
import android.app.*;

public class MainActivity extends AppCompatActivity
{
	private ListView listView;
	private DBManager dm;
	private List<Note> noteDataList = new ArrayList<>();
    private MyAdapter adapter;
	private FloatingActionButton fab;
	private DrawerLayout drawer;
    private NavigationView nv;
	private Toolbar tb;

	public <T extends View> T $(int i){
        return (T) super.findViewById(i);
    }

	public void Sna(String msg){
		Snackbar.make(fab, msg, 0).show();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		init();
		AcManager.getInstance().addActivity(this);
    }
	
	private void init(){
		listView = $(R.id.list);
		drawer = $(R.id.drawer_layout);
        nv = $(R.id.nav_view);
		tb = $(R.id.toolbar);

        dm = new DBManager(this);
        dm.readFromDB(noteDataList);
		updateView();

        adapter = new MyAdapter(this, noteDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new NoteClickListener());
        listView.setOnItemLongClickListener(new NoteLongClickListener());

        fab = $(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
                    final EditText et = new EditText(MainActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("(๑•̀ㅁ•́๑)✧网址");
                    builder.setView(et);
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new NetTask().execute(String.valueOf(et.getText()));
                            }
                        });     
                    builder.create().show();
				}
			});
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
			getWindow().setStatusBarColor(Color.parseColor("#10000000"));
			getWindow().setNavigationBarColor(Color.parseColor("#8594FF"));
		}
		setSupportActionBar(tb);
		setupDrawerContent(nv);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawer, tb, R.string.about, R.string.about);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

	private void updateView() {
        if (noteDataList.isEmpty()) {
			dm.addToDB(getString(R.string.intro),getString(R.string.empty),getString(R.string.now), "#00000000", "天涯海角");
			dm.readFromDB(noteDataList);
        } 
    }
	
	private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(

			new NavigationView.OnNavigationItemSelectedListener()
			{

				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem)
				{
					menuItem.setChecked(true);
					drawer.closeDrawers();
					return true;
				}
			});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this,ScrollingActivity.class));
				finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

	private class NoteClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) view.getTag();
            String content = viewHolder.tvTitle.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            intent.putExtra("path", content);
            startActivity(intent);
            finish();
        }
    }
    
    private class NoteLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
            final Note note = ((MyAdapter) adapterView.getAdapter()).getItem(i);
            if (note == null) {
                return true;
            }
            final int id = note.getId();
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("(๑•̀ㅁ•́๑)✧");
			builder.setMessage("\n确认删除此条记录？\n");
			builder.setNegativeButton("明白", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DBManager.getInstance(MainActivity.this).deleteNote(id);
						adapter.removeItem(i);
					}
				});		
			builder.create().show();
            return true;
        }
    }

	/*@Override
	protected void onNewIntent(Intent intent)
	{
		finish();
		super.onNewIntent(intent);
	}*/

	@Override
	public void onBackPressed()
	{
		Snackbar.make(fab, "点击右方按钮以退出", 0).setAction("退出", new OnClickListener(){
				@Override
				public void onClick(View v){
					finish();
					//controlService(1);
				}
			}).show();
	}
    
    private class NetTask extends AsyncTask<String, Integer, String> 
    {
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
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
            startActivity(new Intent(MainActivity.this , ImageActivity.class).putExtra("url", result));
            finish();
        }
    }
}
