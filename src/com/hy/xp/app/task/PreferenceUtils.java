package com.hy.xp.app.task;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils
{

	public static SharedPreferences getSharedPreferences(Context mContext, String file)
	{
		return mContext.getSharedPreferences(file, Context.MODE_PRIVATE);
	}

	public static final String FILE_NAME = "xp_public";

	public static void setParam(Context context, String fileName, String key, Object object)
	{

		String type = object.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if ("String".equals(type)) {
			editor.putString(key, (String) object);
		} else if ("Integer".equals(type)) {
			editor.putInt(key, (Integer) object);
		} else if ("Boolean".equals(type)) {
			editor.putBoolean(key, (Boolean) object);
		} else if ("Float".equals(type)) {
			editor.putFloat(key, (Float) object);
		} else if ("Long".equals(type)) {
			editor.putLong(key, (Long) object);
		}

		editor.commit();
	}

	public static Object getParam(Context context, String fileName, String key, Object defaultObject)
	{
		String type = defaultObject.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

		if ("String".equals(type)) {
			return sp.getString(key, (String) defaultObject);
		} else if ("Integer".equals(type)) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if ("Boolean".equals(type)) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if ("Float".equals(type)) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if ("Long".equals(type)) {
			return sp.getLong(key, (Long) defaultObject);
		}

		return null;
	}

	public static void clearKey(Context mContext, String filename, String key)
	{
		SharedPreferences sp = mContext.getSharedPreferences(filename, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key).commit();
	}

	public static void setParam(Context context, String key, Object object)
	{

		String type = object.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if ("String".equals(type)) {
			editor.putString(key, (String) object);
		} else if ("Integer".equals(type)) {
			editor.putInt(key, (Integer) object);
		} else if ("Boolean".equals(type)) {
			editor.putBoolean(key, (Boolean) object);
		} else if ("Float".equals(type)) {
			editor.putFloat(key, (Float) object);
		} else if ("Long".equals(type)) {
			editor.putLong(key, (Long) object);
		}

		editor.commit();
	}

	public static Object getParam(Context context, String key, Object defaultObject)
	{
		String type = defaultObject.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

		if ("String".equals(type)) {
			return sp.getString(key, (String) defaultObject);
		} else if ("Integer".equals(type)) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if ("Boolean".equals(type)) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if ("Float".equals(type)) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if ("Long".equals(type)) {
			return sp.getLong(key, (Long) defaultObject);
		}

		return null;
	}

}

/**
 * ä¿?å­???°æ??\t SharedPreferencesUtils.setParam(this, "String", "xiaanming");
 * SharedPreferencesUtils.setParam(this, "int", 10);
 * SharedPreferencesUtils.setParam(this, "boolean", true);
 * SharedPreferencesUtils.setParam(this, "long", 100L);
 * SharedPreferencesUtils.setParam(this, "float", 1.1f); ??·å????°æ??\t
 * SharedPreferencesUtils.getParam(TimerActivity.this, "String", "");
 * SharedPreferencesUtils.getParam(TimerActivity.this, "int", 0);
 * SharedPreferencesUtils.getParam(TimerActivity.this, "boolean", false);
 * SharedPreferencesUtils.getParam(TimerActivity.this, "long", 0L);
 * SharedPreferencesUtils.getParam(TimerActivity.this, "float", 0.0f);
 */

