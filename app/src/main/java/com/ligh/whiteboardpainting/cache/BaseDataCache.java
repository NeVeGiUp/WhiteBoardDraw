package com.ligh.whiteboardpainting.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * 数据缓存基类
 */

public class BaseDataCache {

	private Context mContext;
	private SharedPreferences mSharePreferences;

	public BaseDataCache(Context appContext) {
		mContext = appContext;
		mSharePreferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
	}

	public BaseDataCache(Context appContext, String tinydbname) {
		mContext = appContext;
		setPreferences(tinydbname);
	}

	@SuppressLint("InlinedApi")
	public void setPreferences(String tinydbname) {
		if (Build.VERSION.SDK_INT < 11) {
			mSharePreferences = mContext.getSharedPreferences(tinydbname,
					Context.MODE_PRIVATE);
		} else {
			mSharePreferences = mContext.getSharedPreferences(tinydbname,
					Context.MODE_MULTI_PROCESS);
		}
	}

	public Bitmap getImage(String path) {
		Bitmap theGottenBitmap = null;
		try {
			theGottenBitmap = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
		}
		return theGottenBitmap;
	}

	public int getInt(String key) {
		return mSharePreferences.getInt(key, 0);
	}

	public long getLong(String key) {
		return mSharePreferences.getLong(key, 0l);
	}

	public long getLongDefaultMaxValue(String key) {
		return mSharePreferences.getLong(key, Long.MAX_VALUE);
	}

	public String getString(String key) {
		return mSharePreferences.getString(key, "");
	}

	public double getDouble(String key) {
		String number = getString(key);
		try {
			double value = Double.parseDouble(number);
			return value;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putInt(key, value);
		editor.apply();
	}

	public void putLong(String key, long value) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putLong(key, value);
		editor.apply();
	}

	public void putDouble(String key, double value) {
		putString(key, String.valueOf(value));
	}

	public void putString(String key, String value) {

		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public void putList(String key, ArrayList<String> marray) {

		SharedPreferences.Editor editor = mSharePreferences.edit();
		String[] mystringlist = marray.toArray(new String[marray.size()]);
		editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
		editor.apply();
	}

	public ArrayList<String> getList(String key) {
		String[] mylist = TextUtils.split(mSharePreferences.getString(key, ""),
				"‚‗‚");
		ArrayList<String> gottenlist = new ArrayList<String>(
				Arrays.asList(mylist));
		return gottenlist;
	}

	public void putListInt(String key, ArrayList<Integer> marray,
                           Context context) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		Integer[] mystringlist = marray.toArray(new Integer[marray.size()]);
		editor.putString(key, TextUtils.join("‚‗‚", mystringlist));
		editor.apply();
	}

	public ArrayList<Integer> getListInt(String key, Context context) {
		String[] mylist = TextUtils.split(mSharePreferences.getString(key, ""), "‚‗‚");
		ArrayList<String> gottenlist = new ArrayList<String>(
				Arrays.asList(mylist));
		ArrayList<Integer> gottenlist2 = new ArrayList<Integer>();
		for (int i = 0; i < gottenlist.size(); i++) {
			gottenlist2.add(Integer.parseInt(gottenlist.get(i)));
		}

		return gottenlist2;
	}

	public void putListBoolean(String key, ArrayList<Boolean> marray) {
		ArrayList<String> origList = new ArrayList<String>();
		for (Boolean b : marray) {
			if (b == true) {
				origList.add("true");
			} else {
				origList.add("false");
			}
		}
		putList(key, origList);
	}

	public ArrayList<Boolean> getListBoolean(String key) {
		ArrayList<String> origList = getList(key);
		ArrayList<Boolean> mBools = new ArrayList<Boolean>();
		for (String b : origList) {
			if (b.equals("true")) {
				mBools.add(true);
			} else {
				mBools.add(false);
			}
		}
		return mBools;
	}

	public void putBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}

	public boolean getBoolean(String key) {
		return mSharePreferences.getBoolean(key, false);
	}

	public boolean getBooleanDefaultTrue(String key) {
		return mSharePreferences.getBoolean(key, true);
	}

	public void putFloat(String key, float value) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.putFloat(key, value);
		editor.apply();
	}

	public float getFloat(String key) {
		return mSharePreferences.getFloat(key, 0f);
	}

	public void remove(String key) {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.remove(key);
		editor.apply();
	}

	public Boolean deleteImage(String path) {
		File tobedeletedImage = new File(path);
		Boolean isDeleted = tobedeletedImage.delete();
		return isDeleted;
	}

	public void clear() {
		SharedPreferences.Editor editor = mSharePreferences.edit();
		editor.clear();
		editor.apply();
	}

	public Map<String, ?> getAll() {
		return mSharePreferences.getAll();
	}

	public void registerOnSharedPreferenceChangeListener(
			SharedPreferences.OnSharedPreferenceChangeListener listener) {
		mSharePreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unregisterOnSharedPreferenceChangeListener(
			SharedPreferences.OnSharedPreferenceChangeListener listener) {
		mSharePreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

}
