package com.del.app.manager.util;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import android.content.res.*;
import android.graphics.*;
import android.view.Gravity;
import com.del.app.manager.R;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.view.menu.MenuBuilder;
import com.del.app.manager.task.BackupTask;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import java.io.File;
import net.dongliu.apk.parser.ApkFile;
import java.io.IOException;
import net.dongliu.apk.parser.bean.ApkMeta;
import android.content.Intent;
import com.del.app.manager.activity.MoreDetailActivity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.os.Build;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.Activity;
import androidx.core.app.NotificationCompat;
import android.net.Uri;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;

public final class MainUtils {

	public static void setBackgroundGradient(View view, int color1, int color2){
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[] {color1,color2});
		view.setBackgroundDrawable(gd);
	}

	public static void setRoundedRipple(View v,int LT,int RT,int RB,int LB,int color1,int size,int color2,int color3){
		GradientDrawable shape = new GradientDrawable();
		shape.setColor(color1);
		shape.setCornerRadii(new float[]{(float)LT,(float)LT,(float)RT,(float)RT,(float)RB,(float)RB,(float)LB,(float)LB});
		shape.setStroke(size, color2);
		RippleDrawable ripdr = new RippleDrawable(new ColorStateList(new int[][]{new int[]{}}, new int[]{color3}), shape, null);
		v.setBackgroundDrawable(ripdr);
	}


	public static void showMessage(Context _context, String _s) {
		Toast.makeText(_context, _s, Toast.LENGTH_SHORT).show();
	}
	
	public static void showDialog(Context con, String msg){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setTitle("AndroidManifest.xml");
		builder.setMessage(msg)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//do things
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}

	public static int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}

	public static int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}

	public static ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
				_result.add((double) _arr.keyAt(_iIdx));
		}
		return _result;
	}

	public static float getDip(Context _context, int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, _context.getResources().getDisplayMetrics());
	}

	public static int getDisplayWidthPixels(Context _context) {
		return _context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getDisplayHeightPixels(Context _context) {
		return _context.getResources().getDisplayMetrics().heightPixels;
	}

	public static void getAllKeysFromMap(Map<String, Object> map, ArrayList<String> output) {
		if (output == null) return;
		output.clear();

		if (map == null || map.size() <= 0) return;

		Iterator itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry) itr.next();
			output.add(entry.getKey());
		}
	}
	
	public static void showPopMenu(final Context ctx,final  View v , final String str, final String dir){
		
		MenuBuilder menuBuilder =new MenuBuilder(ctx);
		MenuInflater inflater = new MenuInflater(ctx);
		inflater.inflate(R.menu.menu_more, menuBuilder);
		MenuPopupHelper optionsMenu = new MenuPopupHelper(ctx, menuBuilder, v);
		optionsMenu.setForceShowIcon(true); 
		
		menuBuilder.setCallback(new MenuBuilder.Callback(){

				@Override
				public boolean onMenuItemSelected(MenuBuilder menu, MenuItem menuItem) {
					switch(menuItem.getItemId()){
						case R.id.open:
							ApkUtils.openApp(ctx, str);
							break;
						case R.id.extract:
							new BackupTask(ctx).execute(new String[]{str});
							break;
						case R.id.sharelink:
							ApkUtils.sharePlayStoreLink(ctx, str);
							break;
						case R.id.shareapk:
							ApkUtils.shareApk(ctx, dir);
							break;
						case R.id.playstore:
							ApkUtils.playStoreIntent(ctx, str);
							break;
						case R.id.uninstall:
							ApkUtils.uninstallAPK(ctx, str);
							break;
						case R.id.info:
							ApkUtils.infoScreen(ctx, str);
							break; 
							default:
							return false;
					}
					return true;
				}

				@Override
				public void onMenuModeChange(MenuBuilder menu) {
					
				}
			});
			optionsMenu.show();
	}
	
	public static void copyToClipBoardText(Context con, String text){
		ClipboardManager clipboard = (ClipboardManager) con.getSystemService(Context.CLIPBOARD_SERVICE); 
		ClipData clip = ClipData.newPlainText("Copy", text);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(con, "Copied text: "+ text, 2).show();
	}
	
	public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n Please don't remove this information\n-----------------------------\n Device OS: Android \n Device OS version: " +
				Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
				"\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ghodelchibar@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report find bug");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, "Choose email client"));
    }
	
	public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
    
    public static void restart(Context con) {
        PackageManager pm = con.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(con.getPackageName());
        if (intent != null) {
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            con.startActivity(intent);
        }
        ((Activity)con).finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    
    public static void showNotif(Context con, String location){
        NotificationCompat.Builder notif = new NotificationCompat.Builder(con)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Extract done")
        .setContentText(location)
        .setPriority(5)
        .setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        NotificationManager manager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notif.build());
    }
}
