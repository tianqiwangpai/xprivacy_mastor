package com.hy.xp.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.hy.xp.app.task.PhoneDataBean;
import com.hy.xp.app.task.SetData;

public class PrivacyManager
{
	public static final boolean cVersion3 = true;

	// This should correspond with restrict_<name> in strings.xml
	public static final String cAccounts = "accounts";
	public static final String cBrowser = "browser";
	public static final String cCalendar = "calendar";
	public static final String cCalling = "calling";
	public static final String cClipboard = "clipboard";
	public static final String cContacts = "contacts";
	public static final String cDictionary = "dictionary";
	public static final String cEMail = "email";
	public static final String cIdentification = "identification";
	public static final String cInternet = "internet";
	public static final String cIPC = "ipc";
	public static final String cLocation = "location";
	public static final String cMedia = "media";
	public static final String cMessages = "messages";
	public static final String cNetwork = "network";
	public static final String cNfc = "nfc";
	public static final String cNotifications = "notifications";
	public static final String cOverlay = "overlay";
	public static final String cPhone = "phone";
	public static final String cSensors = "sensors";
	public static final String cShell = "shell";
	public static final String cStorage = "storage";
	public static final String cSystem = "system";
	public static final String cView = "view";

	// This should correspond with the above definitions
	//private static final String cRestrictionNames[] = new String[] { cAccounts, cBrowser, cCalendar, cCalling, cClipboard, cContacts, cDictionary, cEMail, cIdentification, cInternet, cIPC, cLocation, cMedia, cMessages, cNetwork, cNfc, cNotifications, cOverlay, cPhone, cSensors, cShell, cStorage, cSystem, cView };

	private static final String cRestrictionNames[] = { "accounts", "identification", "location", "network", "phone", "storage" };

	public static List<String> cMethodNoState = Arrays.asList(new String[] { "IntentFirewall", "checkPermission", "checkUidPermission" });

	// Setting names
	public final static String cSettingSerial = "Serial";
	public final static String cSettingLatitude = "Latitude";
	public final static String cSettingLongitude = "Longitude";
	public final static String cSettingAltitude = "Altitude";
	public final static String cSettingMac = "Mac";
	public final static String cSettingIP = "IP";
	public final static String cSettingImei = "IMEI";
	public final static String cSettingPhone = "Phone";
	public final static String cSettingId = "ID";
	public final static String cSettingGsfId = "GSF_ID";
	public final static String cSettingAdId = "AdId";
	public final static String cSettingMcc = "MCC";
	public final static String cSettingMnc = "MNC";
	public final static String cSettingCountry = "Country";
	public final static String cSettingOperator = "Operator";
	public final static String cSettingIccId = "ICC_ID";
	public final static String cSettingSubscriber = "Subscriber";
	public final static String cSettingSSID = "SSID";
	public final static String cSettingUa = "UA";
	public final static String cSettingOpenTab = "OpenTab";
	public final static String cSettingSelectedCategory = "SelectedCategory";
	public final static String cSettingFUsed = "FUsed";
	public final static String cSettingFInternet = "FInternet";
	public final static String cSettingFRestriction = "FRestriction";
	public final static String cSettingFRestrictionNot = "FRestrictionNot";
	public final static String cSettingFPermission = "FPermission";
	public final static String cSettingFOnDemand = "FOnDemand";
	public final static String cSettingFOnDemandNot = "FOnDemandNot";
	public final static String cSettingFUser = "FUser";
	public final static String cSettingFSystem = "FSystem";
	public final static String cSettingSortMode = "SortMode";
	public final static String cSettingSortInverted = "SortInverted";
	public final static String cSettingModifyTime = "ModifyTime";
	public final static String cSettingTheme = "Theme";
	public final static String cSettingSalt = "Salt";
	public final static String cSettingVersion = "Version";
	public final static String cSettingNotify = "Notify";
	public final static String cSettingLog = "Log";
	public final static String cSettingDangerous = "Dangerous";
	public final static String cSettingExperimental = "Experimental";
	public final static String cSettingRandom = "Random@boot";
	public final static String cSettingState = "State";
	public final static String cSettingConfidence = "Confidence";
	public final static String cSettingHttps = "Https";
	public final static String cSettingRegistered = "Registered";
	public final static String cSettingUsage = "UsageData";
	public final static String cSettingParameters = "Parameters";
	public final static String cSettingValues = "Values";
	public final static String cSettingSystem = "RestrictSystem";
	public final static String cSettingRestricted = "Retricted";
	public final static String cSettingOnDemand = "OnDemand";
	public final static String cSettingMigrated = "Migrated";
	public final static String cSettingCid = "Cid";
	public final static String cSettingLac = "Lac";
	public final static String cSettingBlacklist = "Blacklist";
	public final static String cSettingResolve = "Resolve";
	public final static String cSettingNoResolve = "NoResolve";
	public final static String cSettingFreeze = "Freeze";
	public final static String cSettingPermMan = "PermMan";
	public final static String cSettingIntentWall = "IntentWall";
	public final static String cSettingSafeMode = "SafeMode";
	public final static String cSettingTestVersions = "TestVersions";
	public final static String cSettingOnDemandSystem = "OnDemandSystem";
	public final static String cSettingLegacy = "Legacy";
	public final static String cSettingAOSPMode = "AOSPMode";
	// public final static String cSettingUpdates = "Updates";
	public final static String cSettingMethodExpert = "MethodExpert";
	public final static String cSettingWhitelistNoModify = "WhitelistNoModify";

	public final static String cSettingODExpert = "ODExpert";
	public final static String cSettingODCategory = "ODCategory";
	public final static String cSettingODOnce = "ODOnce";
	public final static String cSettingODOnceDuration = "ODOnceDuration";
	// new add data
	public final static String cSettingModel = "model";
	public final static String cSettingManufacturer = "manufacturer";
	public final static String cSettingProduct = "product";
	public final static String cSettingDataType = "datatype";
	public final static String cSettingAndroidCode = "androidCode";
	public final static String cSettingSystemCOde = "systemcode";
	public final static String cSettingDensity = "density";
	public final static String cSettingTimeApp = "timeapp";
	// public final static String cSettingClearAppid = "clearappid";

	// ¼ä¸ô
	public final static String cSettingInterval = "interval";

	// Special value names
	public final static String cValueRandom = "#Random#";
	public final static String cValueRandomLegacy = "\nRandom\n";

	// Constants
	public final static int cXposedAppProcessMinVersion = 46;
	public final static int cWarnServiceDelayMs = 200;
	public final static int cWarnHookDelayMs = 200;

	private final static int cMaxExtra = 128;
	private final static String cDeface = "DEFACE";

	// Caching
	public final static int cRestrictionCacheTimeoutMs = 15 * 1000;
	public final static int cSettingCacheTimeoutMs = 30 * 1000;
	private static Map<String, Map<String, Hook>> mMethod = new LinkedHashMap<String, Map<String, Hook>>();
	private static Map<String, List<String>> mRestart = new LinkedHashMap<String, List<String>>();
	private static Map<String, List<Hook>> mPermission = new LinkedHashMap<String, List<Hook>>();
	private static Map<CSetting, CSetting> mSettingsCache = new HashMap<CSetting, CSetting>();
	private static Map<CSetting, CSetting> mTransientCache = new HashMap<CSetting, CSetting>();
	private static Map<CRestriction, CRestriction> mRestrictionCache = new HashMap<CRestriction, CRestriction>();
	private static SparseArray<Map<String, Boolean>> mPermissionRestrictionCache = new SparseArray<Map<String, Boolean>>();
	private static SparseArray<Map<Hook, Boolean>> mPermissionHookCache = new SparseArray<Map<Hook, Boolean>>();

