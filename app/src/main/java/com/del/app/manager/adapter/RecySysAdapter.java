package com.del.app.manager.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import java.util.List;
import com.del.app.manager.model.ModelApk;
import java.util.ArrayList;
import android.view.LayoutInflater;
import com.del.app.manager.R;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.File;
import com.del.app.manager.util.ApkUtils;
import com.del.app.manager.util.MainUtils;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.Collection;
import android.content.Intent;
import com.del.app.manager.activity.MoreDetailActivity;

public class RecySysAdapter extends RecyclerView.Adapter<RecySysAdapter.SystemVH> implements Filterable {
	
	private Context con;
	
	private List<ModelApk> apkSys;
	private List<ModelApk> apkSysListFull;
	
	public RecySysAdapter(Context con, List<ModelApk> apkSys){
		this.con = con;
		this.apkSys = apkSys;
		this.apkSysListFull = new ArrayList<>(apkSys);
	}
	
	@Override
	public SystemVH onCreateViewHolder(ViewGroup parent, int viewType) {
		
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
		
		return new SystemVH(v);
	}

	@Override
	public void onBindViewHolder(SystemVH holder, int position) {
		final ModelApk mApk = apkSys.get(position);
		
		holder.appName.setText(mApk.getName());
		holder.appName.setSelected(true);
		holder.verName.setText("V." +mApk.getvName());
		File f = new File(mApk.getDir());
		holder.appSize.setText(ApkUtils.bytesToMB(f.length()));
		holder.imageView.setImageDrawable(mApk.getIcon());

		holder.bg.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    MainUtils.showPopMenu(con, v, mApk.getPackageName(), mApk.getDir());
                    return true;
                }
            });
            
        holder.bg.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent in = new Intent();
                    in.setClass(con, MoreDetailActivity.class);
                    in.putExtra("packageName", mApk.getPackageName());
                    in.putExtra("appDirectory", mApk.getDir());
                    con.startActivity(in);
                }
			});
            
	}

	@Override
	public int getItemCount() {
		return apkSys.size();
	}
	
	public class SystemVH extends RecyclerView.ViewHolder {
		
		TextView appName, verName, appSize;
		ImageView imageView;
		LinearLayout bg;
		
		public SystemVH(View itemView){
			super(itemView);
			
			appName = (TextView)itemView.findViewById(R.id.app_name);
			verName = (TextView)itemView.findViewById(R.id.ver_name);
			appSize = (TextView)itemView.findViewById(R.id.size_name);
			imageView =(ImageView)itemView.findViewById(R.id.image_view);
			bg = (LinearLayout)itemView.findViewById(R.id.bg_item);
			
		}
	}
    
	@Override
	public Filter getFilter() {
		return filter;
	}
	Filter filter = new Filter(){

		@Override
		protected Filter.FilterResults performFiltering(CharSequence charSeq) {
			List<ModelApk> filterList = new ArrayList<>();

			if(charSeq == null || charSeq.length() == 0){
				filterList.addAll(apkSysListFull);
			} else{
				String query = charSeq.toString().toLowerCase().trim();

				for(ModelApk listItem : apkSysListFull){
					if(listItem.getName().toLowerCase().contains(query)){
						filterList.add(listItem);
					}
				}
			}

			FilterResults results = new FilterResults();
			results.values = filterList;
			return results;
		}

		@Override
		protected void publishResults(CharSequence charSeq, Filter.FilterResults results) {
			apkSys.clear();
			apkSys.addAll((Collection<? extends ModelApk>) results.values);
			notifyDataSetChanged();
		}
	};
}
