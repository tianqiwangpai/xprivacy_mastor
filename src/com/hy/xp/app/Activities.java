package com.hy.xp.app;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Activities
{
	public static void hookActivitySettings()
	{

		try {
			hookAllConstructors(findClass("com.android.server.am.ActivityRecord", null), new XC_MethodHook()
			{
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					ActivityInfo aInfo = (ActivityInfo) getObjectField(param.thisObject, "info");
					if (aInfo == null)
						return;
					String pkgName = aInfo.packageName;
					if (XPrivacy.prefs.getInt(pkgName + Common.PREF_RECENTS_MODE, Common.PREF_RECENTS_DEFAULT) > 0) {
						int recentsMode = XPrivacy.prefs.getInt(pkgName + Common.PREF_RECENTS_MODE, Common.PREF_RECENTS_DEFAULT);
						if (recentsMode == Common.PREF_RECENTS_DEFAULT)
							return;
						Intent intent = (Intent) getObjectField(param.thisObject, "intent");
						if (recentsMode == Common.PREF_RECENTS_PREVENT)
							intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					}
				}
			});
		} catch (Throwable e) {
			XposedBridge.log(e);
		}

	}
}
