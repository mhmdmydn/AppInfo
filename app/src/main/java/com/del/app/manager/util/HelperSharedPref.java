package com.del.app.manager.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mete_ on 23.12.2016.
 */
public class HelperSharedPref {

	Context mContext;

	public HelperSharedPref(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 *
	 * @param key Constant RC
	 * @param value Only String, Integer, Long, Float, Boolean types
	 */
	public void saveToSharedPref(String key, Object value) throws Exception {
		SharedPreferences.Editor editor = mContext.getSharedPreferences(key, Context.MODE_PRIVATE).edit();
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else {
			throw new Exception("Unacceptable object type");
		}

		editor.commit();
	}

	/**
	 * Return String
	 * @param key
	 * @return null default is null
	 */
	public String loadStringFromSharedPref(String key) throws Exception {
		SharedPreferences prefs = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
		String restoredText = prefs.getString(key, null);

		return restoredText;
	}

	/**
	 * Return int
	 * @param key
	 * @return null default is -1
	 */
	public Integer loadIntegerFromSharedPref(String key) throws Exception {
		SharedPreferences prefs = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
		Integer restoredText = prefs.getInt(key, -1);

		return restoredText;
	}

	/**
	 * Return float
	 * @param key
	 * @return null default is -1
	 */
	public Float loadFloatFromSharedPref(String key) throws Exception {
		SharedPreferences prefs = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
		Float restoredText = prefs.getFloat(key, -1);

		return restoredText;
	}

	/**
	 * Return long
	 * @param key
	 * @return null default is -1
	 */
	public Long loadLongFromSharedPref(String key) throws Exception {
		SharedPreferences prefs = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
		Long restoredText = prefs.getLong(key, -1);

		return restoredText;
	}

	/**
	 * Return boolean
	 * @param key
	 * @return null default is false
	 */
	public Boolean loadBooleanFromSharedPref(String key) throws Exception {
		SharedPreferences prefs = mContext.getSharedPreferences(key, Context.MODE_PRIVATE);
		Boolean restoredText = prefs.getBoolean(key, false);

		return restoredText;
	}

}
