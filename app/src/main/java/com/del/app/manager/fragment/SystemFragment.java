package com.del.app.manager.fragment;

import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.del.app.manager.R;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import java.util.List;
import com.del.app.manager.model.AppModel;
import java.util.Collections;
import java.util.Comparator;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.animation.LayoutAnimationController;
import android.view.animation.AnimationUtils;
import com.del.app.manager.util.MainUtil;
import android.content.Intent;
import com.del.app.manager.activity.SettingActivity;
import android.app.Activity;
import com.del.app.manager.util.SharedPref;
import android.graphics.Color;
import com.del.app.manager.adapter.AppAdapter;
import com.del.app.manager.loader.AppTask;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

public class SystemFragment extends BaseFragment {
	
    private RecyclerView mRecyclerview;
	private SwipeRefreshLayout swipeRefreshLayout;
	private AppAdapter  adapter;
	private boolean isSystem = true;
	private ProgressBar pb;
	private RelativeLayout linearUnityAds;
	private BannerView bottomBanner;
	
	public static SystemFragment newInstance(boolean z){
		SystemFragment sysFragment = new SystemFragment();
		Bundle args = new Bundle();
		args.putBoolean("isSystem", z);
		sysFragment.setArguments(args);
		return sysFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(getArguments() != null){
			this.isSystem = getArguments().getBoolean("isSystem", this.isSystem);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_system, container, false);
		initView(v);
		initLogic(v);
		initListener(v);
		return v;
		
	}
	@Override
	public void initView(View v) {
		mRecyclerview = (RecyclerView)v.findViewById(R.id.recycler_view_system);
		swipeRefreshLayout =(SwipeRefreshLayout)v.findViewById(R.id.swipe_system); 
		pb = (ProgressBar)v.findViewById(R.id.progress_bar);
		linearUnityAds = (RelativeLayout)v.findViewById(R.id.banner_ads_view);
	}


	@Override
	public void initLogic(View v) {
		bottomBanner = new BannerView(getActivity(), getResources().getString(R.string.unity_banner), new UnityBannerSize(320, 50));
        linearUnityAds.addView(bottomBanner);
        bottomBanner.load();
		loadAppSystem();
	}

	@Override
	public void initListener(View v) {
		swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.RED);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { 
				@Override 
				public void onRefresh() { 
					new Handler().postDelayed(new Runnable() { 
							@Override 
							public void run() { 
								swipeRefreshLayout.setRefreshing(false); 
								loadAppSystem();
							} 
						}, 2000); 
				} 
			}); 
	}
	
	private void loadAppSystem(){
		mRecyclerview.setVisibility(View.GONE);
		AppTask appTask = new AppTask(getActivity(), this.isSystem, pb, new AppTask.TaskListenter(){

				@Override
				public void onTaskResult(List<AppModel> list) {
					Collections.sort(list, new Comparator<AppModel>() {
							@Override
							public int compare(AppModel lhs, AppModel rhs) {
								return lhs.getName().compareTo(rhs.getName());
							}
						});
					if (getActivity() != null) {
						mRecyclerview.setVisibility(View.VISIBLE);
						mRecyclerview.setHasFixedSize(true);
						GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
						adapter = new AppAdapter(getActivity(), list);
						mRecyclerview.setLayoutManager(layoutManager);
						mRecyclerview.setAdapter(adapter);
						LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_anim_bottom); 
						mRecyclerview.setLayoutAnimation(animation); 
						adapter.notifyDataSetChanged();
					}
				}
			});
		appTask.execute();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_search, menu);
		MenuItem menuItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) menuItem.getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {

					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					if(newText != null){
					    adapter.getFilter().filter(newText);
					}
					return false;
				}
			});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_setting:
				startActivity(new Intent(getActivity(), SettingActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
