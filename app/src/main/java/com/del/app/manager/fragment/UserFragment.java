package com.del.app.manager.fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import com.del.app.manager.R;
import com.del.app.manager.task.UserAppTask;
import com.del.app.manager.Interface.Resul2Task;
import java.util.List;
import com.del.app.manager.model.ModelApk;
import androidx.recyclerview.widget.GridLayoutManager;
import com.del.app.manager.adapter.RecyApkAdapter;
import java.util.Collection;
import java.util.Collections;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Handler;
import android.view.Menu;
import androidx.appcompat.widget.SearchView;
import android.view.MenuItem;
import android.view.MenuInflater;
import com.del.app.manager.util.MainUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.AnimationUtils;
import java.util.Comparator;
import com.del.app.manager.activity.SettingActivity;
import android.content.Intent;
import com.del.app.manager.util.HelperSharedPref;
import android.graphics.Color;

public class UserFragment extends Fragment {


    private RecyclerView mRecyclerview;
	private RecyApkAdapter mApkAdapter;
	private SwipeRefreshLayout swipeRefreshLayout; 
    private String numMode;

	public UserFragment() {
        // Required empty public constructor
	}

	public static UserFragment newInstance() {
		UserFragment fragmentUser = new UserFragment();
		Bundle bundle = new Bundle();
		fragmentUser.setArguments(bundle);
		return fragmentUser;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
        themeSwipe();
		refreshContent();
	}

	@Override
	public void onResume() {
		super.onResume();
		loadApk();
	}

	private void initView() {
		mRecyclerview = (RecyclerView)getActivity().findViewById(R.id.recycler_view_user);
		swipeRefreshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_refresh); 
	}

	public void loadApk() {

		UserAppTask appUser = new UserAppTask(getActivity(), new Resul2Task(){

				@Override
				public void userFinised(List<ModelApk> listUserApp) {
					Collections.sort(listUserApp, new Comparator<ModelApk>() {
							@Override
							public int compare(ModelApk lhs, ModelApk rhs) {
								return lhs.getName().compareTo(rhs.getName());
							}
						});
					if (getActivity() != null) {
						RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
						mRecyclerview.setLayoutManager(layoutManager);
						mApkAdapter = new RecyApkAdapter(getActivity(), listUserApp);
						mRecyclerview.setHasFixedSize(true);
						mRecyclerview.setAdapter(mApkAdapter);
						LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_anim_bottom); 
						mRecyclerview.setLayoutAnimation(animation); 
						mApkAdapter.notifyDataSetChanged();
					}
				}
			});
		appUser.execute();
	}
	private void refreshContent() {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.RED);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { 
				@Override 
				public void onRefresh() { 
					new Handler().postDelayed(new Runnable() { 
							@Override 
							public void run() { 
								loadApk(); 
								swipeRefreshLayout.setRefreshing(false); 
							} 
						}, 2000); 
				} 
			}); 
	}

    private void themeSwipe() {
        try {
            numMode = new HelperSharedPref(getActivity()).loadStringFromSharedPref("Theme");
            switch (numMode) {
                case "0":
                    swipeRefreshLayout.setProgressBackgroundColorSchemeColor(R.color.colorPrimaryDark);
                    break;
                case "1":
                    swipeRefreshLayout.setProgressBackgroundColorSchemeColor(R.color.colorPrimaryDarkNight);
                    break;
                case "2":

                    break;
            }
        } catch (Exception e) {

        }
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
					if (newText != null) {
						mApkAdapter.getFilter().filter(newText);
					}
					return false;
				}
			});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_setting:
				startActivity(new Intent(getActivity(), SettingActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
