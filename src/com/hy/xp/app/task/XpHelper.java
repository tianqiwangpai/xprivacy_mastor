package com.hy.xp.app.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

/**
 * 甯???╋拷?
 * 
 * @author Administrator
 */
public class XpHelper
{
	public final static String[] array = { "其他", "中国电信", "中国联通", "中国移动" };
	public final static String[] arrayzz = { "中国电信", "中国联通", "中国移动" };
	public final static String Tn = "tn";

	public static boolean CreateTaskNameDirs(String TaskName)
	{
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			String mPath = sdcardDir.getPath() + "/xp_datafile/" + TaskName;
			File mFile = new File(mPath);
			if (!mFile.exists()) {
				mFile.mkdirs();
				mFile.setExecutable(true, false);
				mFile.setReadable(true, false);
				mFile.setWritable(true, false);
				return true;
			}
		}
		return false;
	}

	public static String CreateTaskNameDataFile(Context mContext, String data, String path, String filename)
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			String sdcardRoot = path + "/" + filename;
			File mFile = new File(sdcardRoot);
			try {
				if (!mFile.exists()) {
					mFile.createNewFile();
				} else {
					mFile.delete();
				}
				FileOutputStream fos = new FileOutputStream(mFile);
				fos.write(data.getBytes());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			return sdcardRoot;
		} else {
			Toast.makeText(mContext, "当前手机没有SD卡或者SD为只读状态！", Toast.LENGTH_LONG).show();
		}
		return null;
	}

	public static String BufferedReaderJSON(String path) throws IOException
	{
		File file = new File(path);
		if (!file.exists() || file.isDirectory())
			throw new FileNotFoundException();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String temp = null;
		StringBuffer sb = new StringBuffer();
		temp = br.readLine();
		while (temp != null) {
			sb.append(temp + " ");
			temp = br.readLine();
		}
		br.close();
		return sb.toString();
	}
}
