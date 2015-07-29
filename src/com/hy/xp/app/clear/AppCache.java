package com.hy.xp.app.clear;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hy.xp.app.Common;

public class AppCache
{
	// 应用的清理次数
	private static HashMap<String, Integer> clearTimes;

	// 需要清理的文件夹
	private static HashSet<String> needDelDirs;

	private static SharedPreferences sp;

	/**
	 * update 2013-5-13
	 */
	public static HashMap<String, Integer> getClearTimes()
	{
		return clearTimes;
	}

	/**
	 * update 2013-5-13
	 */
	public static void resetClearTimes()
	{
		clearTimes.clear();
	}

	// 添加一次清理次数,调用肯定在getClearTimes之后
	public static void addOneClear(String pkgName, Context context)
	{
		/**
		 * update 2013-5-13
		 */
		initClearTimes(context);// 初始化数据

		Integer times = clearTimes.get(pkgName);

		if (null == times) {
			times = 0;
		}

		times++;

		clearTimes.put(pkgName, times);
		/**
		 * 保存清理次数的数据 update 2013-5-13
		 */
		if (null == sp)
			sp = context.getSharedPreferences(Common.CLEAN_TIME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(pkgName, times);
		editor.commit();
	}

	/**
	 * 根据应用包名获得清理的次数,保存在
	 */
	public static int getClearTimesByPkgName(String pkgName, Context context)
	{
		/**
		 * update 2013-5-13
		 */
		initClearTimes(context);// 初始化数据

		Integer times = clearTimes.get(pkgName);

		if (null == times) {
			times = 0;
		}

		return times;
	}

	/**
	 * 当clearTimes、needDelDirs为空，表示软件是刚刚启动,需要对数据进行初始化 update 2013-5-13
	 */
	private static void initClearTimes(Context context)
	{
		if (clearTimes != null && needDelDirs != null && sp != null)
			return;
		// System.out.println("---->init clear times");
		sp = context.getSharedPreferences(Common.CLEAN_TIME, Context.MODE_PRIVATE);
		clearTimes = new HashMap<String, Integer>();
		needDelDirs = new HashSet<String>();
		Map<String, Integer> datas = (Map<String, Integer>) sp.getAll();
		for (Map.Entry<String, Integer> entity : datas.entrySet()) {
			clearTimes.put(entity.getKey(), entity.getValue());
		}
	}

	/**
	 * 添加要清理的文件夹
	 */
	public static void addDelDir(String path)
	{
		needDelDirs.add(path);
	}

	/**
	 * 去掉要清理的文件夹
	 */
	public static void removeDelDir(String path)
	{
		needDelDirs.remove(path);
	}

	/**
	 * 获得要清理的文件夹
	 */
	public static HashSet<String> getDelDirs()
	{
		return needDelDirs;
	}

	/**
	 * 清空要清理的文件夹集合
	 */
	public static void clearDelDirs()
	{
		needDelDirs.clear();
	}
}
