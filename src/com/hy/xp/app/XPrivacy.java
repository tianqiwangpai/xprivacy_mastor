package com.hy.xp.app;

import static de.robv.android.xposed.XposedHelpers.findClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;

import com.saurik.substrate.MS;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

// TODO: fix link error when using Cydia Substrate
public class XPrivacy implements IXposedHookLoadPackage, IXposedHookZygoteInit
{
	private static boolean mCydia = false;
	private static String mSecret = null;
	private static List<String> mListHookError = new ArrayList<String>();
	private static List<CRestriction> mListDisabled = new ArrayList<CRestriction>();

	// http://developer.android.com/reference/android/Manifest.permission.html
	public static XSharedPreferences prefs;

	static {
		if (mListDisabled.size() == 0) {
			File disabled = new File("/data/system/hytmotest/disabled");
			if (disabled.exists() && disabled.canRead())
				try {
					FileInputStream fis = new FileInputStream(disabled);
					InputStreamReader ir = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(ir);
					String line;
					while ((line = br.readLine()) != null) {
						String[] name = line.split("/");
						if (name.length > 0) {
							String methodName = (name.length > 1 ? name[1] : null);
							CRestriction restriction = new CRestriction(0, name[0], methodName, null);
							mListDisabled.add(restriction);
						}
					}
					br.close();
					ir.close();
					fis.close();
				} catch (Throwable ex) {
					Log.w("XP", ex.toString());
				}
		}
	}

	// Xposed
	public void initZygote(StartupParam startupParam) throws Throwable
	{
		// Check for LBE security master
		if (Util.hasLBE()) {
			Util.log(null, Log.ERROR, "LBE installed");
			return;
		}
		loadPrefs();
		init(startupParam.modulePath);
		//Activities.hookActivitySettings();

	}

	private void loadPrefs()
	{
		// TODO Auto-generated method stub
		prefs = new XSharedPreferences(Common.MY_PACKAGE_NAME, "ModSettings");
		prefs.makeWorldReadable();
	}

