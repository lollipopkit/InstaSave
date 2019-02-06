package fjy.ins.fragment;

import android.content.*;
import android.os.*;
import android.preference.*;
import fjy.ins.*;
import fjy.ins.model.*;

import fjy.ins.R;
import android.support.v7.app.*;

public class PrefFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
	private Preference pref;

	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		if(p1 == pref){
			AlertDialog alertDialog = new AlertDialog.Builder(getContext()).
			setTitle(getString(R.string.clean)).
			setMessage(getString(R.string.clean_di)).
			setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for (int id = 0; id < 100; id++){
						DBManager.getInstance(getContext()).deleteNote(id);
					}
					
					AcManager.getInstance().exit();
					
					final Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.create();
			alertDialog.show();
		}
		return false;
	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
		
		pref = (Preference)findPreference("pref_clean");
		pref.setOnPreferenceClickListener(this);
    }
}
