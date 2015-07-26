package com.hy.xp.app.clear;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.hy.xp.app.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppCache
{
	// Ӧ�õ��������
	private static HashMap<String, Integer> clearTimes;

	// ��Ҫ������ļ���
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

	// ���һ���������,���ÿ϶���getClearTimes֮��
	public static void addOneClear(String pkgName, Context context)
	{
		/**
		 * update 2013-5-13
		 */
		initClearTimes(context);// ��ʼ������

		Integer times = clearTimes.get(pkgName);

		if (null == times) {
			times = 0;
		}

		times++;

		clearTimes.put(pkgName, times);
		/**
		 * ����������������� update 2013-5-13
		 */
		if (null == sp)
			sp = context.getSharedPreferences(Common.CLEAN_TIME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(pkgName, times);
		editor.commit();
	}

	/**
	 * ����Ӧ�ð����������Ĵ���,������
	 */
	public static int getClearTimesByPkgName(String pkgName, Context context)
	{
		/**
		 * update 2013-5-13
		 */
		initClearTimes(context);// ��ʼ������

		Integer times = clearTimes.get(pkgName);

		if (null == times) {
			times = 0;
		}

		return times;
	}

	/**
	 * ��clearTimes��needDelDirsΪ�գ���ʾ����Ǹո�����,��Ҫ�����ݽ��г�ʼ�� update 2013-5-13
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
	 * ���Ҫ������ļ���
	 */
	public static void addDelDir(String path)
	{
		needDelDirs.add(path);
	}

	/**
	 * ȥ��Ҫ������ļ���
	 */
	public static void removeDelDir(String path)
	{
		needDelDirs.remove(path);
	}

	/**
	 * ���Ҫ������ļ���
	 */
	public static HashSet<String> getDelDirs()
	{
		return needDelDirs;
	}

	/**
	 * ���Ҫ������ļ��м���
	 */
	public static void clearDelDirs()
	{
		needDelDirs.clear();
	}
}
