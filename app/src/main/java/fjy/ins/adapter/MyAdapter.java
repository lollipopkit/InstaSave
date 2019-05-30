package fjy.ins.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import java.util.*;
import fjy.ins.R;
import android.widget.*;
import android.content.*;
import fjy.ins.model.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.resource.bitmap.*;
import fjy.ins.*;


/**
 * Created by 青青-子衿 on 2018/1/15.
 */


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

	private View view;
	Context context;
	List<Note> notes;
	private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
	

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		view = View.inflate(parent.getContext(), R.layout.notes_row, null);
		return new MyViewHolder(view);
	}
	
	public MyAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }
	
	public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
	
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
	
	public void removeItem(int position){
		notes.remove(position);
		notifyDataSetChanged();
	}
	
	public Note getItem(int position){
		return notes.get(position);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, int position) {
		Note not = notes.get(position);

        Glide.with(context)
            .load("/sdcard/InstaSave/" + not.getPath())
			.thumbnail(0.1f)
			.crossFade(600)
			.transform(new CenterCrop(context), new GlideRoundTransform(context,8))
            .into(holder.iv);

        holder.tvId.setText(not.getId() + "");
        holder.tvTitle.setText(not.getTitle());
        holder.tvContent.setText(not.getContent());
        holder.tvTime.setText(not.getTime());
		holder.tvSize.setText(not.getSize());
		if(mOnItemClickListener != null){
			//为ItemView设置监听器
			holder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = holder.getLayoutPosition(); // 1
						mOnItemClickListener.onItemClick(holder.itemView,position); // 2
					}
				});
		}
		if(mOnItemLongClickListener != null){
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						int position = holder.getLayoutPosition();
						mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
						//返回true 表示消耗了事件 事件不会继续传递
						return true;
					}
				});
		}
	}

	@Override
	public int getItemCount() {
		return notes.size();
	}

	/**
	 * 设置viewHolder
	 */

	public class MyViewHolder extends RecyclerView.ViewHolder {

		public ImageView iv;
		public TextView tvId;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvTime;
		public TextView tvSize;

		public MyViewHolder(View itemView) {
			super(itemView);
			//初始化控件
			iv = (ImageView) view.findViewById(R.id.iv_card);
			tvId = (TextView) view.findViewById(R.id.note_id);
            tvTitle = (TextView) view.findViewById(R.id.note_title);
            tvContent = (TextView) view.findViewById(R.id.note_content);
            tvTime = (TextView) view.findViewById(R.id.note_time);
			tvSize =(TextView) view.findViewById(R.id.note_size);
		}
	}
}

