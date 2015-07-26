package com.hy.xp.app.clear;

import java.io.DataOutputStream;

public class AppGetRootPermission
{
	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * 
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean upgradeRootPermission(String pkgCodePath)
	{
		Process process = null;
		DataOutputStream os = null;
		try {
			// System.out.println("pkgCodePath:" + pkgCodePath);
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();

			// int exitValue = process.exitValue();
			// System.out.println("process.exitValue() = " + exitValue);

		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				// ----修改点----
				// process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
