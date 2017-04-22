package fjy.ins.activity;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import fjy.ins.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import android.support.v7.widget.Toolbar;

public class EditNoteActivity extends ActionBarActivity 
{
    private EditText titleEt;
    private FloatingActionButton fab_save;
	private Toolbar tb;
	
	
	public <T extends View> T $(int i){
		return (T) super.findViewById(i);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
		AcManager.getInstance().addActivity(this);
    }
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //初始化
    private void init() {
		
        fab_save = $(R.id.save);
		titleEt = $(R.id.et_title);
		tb = $(R.id.toolbar);
		
		setSupportActionBar(tb);
        
        fab_save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//new NetTask().execute(String.valueOf(titleEt.getText()));
			}
		});
        
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			getWindow().setNavigationBarColor(Color.parseColor("#8594FF"));
		}
	}

    @Override
    public void onBackPressed() {
		Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
		startActivity(intent);
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
	}
    
    
}
