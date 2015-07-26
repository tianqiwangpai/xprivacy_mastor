package com.hy.xp.app.clear;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.hy.xp.app.Common;
import com.hy.xp.app.task.PreferenceUtils;
import com.hy.xp.talkingdata.ClearFileBean;
import com.hy.xp.talkingdata.TalkingdataFile;

/**
 * �� �� ��: AppCleanManager.java �� ��:
 * ��Ҫ�����������/�⻺�棬������ݿ⣬���sharedPreference�����files������Զ���Ŀ¼
 */
public class AppCleanManager
{
	private static SharedPreferences sp;
	private static String path;

	public static void cleanAppDataByPackageName(Context context, PackageManager packageManager, String packageName)
	{
		// �ر�Ӧ��
		ApplicationInfo appInfo = null;
		try {
			appInfo = packageManager.getApplicationInfo(packageName, 0);
			System.out.println(appInfo.uid);
			CloseApp.killProcessByApplicationInfo(appInfo, context);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// Intent applyIntent = new Intent(Common.MY_PACKAGE_NAME +
		// ".UPDATE_PERMISSIONS");
		// applyIntent.putExtra("action", Common.ACTION_PERMISSIONS);
		// applyIntent.putExtra("Package", packageName);
		// applyIntent.putExtra("Kill", true);
		// ApplicationEx.getContextObject().sendBroadcast(applyIntent,
		// Common.MY_PACKAGE_NAME + ".BROADCAST_PERMISSION");

		AppCache.addOneClear(packageName, context);// ���һ�������¼

		clearPackage(packageName);// ����Ӧ������
		clearSdcard(context);// ����SD���������ѡ��õ��ļ�����������
		deleteFilesByDirectory(context, getDataDir(packageManager, packageName, context), packageName);

		try {
			ClearFilePaht(TalkingdataFile.reader("/tmp/" + appInfo.uid + ".log"), "/tmp/" + appInfo.uid + ".log");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ʹ��pm��������ֻ�����
	 * 
	 * @param packageName
	 */
	public static void clearPackage(String packageName)
	{
		Process process = null;
		DataOutputStream os = null;
		try {
			String clearPackageCmd = "pm clear " + packageName;

			// clearPackageCmd = pm clear com.tencent.mobileqq
			// System.out.println("clearPackageCmd = " + clearPackageCmd);

			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(clearPackageCmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void ClearFilePaht(Set<ClearFileBean> mBeans, String lastFile)
	{
		if (mBeans == null) {
			return;
		}
		Process process = null;
		DataOutputStream os = null;
		try {
			String clearDirCmd;
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			for (ClearFileBean dir : mBeans) {
				// ɾ��·����rm -r /mnt/sdcard/.android_secure
				clearDirCmd = "rm -r " + dir.getmFilePath();
				System.out.println("clearDirCmd = " + clearDirCmd);
				os.writeBytes(clearDirCmd + "\n");
			}
			clearDirCmd = "rm -r " + lastFile;
			System.out.println("clearDirCmd = " + clearDirCmd);
			os.writeBytes(clearDirCmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����ֻ�SD���ϵ�����
	 */
	public static void clearSdcard(Context context)
	{
		sp = PreferenceUtils.getSharedPreferences(context, Common.CLEAN_FILE);
		Map<String, String> datas = (Map<String, String>) sp.getAll();
		if (datas.size() == 0) {
			return;
		}
		HashSet<String> dirs = new HashSet<String>();

		for (Entry<String, String> entity : datas.entrySet()) {
			dirs.add(entity.getKey());
		}

		if (dirs.size() == 0) {
			return;
		}

		Process process = null;
		DataOutputStream os = null;
		try {
			String clearDirCmd;
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());

			for (String dir : dirs) {
				// ɾ��·����rm -r /mnt/sdcard/.android_secure
				clearDirCmd = "rm -r " + dir;

				// System.out.println("clearDirCmd = " + clearDirCmd);

				os.writeBytes(clearDirCmd + "\n");

			}
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ���Ӧ�ö�Ӧ��·���� dataDir:/data/data/com.tencent.mm
	 */
	private static File getDataDir(PackageManager packageManager, String packageName, Context context)
	{
		try {
			ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
			// ɱ������
			CloseApp.killProcessByApplicationInfo(appInfo, context);
			// System.out.println("--------ɾ����Ŀ¼�µ��ļ�:" + appInfo.dataDir);
			return new File(appInfo.dataDir);
		} catch (Exception e) {
			throw new RuntimeException("Pacakge manager dead", e);
		}
	}

	/**
	 * ɾ������ ����ֻ��ɾ��ĳ���ļ����µ��ļ�����������directory�Ǹ��ļ�������������
	 * 
	 * @param directory
	 */
	private static void deleteFilesByDirectory(Context context, File directory, String packageName)
	{
		removeDirectory(directory.getAbsolutePath(), packageName);
		/*
		 * if (null != directory && directory.exists() &&
		 * directory.isDirectory()) { File[] files = directory.listFiles();
		 * if(null != files && files.length > 0) { for (File item : files) {
		 * System.out.println(item.getAbsolutePath()); boolean isRemove =
		 * removeDirectoryOrFile(item.getAbsolutePath());
		 * System.out.println("---------------" + item.getAbsolutePath() +
		 * (isRemove==true?"ɾ���ɹ���":"ɾ��ʧ�ܣ�")); } } }
		 */
	}

	/**
	 * ʹ��Linux������ļ�����ɾ��
	 */
	private static boolean removeDirectory(String directory, String packageName)
	{
		try {
			// 05-04 23:30:39.829: I/System.out(30438):
			// fileOrDir:/data/data/tv.pps.mobile
			/**
			 * ע��Ȩ�����ⲻ��ʹ��rootȨ�޼��ļ��У�����������Ӧ�÷��ʲ���
			 */
			// System.out.println("---->directory:" + directory);

			/* modify by Justin on 20130507 start */
			// KillProcess.killProcessByPackageNameDirectly(packageName);

			removeCacheAndDataFiles(directory);
			/* modify by Justin on 20130507 end */

			/*
			 * String cmd = "rm -r " + directory + "/*";
			 * System.out.println("-------->cmd:" + cmd); process =
			 * Runtime.getRuntime().exec("su"); //�л���root�ʺ� os = new
			 * DataOutputStream(process.getOutputStream()); os.writeBytes(cmd +
			 * "\n"); os.writeBytes("exit\n"); os.flush(); process.waitFor();
			 */
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void removeCacheAndDataFiles(String directory) throws Exception
	{
		Process process = null;
		DataOutputStream os = null;
		BufferedReader br = null;
		try {
			// ���Ӧ�����ݺͻ���
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("ls " + directory + "\n");
			os.writeBytes("exit\n");
			os.flush();

			br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

			ArrayList<String> fileNames = new ArrayList<String>();

			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				// if ("lib".equals(line))// ��ɾ��lib�ļ���
				// {
				// continue;
				// } else
				// {
				fileNames.add(line);
				// }
			}

			// System.out.println("need delete files are " + fileNames);

			os.close();

			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());

			for (String fileName : fileNames) {
				os.writeBytes("rm -r " + directory + "/" + fileName + "\n");
			}

			os.writeBytes("exit\n");
			os.flush();

			os.close();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