	// Meta data

	static {
		List<Hook> listHook = Meta.get();
		List<String> listRestriction = getRestrictions();
		for (Hook hook : listHook) {
			String restrictionName = hook.getRestrictionName();
			if (restrictionName == null)
				restrictionName = "";

			// Check restriction
			else if (!listRestriction.contains(restrictionName))
				if (hook.isAvailable())
					Util.log(null, Log.WARN, "Not found restriction=" + restrictionName + " hook=" + hook);

			// Enlist method
			if (!mMethod.containsKey(restrictionName))
				mMethod.put(restrictionName, new HashMap<String, Hook>());
			mMethod.get(restrictionName).put(hook.getName(), hook);

			// Cache restart required methods
			if (hook.isRestartRequired()) {
				if (!mRestart.containsKey(restrictionName))
					mRestart.put(restrictionName, new ArrayList<String>());
				mRestart.get(restrictionName).add(hook.getName());
			}

			// Enlist permissions
			String[] permissions = hook.getPermissions();
			if (permissions != null)
				for (String perm : permissions)
					if (!perm.equals("")) {
						String aPermission = (perm.contains(".") ? perm : "android.permission." + perm);
						if (!mPermission.containsKey(aPermission))
							mPermission.put(aPermission, new ArrayList<Hook>());
						if (!mPermission.get(aPermission).contains(hook))
							mPermission.get(aPermission).add(hook);
					}
		}
		Util.log(null, Log.WARN, listHook.size() + " hooks");
	}

	public static List<String> getRestrictions()
	{
		List<String> listRestriction = new ArrayList<String>(Arrays.asList(cRestrictionNames));
		if (Hook.isAOSP(19))
			listRestriction.remove(cIPC);
		return listRestriction;
	}

	public static TreeMap<String, String> getRestrictions(Context context)
	{
		Collator collator = Collator.getInstance(Locale.getDefault());
		TreeMap<String, String> tmRestriction = new TreeMap<String, String>(collator);
		for (String restrictionName : getRestrictions()) {
			int stringId = context.getResources().getIdentifier("restrict_" + restrictionName, "string", context.getPackageName());
			tmRestriction.put(stringId == 0 ? restrictionName : context.getString(stringId), restrictionName);
		}
		return tmRestriction;
	}

	public static Hook getHook(String _restrictionName, String methodName)
	{
		String restrictionName = (_restrictionName == null ? "" : _restrictionName);
		if (mMethod.containsKey(restrictionName))
			if (mMethod.get(restrictionName).containsKey(methodName))
				return mMethod.get(restrictionName).get(methodName);
		return null;
	}

	public static List<Hook> getHooks(String restrictionName, Version version)
	{
		List<Hook> listMethod = new ArrayList<Hook>();
		for (String methodName : mMethod.get(restrictionName).keySet()) {
			Hook hook = mMethod.get(restrictionName).get(methodName);

			if (!hook.isAvailable())
				continue;

			if (version != null && hook.getFrom() != null && version.compareTo(hook.getFrom()) < 0)
				continue;

			if ("IntentFirewall".equals(hook.getName()))
				if (!PrivacyManager.getSettingBool(0, PrivacyManager.cSettingIntentWall, false))
					continue;

			if ("checkPermission".equals(hook.getName()) || "checkUidPermission".equals(hook.getName()))
				if (!PrivacyManager.getSettingBool(0, PrivacyManager.cSettingPermMan, false))
					continue;

			listMethod.add(mMethod.get(restrictionName).get(methodName));
		}
		Collections.sort(listMethod);
		return listMethod;
	}

	public static List<String> getPermissions(String restrictionName, Version version)
	{
		List<String> listPermission = new ArrayList<String>();
		for (Hook md : getHooks(restrictionName, version))
			if (md.getPermissions() != null)
				for (String permission : md.getPermissions())
					if (!listPermission.contains(permission))
						listPermission.add(permission);
		return listPermission;
	}

	// Restrictions

	public static PRestriction getRestrictionEx(int uid, String restrictionName, String methodName)
	{
		PRestriction query = new PRestriction(uid, restrictionName, methodName, false);
		PRestriction result = new PRestriction(uid, restrictionName, methodName, false, true);
		try {
			// Check cache
			boolean cached = false;
			CRestriction key = new CRestriction(uid, restrictionName, methodName, null);
			synchronized (mRestrictionCache) {
				if (mRestrictionCache.containsKey(key)) {
					CRestriction entry = mRestrictionCache.get(key);
					if (!entry.isExpired()) {
						cached = true;
						result.restricted = entry.restricted;
						result.asked = entry.asked;
					}
				}
			}

			if (!cached) {
				// Get restriction
				result = PrivacyService.getRestrictionProxy(query, false, "");
				if (result.debug)
					Util.logStack(null, Log.WARN);

				// Add to cache
				key.restricted = result.restricted;
				key.asked = result.asked;
				if (result.time > 0) {
					key.setExpiry(result.time);
					Util.log(null, Log.WARN, "Caching " + result + " until " + new Date(result.time));
				}
				synchronized (mRestrictionCache) {
					if (mRestrictionCache.containsKey(key))
						mRestrictionCache.remove(key);
					mRestrictionCache.put(key, key);
				}
			}
		} catch (RemoteException ex) {
			Util.bug(null, ex);
		}
		return result;
	}

	public static boolean getRestriction(final XHook hook, int uid, String restrictionName, String methodName, String secret)
	{
		return getRestrictionExtra(hook, uid, restrictionName, methodName, null, null, secret);
	}

	public static boolean getRestrictionExtra(final XHook hook, int uid, String restrictionName, String methodName, String extra, String secret)
	{
		return getRestrictionExtra(hook, uid, restrictionName, methodName, extra, null, secret);
	}

