package com.hy.xp.talkingdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @ClassName: TalkingdataFile
 * @Description: TODO
 * @date Mar 17, 2015 4:58:07 PM
 * 
 */
public class TalkingdataFile
{
	/**
	 * 
	 * @Title: delFilesByPath
	 * @Description: TODO
	 * @param @param path
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean delFilesByPath(String path, String str)
	{
		boolean b = false;
		File file = new File(path);
		File[] tempFile = file.listFiles();
		for (int i = 0; i < tempFile.length; i++) {
			if (tempFile[i].getName().startsWith(str) || tempFile[i].getName().endsWith(str)) {
				tempFile[i].delete();
				b = true;
			}
		}
		return b;
	}

	/**
	 * read and with"\t"
	 * 
	 * @Title: reader
	 * @Description: TODO
	 * @param @param filename
	 * @param @throws IOException
	 * @return void
	 * @throws
	 */
	public static Set<ClearFileBean> reader(String filename) throws IOException
	{

		Set<ClearFileBean> mBeans = new TreeSet<ClearFileBean>();
		List<ClearFileBean> mList = new ArrayList<ClearFileBean>();
		File mFile = new File(filename);
		if (mFile.exists()) {
			FileInputStream fis = new FileInputStream(mFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine()) != null) {
				ClearFileBean mBean = new ClearFileBean(line);
				// org.tracetool.hackconnectivityservice
				if (mBean.getmFilePath().indexOf("/proc/") == -1 && mBean.getmFilePath().indexOf("/sys/") == -1 && mBean.getmFilePath().indexOf("hackconnectivityservice") == -1 && mBean.getmFilePath().indexOf("/system/") == -1 && mBean.getmFilePath().indexOf("/data/app/") == -1 && mBean.getmFilePath().indexOf("/data/misc/") == -1 && mBean.getmFilePath().indexOf("/system/app/") == -1
						&& mBean.getmFilePath().indexOf("/sbin/") == -1 && mBean.getmFilePath().indexOf("init.trout.rc") == -1 && mBean.getmFilePath().indexOf("init.rc") == -1 && mBean.getmFilePath().indexOf("init.goldfish.rc") == -1 && mBean.getmFilePath().indexOf("init") == -1 && mBean.getmFilePath().indexOf("/root/") == -1 && mBean.getmFilePath().indexOf("/dev/") == -1
						&& mBean.getmFilePath().indexOf("/etc/") == -1) {
					mList.add(mBean);
				}
				// System.out.println(mBean.toString());
			}
			br.close();
			isr.close();
			fis.close();
			// }
			mBeans.addAll(mList);
			// System.out.println(mBeans.size());
			return mBeans;
		}
		return null;
	}
}
