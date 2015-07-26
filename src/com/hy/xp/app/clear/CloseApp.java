package com.hy.xp.app.clear;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

public class CloseApp
{

	public static void killProcessByApplicationInfo(ApplicationInfo applicationInfo, Context context)
	{
		// ǿ�ƹر�Ӧ��
		forceStopPackage(applicationInfo.packageName);

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String processName = applicationInfo.processName;
		int pid = -1;// ����ID
		// ��ȡϵͳ�������������еĽ���
		List<RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
		// ��ϵͳ�������������еĽ��̽��е�����������������ǵ�ǰ���̣���Kill��
		top: for (RunningAppProcessInfo appProcessInfo : appProcessInfos) {
			if (processName.equals(appProcessInfo.processName)) {
				pid = appProcessInfo.pid;
				break top;
			}
		}

		// System.out.println("pid = " + pid);

		if (-1 != pid) {
			try {
				killProcessByPid(pid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				activityManager.killBackgroundProcesses(processName);// 2.2����
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				android.os.Process.killProcess(pid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * <uses-permission android:name="android.permission.FORCE_STOP_PACKAGES"/>
	 */
	/*
	 * private static void forceStopApp(ActivityManager activityManager, String
	 * packageName) { try { Method method =
	 * Class.forName("android.app.ActivityManager"
	 * ).getMethod("forceStopPackage", String.class);
	 * method.invoke(activityManager, packageName); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	/**
	 * ���ݽ���IDɱ������
	 */
	private static boolean killProcessByPid(int pid)
	{
		Process process = null;
		DataOutputStream os = null;
		try {
			String kill_cmd = "kill -9 " + pid;

			// System.out.println("kill_cmd = " + kill_cmd);

			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(kill_cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				// process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * ���ݰ�������ɱ��Ӧ��
	 */
	public static void killProcessByPackageNameDirectly(String packageName) throws Exception
	{
		Process process = null;
		DataOutputStream os = null;
		BufferedReader br = null;
		try {
			// kill ����
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("ps\n");
			os.writeBytes("exit\n");
			os.flush();

			br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

			String curLine = null;

			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(packageName)) {
					curLine = line;
					break;
				}
			}

			// System.out.println("need kill process info are [" + curLine +
			// "]");

			os.close();

			if (null != curLine) {
				String[] fields = curLine.split("\\s");

				// System.out.println("ps fields are " +
				// Arrays.toString(fields));

				if (fields.length > 1) {
					String pid = null;
					for (int i = 1; i < fields.length; i++) {
						if (!TextUtils.isEmpty(fields[i].trim())) {
							pid = fields[i].trim();
							break;
						}
					}

					if (pid != null) {
						process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
						os = new DataOutputStream(process.getOutputStream());

						// System.out.println("kill -9 " + pid);

						os.writeBytes("kill -9 " + pid + "\n");

						os.writeBytes("exit\n");
						os.flush();

						os.close();
					}
				}
			}
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

	/**
	 * ʹ��am force-stop����ǿ�ƹر�
	 */
	public static void forceStopPackage(String pkgName)
	{
		Process process = null;
		DataOutputStream os = null;
		try {
			String forceStopCmd = "am force-stop " + pkgName;

			// forceStopCmd = am force-stop com.tencent.mobileqq
			// System.out.println("forceStopCmd = " + forceStopCmd);

			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(forceStopCmd + "\n");
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
	 * ���÷���ǿ�ƹر�Ӧ��
	 */
	public static void forceStopPackage(Context ctx, String pkgName)
	{
		try {
			ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			Method localMethod = am.getClass().getMethod("forceStopPackage", String.class);
			localMethod.invoke(am, pkgName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
