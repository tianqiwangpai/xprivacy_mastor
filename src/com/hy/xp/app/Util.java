package com.hy.xp.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hy.xp.app.task.PhoneDataBean;

public class Util
{
	private static boolean mLog = true;
	private static boolean mLogDetermined = false;
	private static Boolean mHasLBE = null;

	public static int NOTIFY_RESTART = 0;
	public static int NOTIFY_NOTXPOSED = 1;
	public static int NOTIFY_SERVICE = 2;
	public static int NOTIFY_MIGRATE = 3;
	public static int NOTIFY_RANDOMIZE = 4;
	public static int NOTIFY_UPGRADE = 5;
	public static int NOTIFY_UPDATE = 6;
	
	public static int error_code;
	public static enum error{
		Normal,
		NoPhoneInfo,
		FinishedNEWDATA,
		FinishedBackrecord
	}
	
	public static Random r = new Random();

	public static void log(XHook hook, int priority, String msg)
	{
		// Check if logging enabled
		int uid = Process.myUid();
		if (!mLogDetermined && uid > 0) {
			mLogDetermined = true;
			try {
				mLog = PrivacyManager.getSettingBool(0, PrivacyManager.cSettingLog, false);
			} catch (Throwable ignored) {
				mLog = false;
			}
		}

		// Log if enabled
		if (priority != Log.DEBUG && (priority == Log.INFO ? mLog : true))
			if (hook == null)
				Log.println(priority, "hytmotest", msg);
			else
				Log.println(priority, String.format("hytmotest/%s", hook.getClass().getSimpleName()), msg);

		// Report to service
		if (uid > 0 && priority == Log.ERROR)
			if (PrivacyService.isRegistered())
				PrivacyService.reportErrorInternal(msg);
			else
				try {
					IPrivacyService client = PrivacyService.getClient();
					if (client != null)
						client.reportError(msg);
				} catch (RemoteException ignored) {
				}
	}

	public static void bug(XHook hook, Throwable ex)
	{
		int priority;
		if (ex instanceof ActivityShare.AbortException)
			priority = Log.WARN;
		else if (ex instanceof ActivityShare.ServerException)
			priority = Log.WARN;
		else if (ex instanceof ConnectTimeoutException)
			priority = Log.WARN;
		else if (ex instanceof FileNotFoundException)
			priority = Log.WARN;
		else if (ex instanceof HttpHostConnectException)
			priority = Log.WARN;
		else if (ex instanceof NoClassDefFoundError)
			priority = Log.WARN;
		else if (ex instanceof OutOfMemoryError)
			priority = Log.WARN;
		else if (ex instanceof RuntimeException)
			priority = Log.WARN;
		else if (ex instanceof SecurityException)
			priority = Log.WARN;
		else if (ex instanceof SocketTimeoutException)
			priority = Log.WARN;
		else if (ex instanceof SSLPeerUnverifiedException)
			priority = Log.WARN;
		else if (ex instanceof TransactionTooLargeException)
			priority = Log.WARN;
		else if (ex instanceof UnknownHostException)
			priority = Log.WARN;
		else
			priority = Log.ERROR;

		boolean xprivacy = false;
		for (StackTraceElement frame : ex.getStackTrace())
			if (frame.getClassName() != null && frame.getClassName().startsWith("com.hy.xp.app")) {
				xprivacy = true;
				break;
			}
		if (!xprivacy)
			priority = Log.WARN;

		log(hook, priority, ex.toString() + " uid=" + Process.myUid() + "\n" + Log.getStackTraceString(ex));
	}

	public static void logStack(XHook hook, int priority)
	{
		logStack(hook, priority, false);
	}

