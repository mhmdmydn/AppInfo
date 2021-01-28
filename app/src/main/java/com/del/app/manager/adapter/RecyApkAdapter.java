package com.del.app.manager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.del.app.manager.R;
import com.del.app.manager.activity.MoreDetailActivity;
import com.del.app.manager.model.ModelApk;
import com.del.app.manager.util.ApkUtils;
import com.del.app.manager.util.MainUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyApkAdapter extends RecyclerView.Adapter<RecyApkAdapter.ApkVH> implements Filterable {
	
	private List<ModelApk> apk;
	private List<ModelApk> apkListFull;
	private Context context;
	
	public RecyApkAdapter(Context context, List<ModelApk> apk){
		this.context = context;
		this.apk = apk;
		this.apkListFull = new ArrayList<>(apk);
	}
	
	@Override
	public ApkVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
		return new ApkVH(view);
	}

	@Override
	public void onBindViewHolder(ApkVH holder, int pos) {
		final ModelApk mApk = apk.get(pos);
		
		holder.appName.setText(mApk.getName());
		holder.appName.setSelected(true);
		holder.verName.setText("V." +mApk.getvName());
		File f = new File(mApk.getDir());
		holder.appSize.setText(ApkUtils.bytesToMB(f.length()));
		holder.imageView.setImageDrawable(mApk.getIcon());
        
        holder.bg.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    MainUtils.showPopMenu(context, v, mApk.getPackageName(), mApk.getDir());
                    return true;
                }
            });
		
		holder.bg.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent in = new Intent();
                    in.setClass(context, MoreDetailActivity.class);
                    in.putExtra("packageName", mApk.getPackageName());
                    in.putExtra("appDirectory", mApk.getDir());
                    context.startActivity(in);
				}
			});
	}

	@Override
	public int getItemCount() {
		return apk.size();
	}
	
    
	class ApkVH extends RecyclerView.ViewHolder{
		
		TextView appName, verName, appSize;
		ImageView imageView;
		LinearLayout bg;
		
		public ApkVH(View itemView){
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
				filterList.addAll(apkListFull);
			} else{
				String query = charSeq.toString().toLowerCase().trim();
				
				for(ModelApk listItem : apkListFull){
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
			apk.clear();
			apk.addAll((Collection<? extends ModelApk>) results.values);
			notifyDataSetChanged();
		}
	};
} 
