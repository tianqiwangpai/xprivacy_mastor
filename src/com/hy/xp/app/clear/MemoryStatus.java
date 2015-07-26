package com.hy.xp.app.clear;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

public class MemoryStatus
{
	static final int ERROR = -1;

	static public boolean externalMemoryAvailable()
	{
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	static public long getAvailableInternalMemorySize()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	static public long getTotalInternalMemorySize()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public static long getAvailableExternalMemorySize()
	{
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	static public long getTotalExternalMemorySize()
	{
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return ERROR;
		}
	}

	public static void getStorageStatus(Context context)
	{
		// System.out.println("�ڴ洢�����ô�С:" + formateFileSize(context,
		// getAvailableInternalMemorySize()));
		// System.out.println("�ڴ洢���ܴ�С:" + formateFileSize(context,
		// getTotalInternalMemorySize()));
		// System.out.println("��洢��(SD��)���ô�С:" + formateFileSize(context,
		// getAvailableExternalMemorySize()));
		// System.out.println("��洢��(SD��)�ܴ�С:" + formateFileSize(context,
		// getTotalExternalMemorySize()));
	}

	static public String formatSize(long size)
	{
		String suffix = null;

		if (size >= 1024) {
			suffix = "KiB";
			size /= 1024;
			if (size >= 1024) {
				suffix = "MiB";
				size /= 1024;
			}
		}

		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}

		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}

	// ϵͳ�������ַ���ת�� long -String (kb)
	private static String formateFileSize(Context context, long size)
	{
		return Formatter.formatFileSize(context, size);
	}

	// ����ⲿ�洢����״̬����SD����
	public static void getExternalStorageStatus(Context context)
	{
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();

		// System.out.println("totalSize:" + formateFileSize(context,
		// totalBlocks * blockSize));
		// System.out.println("usedSize:" + formateFileSize(context,
		// (totalBlocks - availableBlocks) * blockSize));
		// System.out.println("availableSize:" + formateFileSize(context,
		// availableBlocks * blockSize));
	}
}