	public NetworkInfo createNetworkInfo(final int type, final boolean connected) throws Exception
	{
		Constructor<NetworkInfo> ctor = NetworkInfo.class.getDeclaredConstructor(int.class);
		ctor.setAccessible(true);
		NetworkInfo networkInfo = ctor.newInstance(0);
		XposedHelpers.setIntField((Object) networkInfo, "mNetworkType", type);
		XposedHelpers.setObjectField((Object) networkInfo, "mTypeName", "WIFI");
		XposedHelpers.setObjectField((Object) networkInfo, "mState", NetworkInfo.State.CONNECTED);
		XposedHelpers.setObjectField((Object) networkInfo, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
		XposedHelpers.setBooleanField((Object) networkInfo, "mIsAvailable", true);
		return networkInfo;
	}

	public NetworkInfo createNetworkInfo2G(final int type, final boolean connected) throws Exception
	{
		Constructor<NetworkInfo> ctor = NetworkInfo.class.getDeclaredConstructor(int.class);
		ctor.setAccessible(true);
		NetworkInfo networkInfo = ctor.newInstance(0);
		XposedHelpers.setIntField((Object) networkInfo, "mNetworkType", type);
		XposedHelpers.setObjectField((Object) networkInfo, "mTypeName", "mobile");
		XposedHelpers.setObjectField((Object) networkInfo, "mState", NetworkInfo.State.CONNECTED);
		XposedHelpers.setObjectField((Object) networkInfo, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
		XposedHelpers.setObjectField((Object) networkInfo, "mReason", "dataEnabled");
		XposedHelpers.setObjectField((Object) networkInfo, "mExtraInfo", "3gnet");
		XposedHelpers.setBooleanField((Object) networkInfo, "mIsRoaming", false);
		XposedHelpers.setBooleanField((Object) networkInfo, "mIsFailover", true);
		XposedHelpers.setBooleanField((Object) networkInfo, "mIsAvailable", true);
		return networkInfo;
	}

	public NetworkInfo createNetworkInfo() throws Exception
	{
		NetworkInfo info = createNetworkInfo(ConnectivityManager.TYPE_MOBILE, true);
		return info;
	}

	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
	{
		// Check for LBE security master
		if (Util.hasLBE())
			return;
		if (prefs != null) {
			prefs.reload();
			
			XposedHelpers.findAndHookMethod("com.android.server.am.ActivityManagerService", lpparam.classLoader, "crashApplication", int.class, int.class, String.class, String.class, new XC_MethodHook()
			{

				@Override
				protected void beforeHookedMethod(MethodHookParam param)
						throws Throwable {
					System.out.println("Crash了 ltz+++++++++++++++");
					super.beforeHookedMethod(param);
				}
				
			});
			
			if (prefs.getBoolean(lpparam.packageName + "/mdatatype---", false)) {
				XposedHelpers.findAndHookMethod("android.net.ConnectivityManager", lpparam.classLoader, "getNetworkInfo", int.class, new XC_MethodHook()
				{
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						NetworkInfo mInfo = (NetworkInfo) param.getResult();

						int network_type = (Integer) param.args[0];
						String called = "getNetworkInfo(" + network_type + ")";
						if (network_type == ConnectivityManager.TYPE_WIFI) {
							NetworkInfo mNetworkInfo = NetworkInfo.class.getConstructor(NetworkInfo.class).newInstance(mInfo);
							try {
								Field mFieldmTypeName = NetworkInfo.class.getDeclaredField("mTypeName");
								mFieldmTypeName.setAccessible(true);
								mFieldmTypeName.set(mNetworkInfo, "mobile");

								Field mSubtypeName = NetworkInfo.class.getDeclaredField("mSubtypeName");
								mSubtypeName.setAccessible(true);
								mSubtypeName.set(mNetworkInfo, "EDGE");

								Field mExtraInfo = NetworkInfo.class.getDeclaredField("mExtraInfo");
								mExtraInfo.setAccessible(true);
								mExtraInfo.set(mNetworkInfo, "3gnet");

							} catch (Throwable ex) {
								// System.out.println("EX--->" +
								// ex.getMessage());
							}
							// System.out.println("xp-->" +
							// mNetworkInfo.toString() + "call-->" + called);
							param.setResult(mNetworkInfo);
						}

					}
				});
			}
			if (prefs.getBoolean(lpparam.packageName + "/mdatatype", false)) {// android.net.ConnectivityManager
																				// NetworkInfo.java
				XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.net.ConnectivityManager", lpparam.classLoader), "getActiveNetworkInfo", new XC_MethodHook()
				{// IConnectivityManager
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								// TODO Auto-generated method stub
								NetworkInfo mInfo = (NetworkInfo) param.getResult();
								Field mFieldmTypeName = NetworkInfo.class.getDeclaredField("mTypeName");
								mFieldmTypeName.setAccessible(true);
								mFieldmTypeName.set(mInfo, PrivacyManager.getDefacedProp(Process.myUid(), "DATATYPE"));
							}
						});

			}

			if (prefs.getBoolean(lpparam.packageName + "/updateDisplayInfoLocked", false)) {
				try{
					/*XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.view.Display", lpparam.classLoader), "updateDisplayInfoLocked", new XC_MethodHook()
					{
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable
						{
							String mString = (String) PrivacyManager.getDefacedProp(Process.myUid(), "DENSITY");
							String mUpperCase = mString.toUpperCase();
							String[] s = mUpperCase.split("X");
							Object mDisplayInfoW = XposedHelpers.getObjectField(param.thisObject, "mDisplayInfo");
							XposedHelpers.setIntField(mDisplayInfoW, "appWidth", new Integer(s[0]));
							Object mDisplayInfoH = XposedHelpers.getObjectField(param.thisObject, "mDisplayInfo");
							XposedHelpers.setIntField(mDisplayInfoH, "appHeight", new Integer(s[1]));
						}
					});*/
				}catch(Exception e){
					
				}
				
				XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.view.Display", lpparam.classLoader), "getMetrics", DisplayMetrics.class, new XC_MethodHook()
				{
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						// TODO Auto-generated method stub
						String mString = (String) PrivacyManager.getDefacedProp(Process.myUid(), "DENSITY");
						String mUpperCase = mString.toUpperCase();
						String[] s = mUpperCase.split("X");						
						XposedHelpers.setIntField(param.args[0], "widthPixels", new Integer(s[0]));
						XposedHelpers.setIntField(param.args[0], "heightPixels", new Integer(s[1]));
					}
				});
				
				XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.view.Display", lpparam.classLoader), "getWidth", new XC_MethodHook()
				{
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						// TODO Auto-generated method stub
						String mString = (String) PrivacyManager.getDefacedProp(Process.myUid(), "DENSITY");
						String mUpperCase = mString.toUpperCase();
						String[] s = mUpperCase.split("X");			
						param.setResult(new Integer(s[0]));
					}
				});
				
				XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.view.Display", lpparam.classLoader), "getHeight", new XC_MethodHook()
				{
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						// TODO Auto-generated method stub
						String mString = (String) PrivacyManager.getDefacedProp(Process.myUid(), "DENSITY");
						String mUpperCase = mString.toUpperCase();
						String[] s = mUpperCase.split("X");						
						param.setResult(new Integer(s[1]));
					}
				});
			}

			// Needs to be optimized
			if (prefs.getBoolean(lpparam.packageName + "/timeMachine", false)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				int MINUTE = calendar.get(Calendar.MINUTE);
				String stime = PrivacyManager.getSetting(0, PrivacyManager.cSettingTimeApp, "10");

				int mMinute = Integer.valueOf(stime);
				
				//TODO 指定随机数
				Random random = new Random();
				mMinute = random.nextInt(mMinute);
				
				
				// System.err.println(mMinute);
				calendar.add(Calendar.MINUTE, mMinute);
				String str = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(calendar.getTime());

				final long fakeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(str).getTime();
				final Long[] baseHolder = new Long[1];

				@SuppressWarnings("rawtypes")
				final Class contextWrapperClass = XposedHelpers.findClass("java.lang.System", lpparam.classLoader);
				XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.Activity", lpparam.classLoader), "onPause", new XC_MethodHook()
				{
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						// TODO Auto-generated method stub
						XposedHelpers.findAndHookMethod(contextWrapperClass, "currentTimeMillis", new XC_MethodHook()
						{
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								// TODO Auto-generated method
								// stub
								if (baseHolder[0] == null) {
									baseHolder[0] = (Long) param.getResult();
									return;
								}
								long baseTime = baseHolder[0];
								long currTime = (Long) param.getResult();
								param.setResult(currTime - baseTime + fakeTime);

							}

						});

					}
				});
				// System.out.println(System.currentTimeMillis() + "\t dddddd");
				PrivacyManager.setSetting(0, PrivacyManager.cSettingInterval, System.currentTimeMillis() + "");
				calendar.clear();

				XposedHelpers.findAndHookMethod(XposedHelpers.findClass("android.app.Activity", lpparam.classLoader), "onStart", new XC_MethodHook()
				{
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable
					{
						// TODO Auto-generated method stub
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date());
						int MINUTE = calendar.get(Calendar.MINUTE);
						String stime = PrivacyManager.getSetting(0, PrivacyManager.cSettingInterval, "10");
						String str = null;
						final long fakeTime;

						String sint = PrivacyManager.getSetting(0, PrivacyManager.cSettingInterval, "10");
						if (sint.equals("10")) {
							int mMinute = Integer.valueOf(stime);
							// System.err.println(mMinute);
							calendar.add(Calendar.MINUTE, mMinute);
							str = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(calendar.getTime());
							fakeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(str).getTime();
						} else {
							fakeTime = Long.parseLong(sint);
						}

						final Long[] baseHolder = new Long[1];
						XposedHelpers.findAndHookMethod(contextWrapperClass, "currentTimeMillis", new XC_MethodHook()
						{
							@Override
							protected void afterHookedMethod(MethodHookParam param) throws Throwable
							{
								// TODO Auto-generated method
								// stub
								if (baseHolder[0] == null) {
									baseHolder[0] = (Long) param.getResult();
									return;
								}
								long baseTime = baseHolder[0];
								long currTime = (Long) param.getResult();
								param.setResult(currTime - baseTime + fakeTime);
							}
						});
						// calendar.clear();
					}
				});

			}

		}
		handleLoadPackage(lpparam.packageName, lpparam.classLoader, mSecret);
	}

	// Cydia
	public static void initialize()
	{
		mCydia = true;
		init(null);

		// Self
		MS.hookClassLoad(Util.class.getName(), new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XUtilHook.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// TODO: Cydia: Build.SERIAL

		// Activity recognition
		MS.hookClassLoad("com.google.android.gms.location.ActivityRecognitionClient", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XActivityRecognitionClient.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Advertising Id
		MS.hookClassLoad("com.google.android.gms.ads.identifier.AdvertisingIdClient", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XAdvertisingIdClientInfo.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Google auth
		MS.hookClassLoad("com.google.android.gms.auth.GoogleAuthUtil", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XGoogleAuthUtil.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// GoogleApiClient.Builder
		MS.hookClassLoad("com.google.android.gms.common.api.GoogleApiClient", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XGoogleApiClient.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Google Map V1
		MS.hookClassLoad("com.google.android.maps.GeoPoint", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XGoogleMapV1.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Google Map V2
		MS.hookClassLoad("com.google.android.gms.maps.GoogleMap", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XGoogleMapV2.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Location client
		MS.hookClassLoad("com.google.android.gms.location.LocationClient", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XLocationClient.getInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Phone interface manager
		MS.hookClassLoad("com.android.phone.PhoneInterfaceManager", new MS.ClassLoadHook()
		{
			@Override
			public void classLoaded(Class<?> clazz)
			{
				hookAll(XTelephonyManager1.getPhoneInstances(), clazz.getClassLoader(), mSecret);
			}
		});

		// Providers
		for (final String className : XContentResolver.cProviderClassName)
			MS.hookClassLoad(className, new MS.ClassLoadHook()
			{
				@Override
				public void classLoaded(Class<?> clazz)
				{
					hookAll(XContentResolver.getInstances(className), clazz.getClassLoader(), mSecret);
				}
			});
	}

	// Common
	private static void init(String path)
	{
		Util.log(null, Log.WARN, "Init path=" + path);

		// Generate secret
		mSecret = Long.toHexString(new Random().nextLong());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			try {
				Class<?> libcore = Class.forName("libcore.io.Libcore");
				Field fOs = libcore.getDeclaredField("os");
				fOs.setAccessible(true);
				Object os = fOs.get(null);
				Method setenv = os.getClass().getMethod("setenv", String.class, String.class, boolean.class);
				setenv.setAccessible(true);
				boolean aosp = new File("/data/system/hytmotest/aosp").exists();
				setenv.invoke(os, "XPrivacy.AOSP", Boolean.toString(aosp), false);
				Util.log(null, Log.WARN, "AOSP mode forced=" + aosp);
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}

		// System server
		try {
			// frameworks/base/services/java/com/android/server/SystemServer.java
			Class<?> cSystemServer = Class.forName("com.android.server.SystemServer");
			Method mMain = cSystemServer.getDeclaredMethod("main", String[].class);
			if (mCydia)
				MS.hookMethod(cSystemServer, mMain, new MS.MethodAlteration<Object, Void>()
				{
					@Override
					public Void invoked(Object thiz, Object... args) throws Throwable
					{
						PrivacyService.register(mListHookError, mSecret);
						return invoke(thiz, args);
					}
				});
			else
				XposedBridge.hookMethod(mMain, new XC_MethodHook()
				{
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable
					{
						PrivacyService.register(mListHookError, mSecret);
					}
				});
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
		
		// Account manager
		hookAll(XAccountManager.getInstances(null), null, mSecret);

		// Activity manager
		hookAll(XActivityManager.getInstances(null), null, mSecret);

		// Activity manager service
		hookAll(XActivityManagerService.getInstances(), null, mSecret);

		// App widget manager
		hookAll(XAppWidgetManager.getInstances(), null, mSecret);

		// Application
		hookAll(XApplication.getInstances(), null, mSecret);

		// Audio record
		hookAll(XAudioRecord.getInstances(), null, mSecret);

		// Binder device
		hookAll(XBinder.getInstances(), null, mSecret);

		// Bluetooth adapater
		hookAll(XBluetoothAdapter.getInstances(), null, mSecret);

		// Bluetooth device
		hookAll(XBluetoothDevice.getInstances(), null, mSecret);

		// Camera
		hookAll(XCamera.getInstances(), null, mSecret);

		// Camera2 device
		hookAll(XCameraDevice2.getInstances(), null, mSecret);

		// Clipboard manager
		hookAll(XClipboardManager.getInstances(null), null, mSecret);

		// Connectivity manager
		hookAll(XConnectivityManager.getInstances(null), null, mSecret);

		// Content resolver
		hookAll(XContentResolver.getInstances(null), null, mSecret);

		// Context wrapper
		hookAll(XContextImpl.getInstances(), null, mSecret);

		// Environment
		hookAll(XEnvironment.getInstances(), null, mSecret);

		// Inet address
		hookAll(XInetAddress.getInstances(), null, mSecret);

		// Input device
		hookAll(XInputDevice.getInstances(), null, mSecret);

		// Intent firewall
		hookAll(XIntentFirewall.getInstances(), null, mSecret);

		// IO bridge
		hookAll(XIoBridge.getInstances(), null, mSecret);

		// Location manager
		hookAll(XLocationManager.getInstances(null), null, mSecret);

		// Media recorder
		hookAll(XMediaRecorder.getInstances(), null, mSecret);

		// Network info
		hookAll(XNetworkInfo.getInstances(), null, mSecret);

		// Network interface
		hookAll(XNetworkInterface.getInstances(), null, mSecret);

		// NFC adapter
		hookAll(XNfcAdapter.getInstances(), null, mSecret);

		// Package manager service
		hookAll(XPackageManager.getInstances(null), null, mSecret);

		// Process
		hookAll(XProcess.getInstances(), null, mSecret);

		// Process builder
		hookAll(XProcessBuilder.getInstances(), null, mSecret);

		// Resources
		hookAll(XResources.getInstances(), null, mSecret);

		// Runtime
		hookAll(XRuntime.getInstances(), null, mSecret);

		// Sensor manager
		hookAll(XSensorManager.getInstances(null), null, mSecret);

		// Settings secure
		// TODO: Cydia: android.provider.Settings.Secure
		if (!mCydia)
			hookAll(XSettingsSecure.getInstances(), null, mSecret);

		// SIP manager
		hookAll(XSipManager.getInstances(), null, mSecret);

		// SMS manager
		hookAll(XSmsManager.getInstances(), null, mSecret);

		// System properties
		hookAll(XSystemProperties.getInstances(), null, mSecret);

		// Telephone service
		hookAll(XTelephonyManager1.getInstances(null), null, mSecret);

		// USB device
		hookAll(XUsbDevice.getInstances(), null, mSecret);

		// Web view
		hookAll(XWebView.getInstances(), null, mSecret);

		// Window service
		hookAll(XWindowManager.getInstances(null), null, mSecret);

		// Wi-Fi service
		hookAll(XWifiManager.getInstances(null), null, mSecret);

		// Intent receive
		hookAll(XActivityThread.getInstances(), null, mSecret);

		// Intent send
		hookAll(XActivity.getInstances(), null, mSecret);
	}

	private static void handleLoadPackage(String packageName, ClassLoader classLoader, String secret)
	{
		Util.log(null, Log.INFO, "Load package=" + packageName + " uid=" + Process.myUid());

		// Skip hooking self
		String self = XPrivacy.class.getPackage().getName();
		if (packageName.equals(self)) {
			hookAll(XUtilHook.getInstances(), classLoader, secret);
			return;
		}

		// Build SERIAL
		if (PrivacyManager.getRestrictionExtra(null, Process.myUid(), PrivacyManager.cIdentification, "SERIAL", null, Build.SERIAL, secret))
			try {
				Field serial = Build.class.getField("SERIAL");
				serial.setAccessible(true);
				serial.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "SERIAL"));
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}

		// Build MODEL
		if (PrivacyManager.getRestriction(null, Process.myUid(), PrivacyManager.cIdentification, "MODEL", secret)){
			try {
				Field mmod = Build.class.getField("MODEL");
				mmod.setAccessible(true);
				mmod.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "MODEL"));
				
				//TODO SDK_INI不对，猜猜这样改试试
				/*Field msdkini = Build.VERSION.class.getField("SDK_INT");
				msdkini.setAccessible(true);
				msdkini.set(null, new Random().nextInt(20));*/
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}
		// Build MANUFACTURER
		if (PrivacyManager.getRestriction(null, Process.myUid(), PrivacyManager.cIdentification, "MANUFACTURER", secret))
			try {
				Field mmod = Build.class.getField("MANUFACTURER");
				mmod.setAccessible(true);
				mmod.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "MANUFACTURER"));
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		
		// Build PRODUCT,BRAND
		if (PrivacyManager.getRestriction(null, Process.myUid(), PrivacyManager.cIdentification, "PRODUCT", secret)){
			try {
				Field mmod = Build.class.getField("PRODUCT");
				mmod.setAccessible(true);
				mmod.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "PRODUCT"));
				
				Field mmod1 = Build.class.getField("BRAND");
				mmod1.setAccessible(true);
				mmod1.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "PRODUCT"));
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}

		// Android 鐗堟湰
		if (PrivacyManager.getRestriction(null, Process.myUid(), PrivacyManager.cIdentification, "RELEASE", secret))
			try {
				Field mmod = Build.VERSION.class.getField("RELEASE");
				mmod.setAccessible(true);
				mmod.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "RELEASE"));
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		// 鍐呴儴 鐗堟湰
		if (PrivacyManager.getRestriction(null, Process.myUid(), PrivacyManager.cIdentification, "ID", secret))
			try {
				Field mmod = Build.class.getField("ID");
				mmod.setAccessible(true);
				mmod.set(null, PrivacyManager.getDefacedProp(Process.myUid(), "PRODUCT"));
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}

		// Activity recognition
		try {
			Class.forName("com.google.android.gms.location.ActivityRecognitionClient", false, classLoader);
			hookAll(XActivityRecognitionClient.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// Advertising Id
		try {
			Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient$Info", false, classLoader);
			hookAll(XAdvertisingIdClientInfo.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// Google auth
		try {
			Class.forName("com.google.android.gms.auth.GoogleAuthUtil", false, classLoader);
			hookAll(XGoogleAuthUtil.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// GoogleApiClient.Builder
		try {
			Class.forName("com.google.android.gms.common.api.GoogleApiClient$Builder", false, classLoader);
			hookAll(XGoogleApiClient.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// Google Map V1
		try {
			Class.forName("com.google.android.maps.GeoPoint", false, classLoader);
			hookAll(XGoogleMapV1.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// Google Map V2
		try {
			Class.forName("com.google.android.gms.maps.GoogleMap", false, classLoader);
			hookAll(XGoogleMapV2.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// Location client
		try {
			Class.forName("com.google.android.gms.location.LocationClient", false, classLoader);
			hookAll(XLocationClient.getInstances(), classLoader, secret);
		} catch (Throwable ignored) {
		}

		// Phone interface manager
		if ("com.android.phone".equals(packageName))
			hookAll(XTelephonyManager1.getPhoneInstances(), classLoader, secret);

		// Providers
		hookAll(XContentResolver.getPackageInstances(packageName, classLoader), classLoader, secret);
	}

	public static void handleGetSystemService(String name, String className, String secret)
	{
		if (PrivacyManager.getTransient(className, null) == null) {
			PrivacyManager.setTransient(className, Boolean.toString(true));

			if (name.equals(Context.ACCOUNT_SERVICE))
				hookAll(XAccountManager.getInstances(className), null, secret);
			else if (name.equals(Context.ACTIVITY_SERVICE))
				hookAll(XActivityManager.getInstances(className), null, secret);
			else if (name.equals(Context.CLIPBOARD_SERVICE))
				hookAll(XClipboardManager.getInstances(className), null, secret);
			else if (name.equals(Context.CONNECTIVITY_SERVICE))
				hookAll(XConnectivityManager.getInstances(className), null, secret);
			else if (name.equals(Context.LOCATION_SERVICE))
				hookAll(XLocationManager.getInstances(className), null, secret);
			else if (name.equals("PackageManager"))
				hookAll(XPackageManager.getInstances(className), null, secret);
			else if (name.equals(Context.SENSOR_SERVICE))
				hookAll(XSensorManager.getInstances(className), null, secret);
			else if (name.equals(Context.TELEPHONY_SERVICE))
				hookAll(XTelephonyManager1.getInstances(className), null, secret);
			else if (name.equals(Context.WINDOW_SERVICE))
				hookAll(XWindowManager.getInstances(className), null, secret);
			else if (name.equals(Context.WIFI_SERVICE))
				hookAll(XWifiManager.getInstances(className), null, secret);
		}
	}

	public static void hookAll(List<XHook> listHook, ClassLoader classLoader, String secret)
	{
		for (XHook hook : listHook)
			if (hook.getRestrictionName() == null)
				hook(hook, classLoader, secret);
			else {
				CRestriction crestriction = new CRestriction(0, hook.getRestrictionName(), null, null);
				CRestriction mrestriction = new CRestriction(0, hook.getRestrictionName(), hook.getMethodName(), null);
				if (mListDisabled.contains(crestriction) || mListDisabled.contains(mrestriction))
					Util.log(hook, Log.WARN, "Skipping " + hook);
				else
					hook(hook, classLoader, secret);
			}
	}

	private static void hook(final XHook hook, ClassLoader classLoader, String secret)
	{
		// Get meta data
		Hook md = PrivacyManager.getHook(hook.getRestrictionName(), hook.getSpecifier());
		if (md == null) {
			String message = "Not found hook=" + hook;
			mListHookError.add(message);
			Util.log(hook, Log.ERROR, message);
		} else if (!md.isAvailable())
			return;

		// Provide secret
		if (secret == null)
			Util.log(hook, Log.ERROR, "Secret missing hook=" + hook);
		hook.setSecret(secret);

		try {
			// Find class
			Class<?> hookClass = null;
			try {
				if (mCydia)
					hookClass = Class.forName(hook.getClassName(), false, classLoader);
				else
					hookClass = findClass(hook.getClassName(), classLoader);
			} catch (Throwable ex) {
				String message = "Class not found hook=" + hook;
				int level = (md != null && md.isOptional() ? Log.WARN : Log.ERROR);
				if ("isXposedEnabled".equals(hook.getMethodName()))
					level = Log.WARN;
				if (level == Log.ERROR)
					mListHookError.add(message);
				Util.log(hook, level, message);
				Util.logStack(hook, level);
			}

			// Get members
			List<Member> listMember = new ArrayList<Member>();
			// TODO: enable/disable superclass traversal
			Class<?> clazz = hookClass;
			while (clazz != null && !"android.content.ContentProvider".equals(clazz.getName()))
				try {
					if (hook.getMethodName() == null) {
						for (Constructor<?> constructor : clazz.getDeclaredConstructors())
							if (!Modifier.isAbstract(constructor.getModifiers()) && Modifier.isPublic(constructor.getModifiers()) ? hook.isVisible() : !hook.isVisible())
								listMember.add(constructor);
						break;
					} else {
						for (Method method : clazz.getDeclaredMethods())
							if (method.getName().equals(hook.getMethodName()) && !Modifier.isAbstract(method.getModifiers()) && (Modifier.isPublic(method.getModifiers()) ? hook.isVisible() : !hook.isVisible()))
								listMember.add(method);
					}
					clazz = clazz.getSuperclass();
				} catch (Throwable ex) {
					if (ex.getClass().equals(ClassNotFoundException.class))
						break;
					else
						throw ex;
				}

			// Hook members
			for (Member member : listMember)
				try {
					if (mCydia) {
						XMethodAlteration alteration = new XMethodAlteration(hook, member);
						if (member instanceof Method)
							MS.hookMethod(member.getDeclaringClass(), (Method) member, alteration);
						else
							MS.hookMethod(member.getDeclaringClass(), (Constructor<?>) member, alteration);
					} else
						XposedBridge.hookMethod(member, new XMethodHook(hook));
				} catch (NoSuchFieldError ex) {
					Util.log(hook, Log.WARN, ex.toString());
				} catch (Throwable ex) {
					mListHookError.add(ex.toString());
					Util.bug(hook, ex);
				}

			// Check if members found
			if (listMember.isEmpty() && !hook.getClassName().startsWith("com.google.android.gms")) {
				String message = "Method not found hook=" + hook;
				int level = (md != null && md.isOptional() ? Log.WARN : Log.ERROR);
				if ("isXposedEnabled".equals(hook.getMethodName()))
					level = Log.WARN;
				if (level == Log.ERROR)
					mListHookError.add(message);
				Util.log(hook, level, message);
				Util.logStack(hook, level);
			}
		} catch (Throwable ex) {
			mListHookError.add(ex.toString());
			Util.bug(hook, ex);
		}
	}

	// Helper classes

	private static class XMethodHook extends XC_MethodHook
	{
		private XHook mHook;

		public XMethodHook(XHook hook)
		{
			mHook = hook;
		}

		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable
		{
			try {
				// Do not restrict Zygote
				if (Process.myUid() <= 0)
					return;

				// Pre processing
				XParam xparam = XParam.fromXposed(param);

				long start = System.currentTimeMillis();

				// Execute hook
				try{
				mHook.before(xparam);
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
				long ms = System.currentTimeMillis() - start;
				if (ms > PrivacyManager.cWarnHookDelayMs)
					Util.log(mHook, Log.WARN, String.format("%s %d ms", param.method.getName(), ms));

				// Post processing
				if (xparam.hasResult())
					param.setResult(xparam.getResult());
				if (xparam.hasThrowable())
					param.setThrowable(xparam.getThrowable());
				param.setObjectExtra("xextra", xparam.getExtras());
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
		}

		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable
		{
			if (!param.hasThrowable())
				try {
					// Do not restrict Zygote
					if (Process.myUid() <= 0)
						return;

					// Pre processing
					XParam xparam = XParam.fromXposed(param);
					xparam.setExtras(param.getObjectExtra("xextra"));

					long start = System.currentTimeMillis();

					// Execute hook
					mHook.after(xparam);

					long ms = System.currentTimeMillis() - start;
					if (ms > PrivacyManager.cWarnHookDelayMs)
						Util.log(mHook, Log.WARN, String.format("%s %d ms", param.method.getName(), ms));

					// Post processing
					if (xparam.hasResult())
						param.setResult(xparam.getResult());
					if (xparam.hasThrowable())
						param.setThrowable(xparam.getThrowable());
				} catch (Throwable ex) {
					Util.bug(null, ex);
				}
		}
	};

	private static class XMethodAlteration extends MS.MethodAlteration<Object, Object>
	{
		private XHook mHook;
		private Member mMember;

		public XMethodAlteration(XHook hook, Member member)
		{
			mHook = hook;
			mMember = member;
		}

		@Override
		public Object invoked(Object thiz, Object... args) throws Throwable
		{
			if (Process.myUid() <= 0)
				return invoke(thiz, args);

			XParam xparam = XParam.fromCydia(mMember, thiz, args);
			mHook.before(xparam);

			if (!xparam.hasResult() || xparam.hasThrowable()) {
				try {
					Object result = invoke(thiz, args);
					xparam.setResult(result);
				} catch (Throwable ex) {
					xparam.setThrowable(ex);
				}

				mHook.after(xparam);
			}

			if (xparam.hasThrowable())
				throw xparam.getThrowable();
			return xparam.getResult();
		}
	}
}
