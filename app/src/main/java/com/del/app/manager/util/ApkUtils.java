package com.del.app.manager.util;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import android.graphics.drawable.Drawable;
import java.text.DecimalFormat;
import com.del.app.manager.model.ModelApk;
import java.util.ArrayList;
import android.content.pm.ResolveInfo;
import android.content.ComponentName;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.Signature;

public class ApkUtils {

    /**
     * Install APK
     */
    public static void installAPK(Context context, File apkFile) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * Uninstall APK
     */
    public static void uninstallAPK(Context context, String apkPackageName) {
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.setData(Uri.parse("package:" + apkPackageName));
        context.startActivity(intent);
    }

    /**
     * App Setting
     */
    public static void infoScreen(Context context, String apkPackageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + apkPackageName));
        context.startActivity(intent);
    }

    /**
     * Playstore Intent
     */
    public static void playStoreIntent(Context context, String apkPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + apkPackageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + apkPackageName)));
        }
    }

    /**Share App*/
    public static String getAppName(Activity activity, String packageName) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String appName = (String) packageManager.getApplicationLabel(info);
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
	/** Share Link Apk*/
	
	public static void sharePlayStoreLink(Context con, String packageName){
		Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.SUBJECT", " share PlayStore Link Apk");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://play.google.com/store/apps/details?id=");
        stringBuilder.append(packageName);
        intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
        intent.setType("text/plain");
        con.startActivity(intent);
	}
	
	/** Share Apk Source*/
	public static void shareApk(Context context, String str) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.setType("apk/apk");
        intent.putExtra("android.intent.extra.STREAM", Uri.parse(str));
        context.startActivity(intent);
    }
	
	
    public static File saveAppToStorage(Activity context, String packageName, File dest) {
        try {
            File file = ApkUtils.getAppFile(context, packageName);
            if (!dest.exists()) dest.mkdirs();
            dest = new File(dest.getPath() + "/" + getAppName(context, packageName) + ".apk");
            if (!dest.exists()) dest.createNewFile();
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(dest);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            return dest;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
	
	public static boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) != 0;
    }
	
	
	public static List<ModelApk> getAllApk(Context context, boolean z) {
        List<ModelApk> arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (!isSystemPackage(applicationInfo)) {
                ModelApk modelApk = new ModelApk();
                if (z) {
                    modelApk.setName(applicationInfo.loadLabel(packageManager).toString());
                    modelApk.setDrawable(applicationInfo.loadIcon(packageManager));
                }
                modelApk.setPackageName(applicationInfo.packageName);
                modelApk.setDir(applicationInfo.sourceDir);
                modelApk.setvCode(String.valueOf(packageInfo.versionCode));
                modelApk.setvName(String.valueOf(packageInfo.versionName));
                modelApk.setSelected(false);
                arrayList.add(modelApk);
            }
        }
        return arrayList;
    }
	
	public static List<ModelApk> getSystemApk(Context context, boolean z) {
        List<ModelApk> arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        List installedPackages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            ApplicationInfo applicationInfo = ((PackageInfo) installedPackages.get(i)).applicationInfo;
            if (isSystemPackage(applicationInfo)) {
                ModelApk modelApk = new ModelApk();
                if (z) {
                    modelApk.setName(applicationInfo.loadLabel(packageManager).toString());
                    modelApk.setDrawable(applicationInfo.loadIcon(packageManager));
                }
                modelApk.setPackageName(applicationInfo.packageName);
                modelApk.setvCode(String.valueOf(((PackageInfo) installedPackages.get(i)).versionCode));
                modelApk.setvName(String.valueOf(((PackageInfo) installedPackages.get(i)).versionName));
                modelApk.setDir(applicationInfo.sourceDir);
                modelApk.setSelected(false);
                arrayList.add(modelApk);
            }
        }
        return arrayList;
    }
	
	public static File getAppFile(Context context, String packageName){
		File apkFile = new File(packageName);
		if(apkFile.exists()){
			return apkFile;
		}
		return null;
	}

    public static void openApp(Activity activity, String packageName) {
        if (isAppInstalled(activity, packageName)) {
            if (activity.getPackageManager().getLaunchIntentForPackage(packageName) != null) {
                activity.startActivity(activity.getPackageManager().getLaunchIntentForPackage(packageName));
            } else {
                Toast.makeText(activity, "Couldn't open", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAppInstalled(Activity activity, String packageName) {
        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }
	
    public static String bytesToMB( long bytes ) {
        String res = "";
        Integer num = 0;
        if ( bytes < 1000000 ) {
            // In kilobytes
            num = ( ( int ) Math.ceil( bytes / 1000 ) );
            res = num.toString() + " KB";
        } else {
            // In megabytes
            num = ( ( int ) Math.ceil( bytes / 1000000 ) );
            res = num.toString() + " MB";
        }

        return res;
    }
	
	public static void openApp(Context context, String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
			.queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }
	
	
	public static long firstInstalled(Context con, String pName){
		long installed = 0;
		
		try {
			installed = con.getPackageManager().getPackageInfo(pName, 0).firstInstallTime;
		} catch (PackageManager.NameNotFoundException e) {
			e.getMessage();
		}
		return installed;
	}
	
	
	public static long getLastUpdate(Context con, String pName){
		long lastUpdate = 0;
		try {
			lastUpdate = con.getPackageManager().getPackageInfo(pName, 0).lastUpdateTime;
		} catch (PackageManager.NameNotFoundException e) {
			e.getMessage();
		}
		return lastUpdate;
	}
	
	public static ArrayList<String> activityInfoList(Context con ,String dir){
		ArrayList<String> result = new ArrayList<>();
		PackageManager pm = con.getPackageManager();

		PackageInfo info = pm.getPackageArchiveInfo(dir, 
													PackageManager.GET_ACTIVITIES);
		//Log.i("ActivityInfo", "Package name is " + info.packageName);
		int count = 1;
		for (android.content.pm.ActivityInfo a : info.activities) {
			
			result.add(""+count+ ". " + a.name+ "\n");
			count++;
		}
		return result;
	}
	
	public static ArrayList<String> getPermissionsByPackageName(Context con , String packageName){
		ArrayList<String> listPermission = new ArrayList<>();
        try {
            // Get the package info
            PackageInfo packageInfo = con.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

            // Permissions counter
            int counter = 1;
            // Loop through the package info requested permissions
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    String permission =packageInfo.requestedPermissions[i];
                    // To make permission name shorter
                    //permission = permission.substring(permission.lastIndexOf(".")+1);
                    listPermission.add(""+counter + ". " + permission + "\n");
                    counter++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listPermission;
    }
	
	public static int getUidForPackage(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		try {
			return packageManager.getPackageUid(packageName, 0);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int getSignatureApk(Context con, String packageName){
		int result = 0;
		
		try {
			Signature[] sigs = con.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
			for (Signature sig : sigs) 
			{
				result = sig.hashCode();
			}
		} catch (PackageManager.NameNotFoundException e) {
			
		}
		return result;
	}
	
	public static int getVersionCode (Context context, String packageName) {

        try {

            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);

            int vc = pi.versionCode;

            return vc;

        } catch (Exception exc) {
            exc.printStackTrace();
            return 0;
        }

    }
}
