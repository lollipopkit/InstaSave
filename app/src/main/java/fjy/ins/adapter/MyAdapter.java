package fjy.ins.adapter;

import android.content.*;
import android.view.*;
import android.widget.*;
import fjy.ins.*;
import fjy.ins.activity.*;
import fjy.ins.model.*;
import java.util.*;

import fjy.ins.R;
import android.support.v7.widget.*;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;

    public MyAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    public void removeAllItem() {
        notes.clear();
        notifyDataSetChanged();
    }

    //从List移除对象
    public void removeItem(int position) {
        notes.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder;
        if (convertView == null) {
            convertView
                    = LayoutInflater.from(context).inflate(R.layout.notes_row, null);
            viewHolder = new ViewHolder();
            viewHolder.tvId
                    = (TextView) convertView.findViewById(R.id.note_id);
            viewHolder.tvTitle
                    = (TextView) convertView.findViewById(R.id.note_title);
            viewHolder.tvContent
                    = (TextView) convertView.findViewById(R.id.note_content);
            viewHolder.tvTime
                    = (TextView) convertView.findViewById(R.id.note_time);
			viewHolder.tvLoc
			        =(TextView) convertView.findViewById(R.id.note_loc);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvId.setText(notes.get(position).getId() + "");
        viewHolder.tvTitle.setText(notes.get(position).getTitle());
        viewHolder.tvContent.setText(notes.get(position).getContent());
        viewHolder.tvTime.setText(notes.get(position).getTime());
		viewHolder.tvLoc.setText(notes.get(position).getLoc());
		
		/*final int id = notes.get(position).getId();
		final SlideLayout itemView = (SlideLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, parent, false);
		RelativeLayout cv = (RelativeLayout)itemView.findViewById(R.id.card_layout);
		cv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemView.getSlideState() == MiniSlideRightLayout.STATE_OPEN) {
						itemView.smoothCloseSlide();
					} else {
						Toast.makeText(context, "000", 0).show();
						Intent intent = new Intent(context, EditNoteActivity.class);
						intent.putExtra("id", id);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
					}
				}
			});
		TextView slide = (TextView)itemView.findViewById(R.id.tv_slide);
        slide.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new MaterialDialog.Builder(context)
						.content(R.string.are_you_sure)
						.positiveText(R.string.delete)
						.negativeText(R.string.cancel)
						.callback(new MaterialDialog.ButtonCallback() {
							@Override
							public void onPositive(MaterialDialog dialog) {
								
								DBManager.getInstance(context).deleteNote(id);
								new MyAdapter(context, notes).removeItem(position);
								updateView();
							}
						}
					).show();
				}
			});*/
			
        return convertView;
    }
	
	private void updateView(){
		DBManager dm = new DBManager(context);
		dm.readFromDB(notes);
	}

    //ViewHolder内部类
    public static class ViewHolder {
        public TextView tvId;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvTime;
		public TextView tvLoc;
    }
}
