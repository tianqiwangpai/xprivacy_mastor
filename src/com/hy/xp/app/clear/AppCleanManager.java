package com.hy.xp.app.clear;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.hy.xp.app.Common;
import com.hy.xp.app.task.PreferenceUtils;
import com.hy.xp.talkingdata.ClearFileBean;
import com.hy.xp.talkingdata.TalkingdataFile;

/**
 * 文 件 名: AppCleanManager.java 描 述:
 * 主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录
 */
public class AppCleanManager
{
	private static SharedPreferences sp;
	private static String path;

	public static void cleanAppDataByPackageName(Context context, PackageManager packageManager, String packageName)
	{
		// 关闭应用
		ApplicationInfo appInfo = null;
		try {
			System.out.println(packageName);
			appInfo = packageManager.getApplicationInfo(packageName, 0);
			/*System.out.println(appInfo.uid);
			//CloseApp.killProcessByApplicationInfo(appInfo, context);
			System.out.println("finished kill "+packageName);*/
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String forceStopCmd = "am force-stop " + packageName;
		suhelper.WriteCmd(forceStopCmd);

		// Intent applyIntent = new Intent(Common.MY_PACKAGE_NAME +
		// ".UPDATE_PERMISSIONS");
		// applyIntent.putExtra("action", Common.ACTION_PERMISSIONS);
		// applyIntent.putExtra("Package", packageName);
		// applyIntent.putExtra("Kill", true);
		// ApplicationEx.getContextObject().sendBroadcast(applyIntent,
		// Common.MY_PACKAGE_NAME + ".BROADCAST_PERMISSION");

		AppCache.addOneClear(packageName, context);// 添加一次清理记录

		//clearPackage(packageName);// 清理应用数据
		
		//清理数据：
		String clearPackageCmd = "pm clear " + packageName;
		suhelper.WriteCmd(clearPackageCmd);
		
		clearSdcard(context);// 清理SD卡，会根据选择好的文件夹清理数据
		deleteFilesByDirectory(context, getDataDir(packageManager, packageName, context), packageName);

		try {
			ClearFilePaht(TalkingdataFile.reader("/tmp/" + appInfo.uid + ".log"), "/tmp/" + appInfo.uid + ".log");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用pm命令清除手机数据
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

			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
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
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			for (ClearFileBean dir : mBeans) {
				// 删除路径：rm -r /mnt/sdcard/.android_secure
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
	 * 清除手机SD卡上的数据
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
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());

			for (String dir : dirs) {
				// 删除路径：rm -r /mnt/sdcard/.android_secure
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
	 * 获得应用对应的路径名 dataDir:/data/data/com.tencent.mm
	 */
	private static File getDataDir(PackageManager packageManager, String packageName, Context context)
	{
		try {
			ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
			// 杀死进程
			CloseApp.killProcessByApplicationInfo(appInfo, context);
			// System.out.println("--------删除该目录下的文件:" + appInfo.dataDir);
			return new File(appInfo.dataDir);
		} catch (Exception e) {
			throw new RuntimeException("Pacakge manager dead", e);
		}
	}

	/**
	 * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
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
		 * (isRemove==true?"删除成功！":"删除失败！")); } } }
		 */
	}

	/**
	 * 使用Linux命令对文件进行删除
	 */
	private static boolean removeDirectory(String directory, String packageName)
	{
		try {
			// 05-04 23:30:39.829: I/System.out(30438):
			// fileOrDir:/data/data/tv.pps.mobile
			/**
			 * 注意权限问题不能使用root权限见文件夹，可能有其他应用访问不了
			 */
			// System.out.println("---->directory:" + directory);

			/* modify by Justin on 20130507 start */
			// KillProcess.killProcessByPackageNameDirectly(packageName);

			removeCacheAndDataFiles(directory);
			/* modify by Justin on 20130507 end */

			/*
			 * String cmd = "rm -r " + directory + "/*";
			 * System.out.println("-------->cmd:" + cmd); process =
			 * Runtime.getRuntime().exec("su"); //切换到root帐号 os = new
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
			// 清除应用数据和缓存
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("ls " + directory + "\n");
			os.writeBytes("exit\n");
			os.flush();

			br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

			ArrayList<String> fileNames = new ArrayList<String>();

			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				// if ("lib".equals(line))// 不删除lib文件夹
				// {
				// continue;
				// } else
				// {
				fileNames.add(line);
				// }
			}

			// System.out.println("need delete files are " + fileNames);

			os.close();

			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
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
