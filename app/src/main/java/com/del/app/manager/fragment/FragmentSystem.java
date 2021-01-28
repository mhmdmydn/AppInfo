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
import com.del.app.manager.adapter.RecySysAdapter;
import android.os.Handler;
import com.del.app.manager.task.SystemAppTask;
import com.del.app.manager.Interface.ResultTask;
import java.util.List;
import com.del.app.manager.model.ModelApk;
import java.util.Collections;
import java.util.Comparator;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.animation.LayoutAnimationController;
import android.view.animation.AnimationUtils;
import com.del.app.manager.util.MainUtils;
import android.content.Intent;
import com.del.app.manager.activity.SettingActivity;
import android.app.Activity;
import com.del.app.manager.util.HelperSharedPref;
import android.graphics.Color;

public class FragmentSystem extends Fragment {

    private RecyclerView mRecyclerview;
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecySysAdapter  sAdapter;
	private String numMode;
    
	public FragmentSystem() {
        // Required empty public constructor
	}

	public static FragmentSystem newInstance(){
		FragmentSystem fragmentSys = new FragmentSystem();
		Bundle args = new Bundle();
		fragmentSys.setArguments(args);
		return fragmentSys;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_system, container, false);
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
		initLogic();
	}
	
	private void initView(){
		mRecyclerview = (RecyclerView)getActivity().findViewById(R.id.recycler_view_system);
		swipeRefreshLayout =(SwipeRefreshLayout)getActivity().findViewById(R.id.swipe_system); 
	}
	
	private void initLogic(){
		SystemAppTask appSystem = new SystemAppTask(getActivity(), new ResultTask(){
				@Override
				public void sysAppFinised(List<ModelApk> userApp) {
					
					Collections.sort(userApp, new Comparator<ModelApk>() {
							@Override
							public int compare(ModelApk lhs, ModelApk rhs) {
								return lhs.getName().compareTo(rhs.getName());
							}
						});
					if(getActivity() != null){
						RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
						mRecyclerview.setLayoutManager(layoutManager);
						sAdapter = new RecySysAdapter(getActivity(), userApp);
						mRecyclerview.setHasFixedSize(true);
						mRecyclerview.setAdapter(sAdapter);
						LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_anim_bottom); 
						mRecyclerview.setLayoutAnimation(animation); 
						sAdapter.notifyDataSetChanged();
					}
				}
			});
			appSystem.execute();
	}
	
	private void refreshContent(){
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.RED);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { 
				@Override 
				public void onRefresh() { 
					new Handler().postDelayed(new Runnable() { 
							@Override 
							public void run() { 
								swipeRefreshLayout.setRefreshing(false); 
								initLogic();
							} 
						}, 2000); 
				} 
			}); 
	}
    
    private void themeSwipe(){
        try {
            numMode = new HelperSharedPref(getActivity()).loadStringFromSharedPref("Theme");
            switch(numMode){
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
					if(newText != null){
						sAdapter.getFilter().filter(newText);
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
}
