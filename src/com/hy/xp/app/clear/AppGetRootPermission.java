package com.hy.xp.app.clear;

import java.io.DataOutputStream;

public class AppGetRootPermission
{
	/**
	 * Ӧ�ó������������ȡ RootȨ�ޣ��豸�������ƽ�(���ROOTȨ��)
	 * 
	 * @return Ӧ�ó�����/���ȡRootȨ��
	 */
	public static boolean upgradeRootPermission(String pkgCodePath)
	{
		Process process = null;
		DataOutputStream os = null;
		try {
			// System.out.println("pkgCodePath:" + pkgCodePath);
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
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
				// ----�޸ĵ�----
				// process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
