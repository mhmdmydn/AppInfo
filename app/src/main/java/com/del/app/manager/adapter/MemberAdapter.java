package com.del.app.manager.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import java.util.ArrayList;
import android.view.LayoutInflater;
import com.del.app.manager.R;
import android.widget.TextView;
import com.del.app.manager.util.MainUtil;
import android.widget.LinearLayout;
import android.graphics.Typeface;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberVH> {
	
	private ArrayList<String> list = new ArrayList<>();
	private Context con;
	
	public MemberAdapter(ArrayList<String> list, Context con){
		this.list = list;
		this.con = con;
	}
	
	@Override
	public MemberVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_detail, parent, false);
		return new MemberVH(view);
	}

	@Override
	public void onBindViewHolder(final MemberVH holder,final  int position) {
		holder.tvMember.setText(list.get(position));
		holder.tvMember.setTypeface(Typeface.createFromAsset(con.getAssets(),"fonts/sans_medium.ttf"), 0);
		holder.llLayout.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					MainUtil.copyToClipBoardText(con, list.get(position));
					MainUtil.showSnackBar(v, "has been copied to clipboard");
				}
			});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}
	
	public class MemberVH extends RecyclerView.ViewHolder{
		TextView tvMember;
		LinearLayout llLayout;
		public MemberVH(View itemView){
			super(itemView);
			tvMember = itemView.findViewById(R.id.tv_member);
			llLayout = itemView.findViewById(R.id.ll_member);
		}
		
	}
}
