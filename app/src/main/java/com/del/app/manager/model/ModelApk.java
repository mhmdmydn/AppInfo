package com.del.app.manager.model;

import android.graphics.drawable.Drawable;

public class ModelApk {
    private String dir;
    private Drawable drawable;
    private String name;
    private String packageName;
    private boolean selected;
    private String vCode;
    private String vName;

    public String getDir() {
        return this.dir;
    }

    public Drawable getIcon() {
        return this.drawable;
    }

    public String getName() {
        return this.name;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getvCode() {
        return this.vCode;
    }

    public String getvName() {
        return this.vName;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setDir(String str) {
        this.dir = str;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setvCode(String str) {
        this.vCode = str;
    }

    public void setvName(String str) {
        this.vName = str;
    }

	@Override
	public String toString() {
		return " ModelApk {" +
        "apkName='" + name + '\'' + 
        ", packageName='" +packageName+ '\'' +
        ", versionCode='" +vCode+ '\'' +
        ", versionName='" +vName+ '\'' +
        ", directori='" +dir+ '\'' +
        ", drawable='" +drawable+ '\'' +
        '}';
	}
}
