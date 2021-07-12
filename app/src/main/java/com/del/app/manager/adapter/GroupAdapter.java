package com.del.app.manager.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import com.del.app.manager.R;
import android.widget.TextView;
import java.io.IOException;
import com.del.app.manager.util.AppUtil;
import java.io.File;
import java.util.List;
import java.security.cert.CertificateException;
import com.del.app.manager.util.DateTimeUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.del.app.manager.util.MainUtil;
import android.graphics.Color;
import com.kaopiz.kprogresshud.KProgressHUD;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.content.Intent;
import com.del.app.manager.activity.ManifestActivity;
import android.util.Log;
import androidx.cardview.widget.CardView;
import android.widget.LinearLayout;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupVH> {
	
	
	private ArrayList<String> list = new ArrayList<>();
	private Context con;
	private String packageName, appDir;
	private ArrayList<String> listPermissions = new ArrayList<>();
	private ArrayList<String> listActivities = new ArrayList<>();
	private ArrayList<String> listServices = new ArrayList<>();
	private ArrayList<String> listReceivers = new ArrayList<>();
	private ArrayList<String> listProviders= new ArrayList<>();
	
	public GroupAdapter(Context con, ArrayList<String> list, String packagName, String appDir){
		this.con = con;
		this.list = list;
		this.packageName = packagName;
		this.appDir = appDir;
	}
	
	@Override
	public GroupVH onCreateViewHolder(ViewGroup parent, int itemType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_title_group, parent, false);
		return new GroupVH(view);
	}

	@Override
	public void onBindViewHolder(GroupVH holder, int position) {
		holder.tvTitle.setText(list.get(position));
		holder.tvTitle.setTypeface(Typeface.createFromAsset(con.getAssets(),"fonts/sans_bold.ttf"), 0);
		holder.tvValue.setTypeface(Typeface.createFromAsset(con.getAssets(),"fonts/sans_medium.ttf"), 0);
		
			switch(position){
				case 0:
					holder.tvValue.setText(AppUtil.getVersionName(con, packageName));
				break;
				    
				case 1:
					holder.tvValue.setText(String.valueOf(AppUtil.getVersionCodeApp(con, packageName)));
				break;
				
				case 2:
					holder.tvValue.setText(String.valueOf(AppUtil.getMinSdkVersion(con, packageName)));
				break;
				
				case 3:
					holder.tvValue.setText(String.valueOf(AppUtil.getMaxSdkVersion(con, packageName)));
				break;
				
				case 4:
					holder.tvValue.setText(String.valueOf(AppUtil.getUidForPackage(con, packageName)));
				break;
				
				case 5:
					holder.tvValue.setText(appDir);
				break;
				
				case 6:
					holder.tvValue.setText(AppUtil.getMainActivityAndClass(con, packageName,0));
					break;
				
				case 7:
					holder.tvValue.setText(AppUtil.bytesToMB(new File(appDir).length()));
				break;
				
				case 8:
					holder.tvValue.setText(DateTimeUtil.formatDateTime0(AppUtil.firstInstalled(con, packageName)));
				break;
				
				case 9:
					holder.tvValue.setText(DateTimeUtil.formatDateTime0(AppUtil.getLastUpdate(con, packageName)));
				break;
				
				case 10:
					holder.tvValue.setText(AppUtil.getAppSignatureSHA1(con, packageName));
				break;
				
				case 11:
					holder.tvValue.setText(AppUtil.getAppSignatureSHA256(con, packageName));
				break;
					
				case 12:
					holder.tvValue.setText(AppUtil.getAppSignatureMD5(con, packageName));
				break;
				
				case 13:
					holder.tvValue.setVisibility(View.GONE);
					holder.rvList.setVisibility(View.VISIBLE);
					try{
					listPermissions = AppUtil.getPermissionsByPackageName(con, packageName);
					holder.tvTitle.setText(list.get(position) + " ( " + listPermissions.size() + " ) ");
					holder.rvList.setHasFixedSize(true);
					holder.rvList.setLayoutManager(new LinearLayoutManager(con));
					holder.rvList.setAdapter(new MemberAdapter(listPermissions, con));
					} catch(Exception e) {
						Log.d("Error", "Failed Fetch Permisson" + e.getMessage());
						holder.tvValue.setVisibility(View.VISIBLE);
						holder.rvList.setVisibility(View.GONE);
						holder.tvValue.setText("null");
					}
				break;
				
				case 14:
					holder.tvValue.setVisibility(View.GONE);
					holder.rvList.setVisibility(View.VISIBLE);
					try{
					    listActivities = AppUtil.activityInfoList(con, appDir);
						holder.tvTitle.setText(list.get(position) + " ( " + listActivities.size() + " ) ");
						holder.rvList.setHasFixedSize(true);
						holder.rvList.setLayoutManager(new LinearLayoutManager(con));
						holder.rvList.setAdapter(new MemberAdapter(listActivities, con));
					} catch(Exception e){
						Log.d("Error", "Failed Fetch Activity" + e.getMessage());
						holder.tvValue.setVisibility(View.VISIBLE);
						holder.rvList.setVisibility(View.GONE);
						holder.tvValue.setText("null");
					}
				break;
				
				case 15:
					holder.tvValue.setVisibility(View.GONE);
					holder.rvList.setVisibility(View.VISIBLE);
					try{
					    listServices = AppUtil.getAllService(con, appDir);
						holder.tvTitle.setText(list.get(position) + " ( " + listServices.size() + " ) ");
						holder.rvList.setHasFixedSize(true);
						holder.rvList.setLayoutManager(new LinearLayoutManager(con));
						holder.rvList.setAdapter(new MemberAdapter(listServices, con));
					} catch(Exception e){
						Log.d("Error", "Failed Fetch Service" + e.getMessage());
						holder.tvValue.setVisibility(View.VISIBLE);
						holder.rvList.setVisibility(View.GONE);
						holder.tvValue.setText("null");
					}
					break;
					
				case 16:
					holder.tvValue.setVisibility(View.GONE);
					holder.rvList.setVisibility(View.VISIBLE);
					try{
					    listReceivers = AppUtil.getAllReceiver(con, appDir);
						holder.tvTitle.setText(list.get(position) + " ( " + listReceivers.size() + " ) ");
						holder.rvList.setHasFixedSize(true);
						holder.rvList.setLayoutManager(new LinearLayoutManager(con));
						holder.rvList.setAdapter(new MemberAdapter(listReceivers, con));
					} catch(Exception e){
						Log.d("Error", "Failed Fetch Receivers" + e.getMessage());
						holder.tvValue.setVisibility(View.VISIBLE);
						holder.rvList.setVisibility(View.GONE);
						holder.tvValue.setText("null");
					}
					
					break;
					
				case 17:
					holder.tvValue.setVisibility(View.GONE);
					holder.rvList.setVisibility(View.VISIBLE);
					try{
					    listProviders = AppUtil.getAllProvider(con, appDir);
						holder.tvTitle.setText(list.get(position) + " ( " + listProviders.size() + " ) ");
						holder.rvList.setHasFixedSize(true);
						holder.rvList.setLayoutManager(new LinearLayoutManager(con));
						holder.rvList.setAdapter(new MemberAdapter(listProviders, con));
					} catch(Exception e){
						Log.d("Error", "Failed Fetch Providers" + e.getMessage());
						holder.tvValue.setVisibility(View.VISIBLE);
						holder.rvList.setVisibility(View.GONE);
						holder.tvValue.setText("null");
					}
					break;
				
				case 18:
					
					holder.tvValue.setTextColor(R.color.colorLine);
					holder.tvValue.setText("Click here...");
					
				break;
				
				
			}
		
	}

	@Override
	public int getItemCount() {
		
		return list.size() < 0 ? 0 : list.size();
	}
	
	public class GroupVH extends RecyclerView.ViewHolder {
		TextView tvTitle, tvValue;
		RecyclerView rvList;
		View dividerList;
		LinearLayout bg;
		
		public GroupVH(View itemView){
			super(itemView);
			
			bg = (LinearLayout) itemView.findViewById(R.id.linear_text);
			tvTitle =(TextView) itemView.findViewById(R.id.tv_title);
			tvValue = (TextView)itemView.findViewById(R.id.tv_value);
			dividerList = (View) itemView.findViewById(R.id.divider_list);
			rvList =(RecyclerView) itemView.findViewById(R.id.rv_list);
			
			bg.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						if(tvTitle.getText() == "Manifest"){
							Intent manifestViewr = new Intent();
							manifestViewr.setAction(Intent.ACTION_VIEW);
							manifestViewr.putExtra("fileName", AppUtil.getAppName(con, packageName));
							manifestViewr.putExtra("filePath", appDir);
							manifestViewr.setClass(con, ManifestActivity.class);
							con.startActivity(manifestViewr);
							
						} else if(getAdapterPosition() == 13){
							
						} else if(getAdapterPosition() == 14){
							
						} else if(getAdapterPosition() == 15){
							
						} else if (getAdapterPosition() == 16){
							
						} else if (getAdapterPosition() == 17){
							
						} else{
							MainUtil.copyToClipBoardText(con, tvValue.getText().toString().trim());
							MainUtil.showSnackBar(v, "has been copied to clipboard");
						}
					}
				});
		}
	}

	
}