	public static boolean getRestrictionExtra(final XHook hook, int uid, String restrictionName, String methodName, String extra, String value, String secret)
	{
		// System.out.println("getRestricted--->restrictionName=" +
		// restrictionName + "\tmethodName=" + methodName + "\textra=" + extra +
		// "\tvalue=" + value + "\tsecret=" + secret);

		long start = System.currentTimeMillis();
		PRestriction result = new PRestriction(uid, restrictionName, methodName, false, true);

		// Check uid
		if (uid <= 0)
			return false;
		if (secret == null) {
			Util.log(null, Log.ERROR, "Secret missing restriction=" + restrictionName + "/" + methodName);
			Util.logStack(hook, Log.ERROR);
			secret = "";
		}

		// Check restriction
		if (restrictionName == null || restrictionName.equals("")) {
			Util.log(hook, Log.ERROR, "restriction empty method=" + methodName);
			Util.logStack(hook, Log.ERROR);
			return false;
		}

		// Check usage
		if (methodName == null || methodName.equals("")) {
			Util.log(hook, Log.ERROR, "Method empty");
			Util.logStack(hook, Log.ERROR);
		} else if (getHook(restrictionName, methodName) == null) {
			Util.log(hook, Log.ERROR, "Unknown method=" + methodName);
			Util.logStack(hook, Log.ERROR);
		}

		// Check extra
		if (extra != null && extra.length() > cMaxExtra)
			extra = extra.substring(0, cMaxExtra) + "...";
		result.extra = extra;

		// Check cache
		boolean cached = false;
		CRestriction key = new CRestriction(uid, restrictionName, methodName, extra);
		synchronized (mRestrictionCache) {
			if (mRestrictionCache.containsKey(key)) {
				CRestriction entry = mRestrictionCache.get(key);
				if (!entry.isExpired()) {
					cached = true;
					result.restricted = entry.restricted;
					result.asked = entry.asked;
				}
			}
		}

		// Get restriction
		if (!cached)
			try {
				PRestriction query = new PRestriction(uid, restrictionName, methodName, false);
				query.extra = extra;
				query.value = value;
				PRestriction restriction = PrivacyService.getRestrictionProxy(query, true, secret);
				result.restricted = restriction.restricted;
				if (restriction.debug)
					Util.logStack(null, Log.WARN);

				// Add to cache
				if (result.time >= 0) {
					key.restricted = result.restricted;
					key.asked = result.asked;
					if (result.time > 0) {
						key.setExpiry(result.time);
						Util.log(null, Log.WARN, "Caching " + result + " until " + new Date(result.time));
					}
					synchronized (mRestrictionCache) {
						if (mRestrictionCache.containsKey(key))
							mRestrictionCache.remove(key);
						mRestrictionCache.put(key, key);
					}
				}
			} catch (Throwable ex) {
				Util.bug(hook, ex);
			}

		// Result
		long ms = System.currentTimeMillis() - start;
		Util.log(hook, ms < cWarnServiceDelayMs ? Log.INFO : Log.WARN, String.format("Get client %s%s %d ms", result, (cached ? " (cached)" : ""), ms));

		return result.restricted;
	}

	public static void setRestriction(int uid, String restrictionName, String methodName, boolean restricted, boolean asked)
	{
		checkCaller();

		// Check uid
		if (uid == 0) {
			Util.log(null, Log.WARN, "uid=0");
			return;
		}

		// Build list of restrictions
		List<String> listRestriction = new ArrayList<String>();
		if (restrictionName == null)
			listRestriction.addAll(PrivacyManager.getRestrictions());
		else
			listRestriction.add(restrictionName);

		// Create list of restrictions to set
		List<PRestriction> listPRestriction = new ArrayList<PRestriction>();
		for (String rRestrictionName : listRestriction)
			listPRestriction.add(new PRestriction(uid, rRestrictionName, methodName, restricted, asked));

		// Make exceptions
		if (methodName == null)
			for (String rRestrictionName : listRestriction)
				for (Hook md : getHooks(rRestrictionName, null)) {
					if (!canRestrict(uid, Process.myUid(), rRestrictionName, md.getName(), false))
						listPRestriction.add(new PRestriction(uid, rRestrictionName, md.getName(), false, true));
					else if (md.isDangerous())
						listPRestriction.add(new PRestriction(uid, rRestrictionName, md.getName(), false, md.whitelist() == null));
				}

		setRestrictionList(listPRestriction);
	}

	public static boolean canRestrict(int uid, int xuid, String restrictionName, String methodName, boolean system)
	{
		int _uid = Util.getAppId(uid);
		int userId = Util.getUserId(uid);

		if (_uid == Process.SYSTEM_UID && PrivacyManager.cIdentification.equals(restrictionName))
			return false;

		if (system)
			if (!isApplication(_uid))
				if (!getSettingBool(userId, PrivacyManager.cSettingSystem, false))
					return false;

		// @formatter:off
		if (_uid == Util.getAppId(xuid) && (PrivacyManager.cIdentification.equals(restrictionName) || PrivacyManager.cIPC.equals(restrictionName) || PrivacyManager.cStorage.equals(restrictionName) || PrivacyManager.cSystem.equals(restrictionName) || PrivacyManager.cView.equals(restrictionName)))
			return false;
		// @formatter:on

		Hook hook = getHook(restrictionName, methodName);
		if (hook != null && hook.isUnsafe())
			if (getSettingBool(userId, PrivacyManager.cSettingSafeMode, false))
				return false;

		return true;
	}

	public static void updateState(int uid)
	{
		setSetting(uid, cSettingState, Integer.toString(ApplicationInfoEx.STATE_CHANGED));
		setSetting(uid, cSettingModifyTime, Long.toString(System.currentTimeMillis()));
	}

