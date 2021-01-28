package com.del.app.manager.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import com.del.app.manager.R;
import android.widget.TextView;
import net.dongliu.apk.parser.ApkFile;
import java.io.IOException;
import net.dongliu.apk.parser.bean.ApkMeta;
import com.del.app.manager.util.ApkUtils;
import java.io.File;
import net.dongliu.apk.parser.ApkParser;
import java.util.List;
import net.dongliu.apk.parser.bean.CertificateMeta;
import net.dongliu.apk.parser.bean.ApkSignStatus;
import java.security.cert.CertificateException;
import com.del.app.manager.util.DateTimeUtil;
import net.dongliu.apk.parser.bean.Permission;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.del.app.manager.util.MainUtils;
import android.graphics.Color;
import com.kaopiz.kprogresshud.KProgressHUD;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupVH> {
	
	
	private ArrayList<String> list = new ArrayList<>();
	private Context con;
	private String packageName, appDir;
	private ArrayList<String> listPerm = new ArrayList<>();
	private ArrayList<String> listActi = new ArrayList<>();
	
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
		try {
			final ApkFile apkFile = new ApkFile(appDir);
			final ApkMeta apkMeta = apkFile.getApkMeta();
			
			switch(position){
				case 0:
					holder.tvValue.setText(apkMeta.getVersionName());
				break;
				    
				case 1:
					holder.tvValue.setText(apkMeta.getVersionCode().toString());
				break;
				
				case 2:
					holder.tvValue.setText(apkMeta.getMinSdkVersion());
				break;
				
				case 3:
					holder.tvValue.setText(apkMeta.getTargetSdkVersion());
				break;
				
				case 4:
					holder.tvValue.setText(String.valueOf(ApkUtils.getUidForPackage(con, packageName)));
				break;
				
				case 5:
					holder.tvValue.setText(appDir);
				break;
				
				case 6:
					holder.tvValue.setText(ApkUtils.bytesToMB(new File(appDir).length()));
				break;
				
				case 7:
					holder.tvValue.setText(DateTimeUtil.formatDateTime0(ApkUtils.firstInstalled(con, packageName)));
				break;
				
				case 8:
					holder.tvValue.setText(DateTimeUtil.formatDateTime0(ApkUtils.getLastUpdate(con, packageName)));
				break;
				
				case 9:
					listPerm = ApkUtils.getPermissionsByPackageName(con, packageName);
					
					LinearLayoutManager layoutManagerPerm = new LinearLayoutManager(con);

					holder.rvList.setLayoutManager(layoutManagerPerm);

					MemberAdapter mAdapter = new MemberAdapter(listPerm, con);

					holder.rvList.setAdapter(mAdapter);
					holder.rvList.setHasFixedSize(true);
					
				break;
				
				case 10:
					try{
					listActi = ApkUtils.activityInfoList(con, appDir);
					LinearLayoutManager layoutManagerActivity = new LinearLayoutManager(con);

					holder.rvList.setLayoutManager(layoutManagerActivity);

					MemberAdapter aAdapter = new MemberAdapter(listActi, con);

					holder.rvList.setAdapter(aAdapter);

					holder.rvList.setHasFixedSize(true);
					} catch(Exception e){
						
					}
				break;
				
				case 11:
					holder.tvValue.setTextColor(Color.parseColor("#2196F3"));
					holder.tvValue.setText("Click here...");
					
					holder.tvValue.setOnClickListener(new View.OnClickListener(){

							@Override
							public void onClick(View v) {
								try {
									showDialogCode(apkFile.getManifestXml());
								} catch (IOException e) {
									showDialogCode(e.toString());
								}
							}
						});
				break;
			}
		} catch (IOException e) {
			
		}
		
	}

	@Override
	public int getItemCount() {
		
		return list.size();
	}
	
	public class GroupVH extends RecyclerView.ViewHolder {
		TextView tvTitle, tvValue;
		RecyclerView rvList;
		
		public GroupVH(View itemView){
			super(itemView);
			tvTitle = itemView.findViewById(R.id.tv_title);
			tvValue = itemView.findViewById(R.id.tv_value);
			
			rvList = itemView.findViewById(R.id.rv_list);
			
			tvValue.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						MainUtils.copyToClipBoardText(con, tvValue.getText().toString().trim());
					}
				});
		}
	}
	
	private void showDialogCode(final String text){
		//Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(con);
		
		builder.setTitle("Manifes Viewer");
		builder.setMessage(text);
		builder.setPositiveButton("Copy", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					MainUtils.copyToClipBoardText(con, text);
				}
			});
		
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
        //finally creating the alert dialog and displaying it 
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
	}
	
}