	public static void logStack(XHook hook, int priority, boolean cl)
	{
		StringBuilder trace = new StringBuilder();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			trace.append(ste.toString());
			if (cl)
				try {
					Class<?> clazz = Class.forName(ste.getClassName(), false, loader);
					trace.append(" [");
					trace.append(clazz.getClassLoader().toString());
					trace.append("]");
				} catch (ClassNotFoundException ignored) {
				}
			trace.append("\n");
		}
		log(hook, priority, trace.toString());
	}

	public static boolean isXposedEnabled()
	{
		// Will be hooked to return true
		log(null, Log.WARN, "hytmotest not enabled");
		return false;
	}

	@SuppressLint("NewApi")
	public static int getAppId(int uid)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			try {
				// TODO: update by method in SDK 20
				// UserHandle: public static final int getAppId(int uid)
				Method method = (Method) UserHandle.class.getDeclaredMethod("getAppId", int.class);
				uid = (Integer) method.invoke(null, uid);
			} catch (Throwable ex) {
				Util.log(null, Log.WARN, ex.toString());
			}
		return uid;
	}

	@SuppressLint("NewApi")
	public static int getUserId(int uid)
	{
		int userId = 0;
		if (uid > 99) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				try {
					// TODO: update by method in SDK 20
					// UserHandle: public static final int getUserId(int uid)
					Method method = (Method) UserHandle.class.getDeclaredMethod("getUserId", int.class);
					userId = (Integer) method.invoke(null, uid);
				} catch (Throwable ex) {
					Util.log(null, Log.WARN, ex.toString());
				}
		} else
			userId = uid;
		return userId;
	}

	public static String getUserDataDirectory(int uid)
	{
		// Build data directory
		String dataDir = Environment.getDataDirectory() + File.separator;
		int userId = getUserId(uid);
		if (userId == 0)
			dataDir += "data";
		else
			dataDir += "user" + File.separator + userId;
		dataDir += File.separator + Util.class.getPackage().getName();
		return dataDir;
	}

	public static boolean hasMarketLink(Context context, String packageName)
	{
		try {
			PackageManager pm = context.getPackageManager();
			String installer = pm.getInstallerPackageName(packageName);
			if (installer != null)
				return installer.equals("com.android.vending") || installer.contains("google");
		} catch (Exception ex) {
			log(null, Log.WARN, ex.toString());
		}
		return false;
	}

	public static void viewUri(Context context, Uri uri)
	{
		Intent infoIntent = new Intent(Intent.ACTION_VIEW);
		infoIntent.setData(uri);
		if (isIntentAvailable(context, infoIntent))
			context.startActivity(infoIntent);
		else
			Toast.makeText(context, "View action not available", Toast.LENGTH_LONG).show();
	}

	public static boolean hasLBE()
	{
		if (mHasLBE == null) {
			mHasLBE = false;
			try {
				File apps = new File(Environment.getDataDirectory() + File.separator + "app");
				File[] files = (apps == null ? null : apps.listFiles());
				if (files != null)
					for (File file : files)
						if (file.getName().startsWith("com.lbe.security")) {
							mHasLBE = true;
							break;
						}
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}
		return mHasLBE;
	}

	public static int getSelfVersionCode(Context context)
	{
		try {
			String self = Util.class.getPackage().getName();
			PackageManager pm = context.getPackageManager();
			PackageInfo pInfo = pm.getPackageInfo(self, 0);
			return pInfo.versionCode;
		} catch (NameNotFoundException ex) {
			Util.bug(null, ex);
			return 0;
		}
	}

	public static String getSelfVersionName(Context context)
	{
		try {
			String self = Util.class.getPackage().getName();
			PackageManager pm = context.getPackageManager();
			PackageInfo pInfo = pm.getPackageInfo(self, 0);
			return pInfo.versionName;
		} catch (NameNotFoundException ex) {
			Util.bug(null, ex);
			return null;
		}
	}

	public static String sha1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		// SHA1
		int userId = Util.getUserId(Process.myUid());
		String salt = PrivacyManager.getSalt(userId);
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] bytes = (text + salt).getBytes("UTF-8");
		digest.update(bytes, 0, bytes.length);
		bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes)
			sb.append(String.format("%02X", b));
		return sb.toString();
	}

	public static String md5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		// MD5
		int userId = Util.getUserId(Process.myUid());
		String salt = PrivacyManager.getSalt(userId);
		byte[] bytes = MessageDigest.getInstance("MD5").digest((text + salt).getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes)
			sb.append(String.format("%02X", b));
		return sb.toString();
	}

	public static boolean isDebuggable(Context context)
	{
		return ((context.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
	}

	public static boolean isIntentAvailable(Context context, Intent intent)
	{
		PackageManager packageManager = context.getPackageManager();
		return (packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES).size() > 0);
	}

	public static void setPermissions(String path, int mode, int uid, int gid)
	{
		try {
			// frameworks/base/core/java/android/os/FileUtils.java
			Class<?> fileUtils = Class.forName("android.os.FileUtils");
			Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
			setPermissions.invoke(null, path, mode, uid, gid);
			Util.log(null, Log.WARN, "Changed permission path=" + path + " mode=" + Integer.toOctalString(mode) + " uid=" + uid + " gid=" + gid);
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}

	public static void copy(File src, File dst) throws IOException
	{
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(src);
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(dst);
				FileChannel inChannel = inStream.getChannel();
				FileChannel outChannel = outStream.getChannel();
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				if (outStream != null)
					outStream.close();
			}
		} finally {
			if (inStream != null)
				inStream.close();
		}
	}

	public static boolean move(File src, File dst)
	{
		try {
			copy(src, dst);
		} catch (IOException ex) {
			Util.bug(null, ex);
			return false;
		}
		return src.delete();
	}

	public static List<View> getViewsByTag(ViewGroup root, String tag)
	{
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < root.getChildCount(); i++) {
			View child = root.getChildAt(i);

			if (child instanceof ViewGroup)
				views.addAll(getViewsByTag((ViewGroup) child, tag));

			if (tag.equals(child.getTag()))
				views.add(child);
		}
		return views;
	}

	public static List<PhoneDataBean> readIsDijianShunxu(String path, String filename, double tmp_bilv) throws Exception
	{
		List<PhoneDataBean> modelBases = new ArrayList<PhoneDataBean>();
		List<PhoneDataBean> mBases = readcurray(path, filename);
		double bilvplus = new BigDecimal(tmp_bilv / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		int n = (int) (mBases.size() * bilvplus);
		boolean[] bool = new boolean[mBases.size()];
		int randInt = 0;
		int data_index[] = new int[n];
		for (int i = 0; i < (int) (mBases.size() * bilvplus); i++) {
			PhoneDataBean mBean = mBases.get(i);
			modelBases.add(mBean);
		}
		return modelBases;
	}

	public static List<PhoneDataBean> readIsDijianRandom(String path, String filename, double tmp_bilv) throws Exception
	{
		List<PhoneDataBean> modelBases = new ArrayList<PhoneDataBean>();
		List<PhoneDataBean> mBases = readcurray(path, filename);
		double bilvplus = new BigDecimal(tmp_bilv / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		int n = (int) (mBases.size() * bilvplus);
		boolean[] bool = new boolean[mBases.size()];
		int randInt = 0;
		int data_index[] = new int[n];
		for (int i = 0; i < (int) (mBases.size() * bilvplus); i++) {
			do {
				randInt = r.nextInt(mBases.size());
			} while (bool[randInt]);
			bool[randInt] = true;
			data_index[i] = randInt;
		}
		for (int i = 0; i < data_index.length; i++) {
			PhoneDataBean mBean = mBases.get(data_index[i]);
			// System.out.println(mBean.getImei());
			modelBases.add(mBean);

		}
		return modelBases;
	}

	public static List<PhoneDataBean> readcurray(String path, String filename) throws Exception
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String mPath = path + "/" + filename;
			String result = BufferedReaderJSON(mPath);

			if (null != result) {
				Gson mGson = new Gson();
				List<PhoneDataBean> mBases = mGson.fromJson(result, new TypeToken<List<PhoneDataBean>>()
				{
				}.getType());
				return mBases;
			}
		}
		return null;
	}

	private static String BufferedReaderJSON(String str) throws Exception
	{
		File file = new File(str);
		if (!file.exists() || file.isDirectory()) {
			// throw new FileNotFoundException();
			return null;
		} else {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			StringBuffer sb = new StringBuffer();

			while ((temp = br.readLine()) != null) {
				sb.append(temp + " ");
				temp = br.readLine();
			}
			br.close();
			return sb.toString();
		}

	}

	public static String createRandomcodeFile(Context context, String randomcodes, String path, String filename)
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			String sdcardRoot = path + "/" + filename;
			File file = new File(sdcardRoot);
			try {
				if (!file.exists()) {
					file.createNewFile();
				} else {
					file.delete();
				}
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(randomcodes.getBytes());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("sdcardRoot" + sdcardRoot);
			return sdcardRoot;
		}

		return null;
	}
	
	public static boolean itemexists(Object[] array, Object obj) {
       if(array.length <=0 || obj == null){
    	   return false;
       }
       boolean rst = false;
       for(int i=0; i<array.length; i++){
    	   if(obj.equals(array[i])){
    		   rst = true;
    		   break;
    	   }
       }
       return rst;
    }
}