	public static void setRestrictionList(List<PRestriction> listRestriction)
	{
		checkCaller();

		if (listRestriction.size() > 0)
			try {
				PrivacyService.getClient().setRestrictionList(listRestriction);

				// Clear cache
				synchronized (mRestrictionCache) {
					mRestrictionCache.clear();
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
	}

	public static List<PRestriction> getRestrictionList(int uid, String restrictionName)
	{
		checkCaller();

		try {
			return PrivacyService.getClient().getRestrictionList(new PRestriction(uid, restrictionName, null, false));
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
		return new ArrayList<PRestriction>();
	}

	public static boolean isRestrictionSet(PRestriction restriction)
	{
		try {
			return PrivacyService.getClient().isRestrictionSet(restriction);
		} catch (Throwable ex) {
			Util.bug(null, ex);
			return false;
		}
	}

	public static void deleteRestrictions(int uid, String restrictionName, boolean deleteWhitelists)
	{
		checkCaller();

		try {
			// Delete restrictions
			PrivacyService.getClient().deleteRestrictions(uid, restrictionName == null ? "" : restrictionName);

			// Clear associated whitelists
			if (deleteWhitelists && uid > 0) {
				for (PSetting setting : getSettingList(uid, null))
					if (Meta.isWhitelist(setting.type))
						setSetting(uid, setting.type, setting.name, null);
			}

			// Clear cache
			synchronized (mRestrictionCache) {
				mRestrictionCache.clear();
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}

		// Mark as new/changed
		setSetting(uid, cSettingState, Integer.toString(restrictionName == null ? ApplicationInfoEx.STATE_CHANGED : ApplicationInfoEx.STATE_ATTENTION));

		// Change app modification time
		setSetting(uid, cSettingModifyTime, Long.toString(System.currentTimeMillis()));
	}

	public static List<Boolean> getRestartStates(int uid, String restrictionName)
	{
		// Returns a list of restriction states for functions whose application
		// requires the app to be restarted.
		List<Boolean> listRestartRestriction = new ArrayList<Boolean>();

		Set<String> listRestriction = new HashSet<String>();
		if (restrictionName == null)
			listRestriction = mRestart.keySet();
		else if (mRestart.keySet().contains(restrictionName))
			listRestriction.add(restrictionName);

		try {
			for (String restriction : listRestriction) {
				for (String method : mRestart.get(restriction))
					listRestartRestriction.add(getRestrictionEx(uid, restriction, method).restricted);
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}

		return listRestartRestriction;
	}

	public static void applyTemplate(int uid, String templateName, String restrictionName, boolean methods, boolean clear, boolean invert)
	{
		checkCaller();

		int userId = Util.getUserId(uid);

		// Check on-demand
		boolean ondemand = getSettingBool(userId, PrivacyManager.cSettingOnDemand, true);

		// Build list of restrictions
		List<String> listRestriction = new ArrayList<String>();
		if (restrictionName == null)
			listRestriction.addAll(getRestrictions());
		else
			listRestriction.add(restrictionName);

		// Apply template
		Util.log(null, Log.WARN, "Applying template=" + templateName);
		boolean hasOndemand = false;
		List<PRestriction> listPRestriction = new ArrayList<PRestriction>();
		for (String rRestrictionName : listRestriction) {
			// Cleanup
			if (clear)
				deleteRestrictions(uid, rRestrictionName, false);

			// Parent
			String parentValue = getSetting(userId, templateName, rRestrictionName, Boolean.toString(!ondemand) + "+ask");
			boolean parentRestricted = parentValue.contains("true");
			boolean parentAsked = (!ondemand || parentValue.contains("asked"));
			hasOndemand = hasOndemand || !parentAsked;

			// Merge
			PRestriction parentMerge;
			if (clear)
				parentMerge = new PRestriction(uid, rRestrictionName, null, parentRestricted, parentAsked);
			else
				parentMerge = getRestrictionEx(uid, rRestrictionName, null);

			// Apply
			if (canRestrict(uid, Process.myUid(), rRestrictionName, null, true))
				if (invert && ((parentRestricted && parentMerge.restricted) || (!parentAsked && !parentMerge.asked))) {
					listPRestriction.add(new PRestriction(uid, rRestrictionName, null, parentRestricted ? false : parentMerge.restricted, !parentAsked ? true : parentMerge.asked));
					continue; // leave functions
				} else
					listPRestriction.add(new PRestriction(uid, rRestrictionName, null, parentMerge.restricted || parentRestricted, parentMerge.asked && parentAsked));

			// Childs
			if (methods)
				for (Hook hook : getHooks(rRestrictionName, null))
					if (canRestrict(uid, Process.myUid(), rRestrictionName, hook.getName(), true)) {
						// Child
						String settingName = rRestrictionName + "." + hook.getName();
						String childValue = getSetting(userId, templateName, settingName, null);
						if (childValue == null)
							childValue = Boolean.toString(parentRestricted && !hook.isDangerous()) + (parentAsked || (hook.isDangerous() && hook.whitelist() == null) ? "+asked" : "+ask");
						boolean restricted = childValue.contains("true");
						boolean asked = (!ondemand || childValue.contains("asked"));

						// Merge
						PRestriction childMerge;
						if (clear)
							childMerge = new PRestriction(uid, rRestrictionName, hook.getName(), parentRestricted && restricted, parentAsked || asked);
						else
							childMerge = getRestrictionEx(uid, rRestrictionName, hook.getName());

						// Invert
						if (invert && parentRestricted && restricted) {
							restricted = false;
							childMerge.restricted = false;
						}
						if (invert && !parentAsked && !asked) {
							asked = true;
							childMerge.asked = true;
						}

						// Apply
						if ((parentRestricted && !restricted) || (!parentAsked && asked) || (invert ? false : hook.isDangerous() || !clear)) {
							PRestriction child = new PRestriction(uid, rRestrictionName, hook.getName(), (parentRestricted && restricted) || childMerge.restricted, (parentAsked || asked) && childMerge.asked);
							listPRestriction.add(child);
						}
					}
		}

		// Apply result
		setRestrictionList(listPRestriction);
		if (hasOndemand)
			PrivacyManager.setSetting(uid, PrivacyManager.cSettingOnDemand, Boolean.toString(true));
	}

	// White/black listing

	public static Map<String, TreeMap<String, Boolean>> listWhitelisted(int uid, String type)
	{
		checkCaller();

		Map<String, TreeMap<String, Boolean>> mapWhitelisted = new HashMap<String, TreeMap<String, Boolean>>();
		for (PSetting setting : getSettingList(uid, type))
			if (Meta.isWhitelist(setting.type)) {
				if (!mapWhitelisted.containsKey(setting.type))
					mapWhitelisted.put(setting.type, new TreeMap<String, Boolean>());
				mapWhitelisted.get(setting.type).put(setting.name, Boolean.parseBoolean(setting.value));
			}
		return mapWhitelisted;
	}

	// Usage

	public static long getUsage(int uid, String restrictionName, String methodName)
	{
		checkCaller();

		try {
			List<PRestriction> listRestriction = new ArrayList<PRestriction>();
			if (restrictionName == null)
				for (String sRestrictionName : getRestrictions())
					listRestriction.add(new PRestriction(uid, sRestrictionName, methodName, false));
			else
				listRestriction.add(new PRestriction(uid, restrictionName, methodName, false));
			return PrivacyService.getClient().getUsage(listRestriction);
		} catch (Throwable ex) {
			Util.bug(null, ex);
			return 0;
		}
	}

	public static List<PRestriction> getUsageList(Context context, int uid, String restrictionName)
	{
		checkCaller();

		List<PRestriction> listUsage = new ArrayList<PRestriction>();
		try {
			listUsage.addAll(PrivacyService.getClient().getUsageList(uid, restrictionName == null ? "" : restrictionName));
		} catch (Throwable ex) {
			Util.log(null, Log.ERROR, "getUsageList");
			Util.bug(null, ex);
		}
		Collections.sort(listUsage, new ParcelableRestrictionCompare());
		return listUsage;
	}

	public static class ParcelableRestrictionCompare implements Comparator<PRestriction>
	{
		@Override
		public int compare(PRestriction one, PRestriction another)
		{
			if (one.time < another.time)
				return 1;
			else if (one.time > another.time)
				return -1;
			else
				return 0;
		}
	}

	public static void deleteUsage(int uid)
	{
		checkCaller();

		try {
			PrivacyService.getClient().deleteUsage(uid);
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}

	// Settings

	public static String getSalt(int userId)
	{
		String def = (Build.SERIAL == null ? "" : Build.SERIAL);
		return getSetting(userId, cSettingSalt, def);
	}

	public static void removeLegacySalt(int userId)
	{
		String def = (Build.SERIAL == null ? "" : Build.SERIAL);
		String salt = getSetting(userId, cSettingSalt, null);
		if (def.equals(salt))
			setSetting(userId, cSettingSalt, null);
	}

	public static boolean getSettingBool(int uid, String name, boolean defaultValue)
	{
		return Boolean.parseBoolean(getSetting(uid, name, Boolean.toString(defaultValue)));
	}

	public static boolean getSettingBool(int uid, String type, String name, boolean defaultValue)
	{
		return Boolean.parseBoolean(getSetting(uid, type, name, Boolean.toString(defaultValue)));
	}

	public static String getSetting(int uid, String name, String defaultValue)
	{
		return getSetting(uid, "", name, defaultValue);
	}

	public static String getSetting(int uid, String type, String name, String defaultValue)
	{
		long start = System.currentTimeMillis();
		String value = null;

		// Check cache
		boolean cached = false;
		boolean willExpire = false;
		CSetting key = new CSetting(uid, type, name);
		synchronized (mSettingsCache) {
			if (mSettingsCache.containsKey(key)) {
				CSetting entry = mSettingsCache.get(key);
				if (!entry.isExpired()) {
					cached = true;
					value = entry.getValue();
					willExpire = entry.willExpire();
				}
			}
		}

		// Get settings
		if (!cached)
			try {
				value = PrivacyService.getSettingProxy(new PSetting(Math.abs(uid), type, name, null)).value;
				if (value == null)
					if (uid > 99) {
						int userId = Util.getUserId(uid);
						value = PrivacyService.getSettingProxy(new PSetting(userId, type, name, null)).value;
					}

				// Add to cache
				if (value == null)
					key.setValue(defaultValue);
				else
					key.setValue(value);
				synchronized (mSettingsCache) {
					if (mSettingsCache.containsKey(key))
						mSettingsCache.remove(key);
					mSettingsCache.put(key, key);
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}

		if (value == null)
			value = defaultValue;

		long ms = System.currentTimeMillis() - start;
		if (!willExpire && !cSettingLog.equals(name))
			Util.log(null, ms < cWarnServiceDelayMs ? Log.INFO : Log.WARN, String.format("Get setting uid=%d %s/%s=%s%s %d ms", uid, type, name, value, (cached ? " (cached)" : ""), ms));

		return value;
	}

	public static void setSetting(int uid, String name, String value)
	{
		setSetting(uid, "", name, value);
	}

	public static void setSetting(int uid, String type, String name, String value)
	{
		checkCaller();

		try {
			PrivacyService.getClient().setSetting(new PSetting(uid, type, name, value));

			// Update cache
			CSetting key = new CSetting(uid, type, name);
			key.setValue(value);
			synchronized (mSettingsCache) {
				if (mSettingsCache.containsKey(key))
					mSettingsCache.remove(key);
				mSettingsCache.put(key, key);
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}

	public static void setSettingList(List<PSetting> listSetting)
	{
		checkCaller();

		if (listSetting.size() > 0)
			try {
				PrivacyService.getClient().setSettingList(listSetting);

				// Clear cache
				synchronized (mSettingsCache) {
					mSettingsCache.clear();
				}
			} catch (Throwable ex) {
				Util.bug(null, ex);
			}
	}

	public static List<PSetting> getSettingList(int uid, String type)
	{
		checkCaller();

		try {
			return PrivacyService.getClient().getSettingList(new PSetting(uid, type, null, null));
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
		return new ArrayList<PSetting>();
	}

	public static void deleteSettings(int uid)
	{
		checkCaller();

		try {
			PrivacyService.getClient().deleteSettings(uid);

			// Clear cache
			synchronized (mSettingsCache) {
				mSettingsCache.clear();
			}
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}

	private static final List<String> cSettingAppSpecific = Arrays.asList(new String[] { cSettingRandom, cSettingSerial, cSettingLatitude, cSettingLongitude, cSettingAltitude, cSettingMac, cSettingIP, cSettingImei, cSettingPhone, cSettingId, cSettingGsfId, cSettingAdId, cSettingMcc, cSettingMnc, cSettingCountry, cSettingOperator, cSettingIccId, cSettingCid, cSettingLac, cSettingSubscriber,
			cSettingSSID, cSettingUa });

	public static boolean hasSpecificSettings(int uid)
	{
		boolean specific = false;
		for (PSetting setting : getSettingList(uid, ""))
			if (cSettingAppSpecific.contains(setting.name)) {
				specific = true;
				break;
			}
		return specific;
	}

	public static String getTransient(String name, String defaultValue)
	{
		CSetting csetting = new CSetting(0, "", name);
		synchronized (mTransientCache) {
			if (mTransientCache.containsKey(csetting))
				return mTransientCache.get(csetting).getValue();
		}

		return defaultValue;
	}

	public static void setTransient(String name, String value)
	{
		CSetting setting = new CSetting(0, "", name);
		setting.setValue(value);
		synchronized (mTransientCache) {
			mTransientCache.put(setting, setting);
		}
	}

	// Common

	public static void clear()
	{
		checkCaller();

		try {
			PrivacyService.getClient().clear();
			flush();
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
	}

	public static void flush()
	{
		synchronized (mSettingsCache) {
			mSettingsCache.clear();
		}
		synchronized (mRestrictionCache) {
			mRestrictionCache.clear();
		}
		synchronized (mPermissionRestrictionCache) {
			mPermissionRestrictionCache.clear();
		}
		synchronized (mPermissionHookCache) {
			mPermissionHookCache.clear();
		}
	}

	// Defacing
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

	@SuppressLint("DefaultLocale")
	public static Object getDefacedProp(int uid, String name)
	{
		// new add data start
		if (name.equals("MODEL")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getModel();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return cDeface;
			} else {
				// System.out.println("2");
				return cDeface;
			}
		}
		if (name.equals("MANUFACTURER")) {

			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getManufacturer();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return cDeface;
			} else {
				// System.out.println("2");
				return cDeface;
			}

		}
		// PRODUCT
		if (name.equals("PRODUCT")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getProduct();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return cDeface;
			} else {
				// System.out.println("2");
				return cDeface;
			}

		}
		// RELEASE
		if (name.equals("RELEASE")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);
					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getAndroidCode();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return cDeface;
			} else {
				// System.out.println("2");
				return cDeface;
			}
		}

		if (name.equals("DATATYPE")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);
					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getDatatype();
						// System.out.println("DATATYPE-->" + value);
						return value.toUpperCase();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "Wifi";
			} else {
				// System.out.println("2");
				return "Wifi";
			}

		}
		if (name.equals("DENSITY")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);
					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getDensity();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "480X320";
			} else {
				// System.out.println("2");
				return "480X320";
			}

		}

		if (name.equals("TIMEAPP")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getTimeapp();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "30";
			} else {
				// System.out.println("2");
				return "30";
			}

		}
		// new add data end

		// Serial number
		if (name.equals("SERIAL") || name.equals("%serialno")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getSerial();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("SERIAL");
			} else {
				// System.out.println("2");

				return getRandomProp("SERIAL");
			}
		}

		// Host name
		if (name.equals("%hostname"))
			return cDeface;

		// MAC addresses
		if (name.equals("MAC") || name.equals("%macaddr")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getMacAddress();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("MAC");
			} else {
				return getRandomProp("MAC");
			}

		}

		// cid
		if (name.equals("%cid"))
			return cDeface;

		// IMEI
		if (name.equals("getDeviceId") || name.equals("%imei")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getImei();
						return value;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("IMEI");
			} else {
				// System.out.println("2");
				return getRandomProp("IMEI");
			}

		}

		// Phone
		if (name.equals("PhoneNumber") || name.equals("getLine1AlphaTag") || name.equals("getLine1Number") || name.equals("getMsisdn") || name.equals("getVoiceMailAlphaTag") || name.equals("getVoiceMailNumber") || name.equals("getCompleteVoiceMailNumber")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getPhoneNumber();
						return value;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("PHONE");
			} else {
				// System.out.println("2");
				return getRandomProp("PHONE");
			}
		}

		// Android ID
		if (name.equals("ANDROID_ID")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getAndroidID();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("ANDROID_ID");
			} else {
				// System.out.println("2");
				return getRandomProp("ANDROID_ID");
			}
		}

		// Telephony manager
		if (name.equals("getGroupIdLevel1"))
			return null;
		if (name.equals("getIsimDomain"))
			return null;
		if (name.equals("getIsimImpi"))
			return null;
		if (name.equals("getIsimImpu"))
			return null;

		if (name.equals("getNetworkCountryIso") || name.equals("CountryIso")) {
			// ISO country code
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getCountry();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("ISO3166");
			} else {
				// System.out.println("2");
				return getRandomProp("ISO3166");
			}
		}
		if (name.equals("getNetworkOperator")) {
			// MCC+MNC: test network
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getOperator();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("ISO3166");
			} else {
				// System.out.println("2");

				return getRandomProp("ISO3166");
			}
		}

		if (name.equals("getNetworkOperatorName")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getOperator();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("ISO3166");
			} else {
				// System.out.println("2");

				return getRandomProp("ISO3166");
			}
		}
		if (name.equals("getSimCountryIso")) {
			// ISO country code
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getOperator();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return getRandomProp("ISO3166");
			} else {
				// System.out.println("2");

				return getRandomProp("ISO3166");
			}
		}
		if (name.equals("getSimOperator")) {
			// MCC+MNC: test network
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getMcc() + mBases.getMnc();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "001" + "01";
			} else {
				// System.out.println("2");
				return "001" + "01";
			}
		}

		if (name.equals("getSimOperatorName")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getOperator();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "CN";
			} else {
				// System.out.println("2");
				return "CN";
			}
		}

		if (name.equals("getSimSerialNumber") || name.equals("getIccSerialNumber") || name.equals("getIccSerialNumber")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);
					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getIccId();
						return value;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return cDeface;
			} else {
				return cDeface;
			}
		}

		if (name.equals("getSubscriberId")) {
			// IMSI for a GSM phone
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);
					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getImsi();
						// System.out.println(value);
						return value;
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return getRandomProp("IMSI");
			} else {
				return getRandomProp("IMSI");
			}
		}

		if (name.equals("SSID")) {
			// Default hidden network
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getSsid();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return getRandomProp("SSID");
			} else {
				return getRandomProp("SSID");
			}
		}

		// Google services framework ID
		if (name.equals("GSF_ID")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getGsfId();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return cDeface;
			} else {
				// System.out.println("2");
				return cDeface;
			}
		}

		// Advertisement ID
		if (name.equals("AdvertisingId")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getAdvertisementID();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "DEFACE00-0000-0000-0000-000000000000";
			} else {
				// System.out.println("2");
				return "DEFACE00-0000-0000-0000-000000000000";
			}
		}

		if (name.equals("InetAddress")) {
			// Set address
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getIpAddress();
						// return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String ip = value;
			if (ip != null)
				try {
					return InetAddress.getByName(ip);
				} catch (Throwable ignored) {
				}

			// Any address (0.0.0.0)
			try {
				Field unspecified = Inet4Address.class.getDeclaredField("ANY");
				unspecified.setAccessible(true);
				return (InetAddress) unspecified.get(Inet4Address.class);
			} catch (Throwable ex) {
				Util.bug(null, ex);
				return null;
			}
		}

		if (name.equals("IPInt")) {
			// Set address
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getIpAddress();
						// return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String ip = value;
			if (ip != null)
				try {
					InetAddress inet = InetAddress.getByName(ip);
					if (inet.getClass().equals(Inet4Address.class)) {
						byte[] b = inet.getAddress();
						return b[0] + (b[1] << 8) + (b[2] << 16) + (b[3] << 24);
					}
				} catch (Throwable ex) {
					Util.bug(null, ex);
				}

			// Any address (0.0.0.0)
			return 0;
		}

		if (name.equals("Bytes3"))
			return new byte[] { (byte) 0xDE, (byte) 0xFA, (byte) 0xCE };

		if (name.equals("UA")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getUa();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "Mozilla/5.0 (Linux; U; Android; en-us) AppleWebKit/999+ (KHTML, like Gecko) Safari/999.9";
			} else {
				// System.out.println("2");

				return "Mozilla/5.0 (Linux; U; Android; en-us) AppleWebKit/999+ (KHTML, like Gecko) Safari/999.9";
			}
		}

		// InputDevice
		if (name.equals("DeviceDescriptor"))
			return cDeface;

		// getExtraInfo
		if (name.equals("ExtraInfo"))
			return cDeface;

		if (name.equals("MCC")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getMcc();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("1");
				return "001";
			} else {
				// System.out.println("2");
				return "001";
			}
		}

		if (name.equals("MNC")) {
			String value = null;
			File sdcardDir = Environment.getExternalStorageDirectory();
			String path = sdcardDir.getPath() + "/xp_datafile/setting";
			File mFile = new File(path);
			if (mFile.exists()) {
				try {
					String result = BufferedReaderJSON(path);

					if (result != null) {
						Gson mGson = new Gson();
						PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
						value = mBases.getMnc();
						return value;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "01";
			} else {
				return "01";
			}
		}

		if (name.equals("CID"))
			try {
				return Integer.parseInt("0") & 0xFFFF;
			} catch (Throwable ignored) {
				return -1;
			}

		if (name.equals("LAC"))

			try {
				return Integer.parseInt("0") & 0xFFFF;
			} catch (Throwable ignored) {
				return -1;
			}

		if (name.equals("USB"))
			return cDeface;

		if (name.equals("BTName"))
			return cDeface;

		// Fallback
		Util.log(null, Log.ERROR, "Fallback value name=" + name);
		Util.logStack(null, Log.ERROR);
		return cDeface;
	}

	public static Location getDefacedLocation(int uid, Location location)
	{
		// Christmas Island ~ -10.5 / 105.667
		String value = null;
		String sLat = null, sLon = null, sAlt = null;
		File sdcardDir = Environment.getExternalStorageDirectory();
		String path = sdcardDir.getPath() + "/xp_datafile/setting";
		File mFile = new File(path);
		if (mFile.exists()) {
			try {
				String result = BufferedReaderJSON(path);

				if (result != null) {
					Gson mGson = new Gson();
					PhoneDataBean mBases = mGson.fromJson(result, PhoneDataBean.class);
					sLat = mBases.getLatitude();
					sLon = mBases.getLongitude();
					sAlt = mBases.getAltitude();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("1");
		}

		// Backward compatibility
		if ("".equals(sLat))
			sLat = "-10.5";
		if ("".equals(sLon))
			sLon = "105.667";

		if (cValueRandom.equals(sLat))
			sLat = getRandomProp("LAT");
		if (cValueRandom.equals(sLon))
			sLon = getRandomProp("LON");
		if (cValueRandom.equals(sAlt))
			sAlt = getRandomProp("ALT");

		// 1 degree ~ 111111 m
		// 1 m ~ 0,000009 degrees
		if (location == null)
			location = new Location(cDeface);
		location.setLatitude(Float.parseFloat(sLat) + (Math.random() * 2.0 - 1.0) * location.getAccuracy() * 9e-6);
		location.setLongitude(Float.parseFloat(sLon) + (Math.random() * 2.0 - 1.0) * location.getAccuracy() * 9e-6);
		location.setAltitude(Float.parseFloat(sAlt) + (Math.random() * 2.0 - 1.0) * location.getAccuracy());

		return location;
	}

	@SuppressLint("DefaultLocale")
	public static String getRandomProp(String name)
	{

		Random r = new Random();

		if (name.equals("LAT")) {
			double d = r.nextDouble() * 180 - 90;
			d = Math.rint(d * 1e7) / 1e7;
			return Double.toString(d);
		}
		if (name.equals("LON")) {
			double d = r.nextDouble() * 360 - 180;
			d = Math.rint(d * 1e7) / 1e7;
			return Double.toString(d);
		}

		if (name.equals("SERIAL")) {
			long v = r.nextLong();
			return Long.toHexString(v).toUpperCase();
		}

		if (name.equals("MAC")) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 6; i++) {
				if (i != 0)
					sb.append(':');
				int v = r.nextInt(256);
				if (i == 0)
					v = v & 0xFC; // unicast, globally unique
				sb.append(Integer.toHexString(0x100 | v).substring(1));
			}
			return sb.toString().toUpperCase();
		}

		if (name.equals("PHONE")) {
			String phone = "0";
			for (int i = 1; i < 10; i++)
				phone += Character.forDigit(r.nextInt(10), 10);
			return phone;
		}
		if (name.equals("TELECOM")) {
			String[] p = { "+86133", "+86153", "+86180", "+86181", "+86189" };
			String phone = p[r.nextInt(p.length)];
			for (int i = 1; i < 9; i++)
				phone += Character.forDigit(r.nextInt(10), 10);
			return phone;
		}

		if (name.equals("ANDROIDCODE")) {
			String[] p = { "4.4", "4.4.2", "4.3", "4.2.2", "4.1.2", "4.0.3", "4.0", "3.2", "3.0", "2.3.3" };
			return p[r.nextInt(p.length)];
		}

		if (name.equals("MOBILE")) {
			String[] p = { "+86134", "+86135", "+86136", "+86137", "+86138", "+86139", "+86147", "+86150", "+86151", "+86152", "+86157", "+86159", "+86158", "+86182", "+86183", "+86184", "+86187", "+86188" };
			String phone = p[r.nextInt(p.length)];
			for (int i = 1; i < 9; i++)
				phone += Character.forDigit(r.nextInt(10), 10);
			return phone;
		}
		if (name.equals("DATATYPE")) {
			String[] p = { "2G", "3G", "wifi", "4G" };
			return p[r.nextInt(p.length)];
		}
		if (name.equals("DENSITY")) {
			String[] p = { "240X320", "320X480", "460X640", "480X800", "480X854", "540X960", "640X960", "1280X720" };
			return p[r.nextInt(p.length)];
		}

		if (name.equals("SYSTEMCODE")) {

		}

		if (name.equals("TIMEAPP")) {
			String[] p = { "10", "20", "30", "40", "50", "55", "15", "25", "35", "45" };
			return p[r.nextInt(p.length)];
		}

		if (name.equals("UNICOM")) {
			String[] p = { "+86130", "+86131", "+86132", "+86145", "+86155", "+86156", "+86185", "+86186" };
			String phone = p[r.nextInt(p.length)];
			for (int i = 1; i < 9; i++)
				phone += Character.forDigit(r.nextInt(10), 10);
			return phone;
		}

		// IMEI/MEID
		if (name.equals("IMEI")) {
			// http://en.wikipedia.org/wiki/Reporting_Body_Identifier
			String[] rbi = new String[] { "30", "33", "35", "35", "35", "35", "35", "35", "44", "45", "49", "50", "51", "52", "53", "54", "86", "86", "86", "86", "98" };
			String imei = rbi[r.nextInt(rbi.length)];
			while (imei.length() < 14)
				imei += Character.forDigit(r.nextInt(10), 10);
			imei += getLuhnDigit(imei);
			return imei;
		}

		if (name.equals("ANDROID_ID")) {
			long v = r.nextLong();
			return Long.toHexString(v);
		}

		if (name.equals("AdvertisingId"))
			return UUID.randomUUID().toString().toUpperCase();

		if (name.equals("GSF_ID")) {
			long v = Math.abs(r.nextLong());
			return Long.toString(v, 16).toUpperCase();
		}
		if (name.equals("ALT")) {
			double d = r.nextDouble() * 2 * 686;
			return Double.toString(d);
		}

		if (name.equals("ISO3166")) {
			return "CN";
		}

		if (name.equals("GSF_ID")) {
			long v = r.nextLong();
			return Long.toHexString(v).toUpperCase();
		}

		if (name.equals("GsmCallID")) {
			long v = r.nextLong();
			return Long.toHexString(v).toUpperCase();
		}

		if (name.equals("ICCID")) {
			// Empty
			String[] rbi = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "30", "33", "35", "44", "45", "49", "50", "51", "52", "53", "54", "86", "91", "98", "99" };
			String iccid = rbi[r.nextInt(rbi.length)];
			while (iccid.length() < 14)
				iccid += Character.forDigit(r.nextInt(10), 10);
			iccid += getLuhnDigit(iccid);
			return iccid;
		}

		if (name.equals("SubscriberId")) {
			String subscriber = "00101";
			while (subscriber.length() < 15)
				subscriber += Character.forDigit(r.nextInt(10), 10);
			return subscriber;
		}
		if (name.equals("TELECOM ICCID")) {
			String iccid = "8986030";
			for (int i = 1; i < 15; i++) {
				iccid += Character.forDigit(r.nextInt(10), 10);
			}
			return iccid;
		}
		if (name.equals("MOBILE ICCID")) {
			String iccid = "898600";
			for (int i = 1; i < 15; i++) {
				iccid += Character.forDigit(r.nextInt(10), 10);
			}
			return iccid;
		}

		if (name.equals("IPADDRESS")) {
			StringBuilder sb = new StringBuilder();
			int a = r.nextInt(256);
			if (a != 127 && a != 192) {
				sb.append(a);
				sb.append(".");
				sb.append(r.nextInt(256));
				sb.append(".");
				sb.append(r.nextInt(256));
				sb.append(".");
				sb.append(r.nextInt(256));
			}
			return sb.toString();
		}

		if (name.equals("UNICOM ICCID")) {
			String iccid = "898601";
			for (int i = 1; i < 15; i++) {
				iccid += Character.forDigit(r.nextInt(10), 10);
			}
			return iccid;
		}

		if (name.equals("IMSI")) {
			// Empty
			String[] rbi = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "30", "33", "35", "44", "45", "49", "50", "51", "52", "53", "54", "86", "91", "98", "99" };
			String imsi = rbi[r.nextInt(rbi.length)];
			while (imsi.length() < 14)
				imsi += Character.forDigit(r.nextInt(10), 10);
			imsi += getLuhnDigit(imsi);
			return imsi;
		}

		if (name.equals("TELECOM IMSI")) {
			// Empty
			String imsi = "46003";
			for (int i = 1; i < 11; i++) {
				imsi += Character.forDigit(r.nextInt(10), 10);
			}
			return imsi;
		}
		if (name.equals("MOBILE IMSI")) {
			// Empty
			String imsi = "46000";
			for (int i = 1; i < 11; i++) {
				imsi += Character.forDigit(r.nextInt(10), 10);
			}
			return imsi;
		}
		if (name.equals("SSID")) {
			String ssid = "";
			while (ssid.length() < 6)
				ssid += (char) (r.nextInt(26) + 'A');

			ssid += Character.forDigit(r.nextInt(10), 10);
			ssid += Character.forDigit(r.nextInt(10), 10);
			return ssid;
		}
		if (name.equals("AdvertisementID")) {
			long v = r.nextLong();
			return Long.toHexString(v).toUpperCase();
		}
		if (name.equals("UNICOM IMSI")) {
			// Empty
			String imsi = "46001";
			for (int i = 1; i < 11; i++) {
				imsi += Character.forDigit(r.nextInt(10), 10);
			}
			return imsi;
		}
		if (name.equals("MODLE")) {
			int i = 0;
			String str = "";
			String s = null;
			while (i < 5) {
				switch (r.nextInt(63)) {
				case (0):
					s = "0";
					break;
				case (1):
					s = "1";
					break;
				case (2):
					s = "2";
					break;
				case (3):
					s = "3";
					break;
				case (4):
					s = "4";
					break;
				case (5):
					s = "5";
					break;
				case (6):
					s = "6";
					break;
				case (7):
					s = "7";
					break;
				case (8):
					s = "8";
					break;
				case (9):
					s = "9";
					break;
				case (10):
					s = "a";
					break;
				case (11):
					s = "b";
					break;
				case (12):
					s = "c";
					break;
				case (13):
					s = "d";
					break;
				case (14):
					s = "e";
					break;
				case (15):
					s = "f";
					break;
				case (16):
					s = "g";
					break;
				case (17):
					s = "h";
					break;
				case (18):
					s = "i";
					break;
				case (19):
					s = "j";
					break;
				case (20):
					s = "k";
					break;
				case (21):
					s = "m";
					break;
				case (23):
					s = "n";
					break;
				case (24):
					s = "o";
					break;
				case (25):
					s = "p";
					break;
				case (26):
					s = "q";
					break;
				case (27):
					s = "r";
					break;
				case (28):
					s = "s";
					break;
				case (29):
					s = "t";
					break;
				case (30):
					s = "u";
					break;
				case (31):
					s = "v";
					break;
				case (32):
					s = "w";
					break;
				case (33):
					s = "l";
					break;
				case (34):
					s = "x";
					break;
				case (35):
					s = "y";
					break;
				case (36):
					s = "z";
					break;
				case (37):
					s = "A";
					break;
				case (38):
					s = "B";
					break;
				case (39):
					s = "C";
					break;
				case (40):
					s = "D";
					break;
				case (41):
					s = "E";
					break;
				case (42):
					s = "F";
					break;
				case (43):
					s = "G";
					break;
				case (44):
					s = "H";
					break;
				case (45):
					s = "I";
					break;
				case (46):
					s = "L";
					break;
				case (47):
					s = "J";
					break;
				case (48):
					s = "K";
					break;
				case (49):
					s = "M";
					break;
				case (50):
					s = "N";
					break;
				case (51):
					s = "O";
					break;
				case (52):
					s = "P";
					break;
				case (53):
					s = "Q";
					break;
				case (54):
					s = "R";
					break;
				case (55):
					s = "S";
					break;
				case (56):
					s = "T";
					break;
				case (57):
					s = "U";
					break;
				case (58):
					s = "V";
					break;
				case (59):
					s = "W";
					break;
				case (60):
					s = "X";
					break;
				case (61):
					s = "Y";
					break;
				case (62):
					s = "Z";
					break;
				}
				i++;
				str = s + str;

			}
			return str;

		}
		return "";

	}

	private static char getLuhnDigit(String x)
	{
		// http://en.wikipedia.org/wiki/Luhn_algorithm
		int sum = 0;
		for (int i = 0; i < x.length(); i++) {
			int n = Character.digit(x.charAt(x.length() - 1 - i), 10);
			if (i % 2 == 0) {
				n *= 2;
				if (n > 9)
					n -= 9; // n = (n % 10) + 1;
			}
			sum += n;
		}
		return Character.forDigit((sum * 9) % 10, 10);
	}

	// Helper methods

	public static void checkCaller()
	{
		if (PrivacyService.isRegistered()) {
			Util.log(null, Log.ERROR, "Privacy manager call from service");
			Util.logStack(null, Log.ERROR);
		}
	}

	public static final int FIRST_ISOLATED_UID = 99000;
	public static final int LAST_ISOLATED_UID = 99999;
	public static final int FIRST_SHARED_APPLICATION_GID = 50000;
	public static final int LAST_SHARED_APPLICATION_GID = 59999;

	public static boolean isApplication(int uid)
	{
		uid = Util.getAppId(uid);
		return (uid >= Process.FIRST_APPLICATION_UID && uid <= Process.LAST_APPLICATION_UID);
	}

	public static boolean isShared(int uid)
	{
		uid = Util.getAppId(uid);
		return (uid >= FIRST_SHARED_APPLICATION_GID && uid <= LAST_SHARED_APPLICATION_GID);
	}

	public static boolean isIsolated(int uid)
	{
		uid = Util.getAppId(uid);
		return (uid >= FIRST_ISOLATED_UID && uid <= LAST_ISOLATED_UID);
	}

	public static boolean hasPermission(Context context, ApplicationInfoEx appInfo, String restrictionName, Version version)
	{
		int uid = appInfo.getUid();
		synchronized (mPermissionRestrictionCache) {
			if (mPermissionRestrictionCache.get(uid) == null)
				mPermissionRestrictionCache.append(uid, new HashMap<String, Boolean>());
			if (!mPermissionRestrictionCache.get(uid).containsKey(restrictionName)) {
				boolean permission = hasPermission(context, appInfo.getPackageName(), getPermissions(restrictionName, version));
				mPermissionRestrictionCache.get(uid).put(restrictionName, permission);
			}
			return mPermissionRestrictionCache.get(uid).get(restrictionName);
		}
	}

	public static boolean hasPermission(Context context, ApplicationInfoEx appInfo, Hook md)
	{
		int uid = appInfo.getUid();
		synchronized (mPermissionHookCache) {
			if (mPermissionHookCache.get(uid) == null)
				mPermissionHookCache.append(uid, new HashMap<Hook, Boolean>());
			if (!mPermissionHookCache.get(uid).containsKey(md)) {

				List<String> listPermission = (md.getPermissions() == null ? null : Arrays.asList(md.getPermissions()));
				boolean permission = hasPermission(context, appInfo.getPackageName(), listPermission);
				mPermissionHookCache.get(uid).put(md, permission);
			}
			return mPermissionHookCache.get(uid).get(md);
		}
	}

	public static void clearPermissionCache(int uid)
	{
		synchronized (mPermissionRestrictionCache) {
			if (mPermissionRestrictionCache.get(uid) != null)
				mPermissionRestrictionCache.remove(uid);
		}
		synchronized (mPermissionHookCache) {
			if (mPermissionHookCache.get(uid) != null)
				mPermissionHookCache.remove(uid);
		}
	}

	@SuppressLint("DefaultLocale")
	private static boolean hasPermission(Context context, List<String> listPackage, List<String> listPermission)
	{
		try {
			if (listPermission == null || listPermission.size() == 0 || listPermission.contains(""))
				return true;

			PackageManager pm = context.getPackageManager();
			for (String packageName : listPackage) {
				// Check absolute permissions
				for (String apermission : listPermission)
					if (apermission.contains("."))
						if (pm.checkPermission(apermission, packageName) == PackageManager.PERMISSION_GRANTED)
							return true;

				// Check relative permissions
				PackageInfo pInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
				if (pInfo != null && pInfo.requestedPermissions != null)
					for (String rPermission : pInfo.requestedPermissions)
						for (String permission : listPermission)
							if (rPermission.toLowerCase().contains(permission.toLowerCase())) {
								String aPermission = "android.permission." + permission;
								if (!aPermission.equals(rPermission))
									Util.log(null, Log.WARN, "Check permission=" + permission + "/" + rPermission);
								return true;
							}
			}
		} catch (NameNotFoundException ex) {
			Util.log(null, Log.WARN, ex.toString());
		} catch (Throwable ex) {
			Util.bug(null, ex);
		}
		return false;
	}

	public static String ParagraphPhoneNumber(String str)
	{
		int strLeng = str.length();
		Random r = new Random();
		String phone = "+86";
		phone += str;
		for (int i = 0; i < 11 - strLeng; i++)
			phone += Character.forDigit(r.nextInt(10), 10);
		return phone;
	}

	public static String ParagraphImei(String str)
	{
		Random r = new Random();
		String imei = "35";
		imei += str;
		while (imei.length() < 14)
			imei += Character.forDigit(r.nextInt(10), 10);
		imei += getLuhnDigit(imei);
		return imei;
	}
}
