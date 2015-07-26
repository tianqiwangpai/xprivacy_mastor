package com.hy.xp.app.task;

import com.hy.xp.app.PrivacyProvider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class SetData
{
	public static void setting_resolver(Context context, String settingName, String value)
	{
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(PrivacyProvider.COL_VALUE, value);
		if (contentResolver.update(PrivacyProvider.URI_SETTING, values, settingName, null) <= 0)
		{
			System.out.println(String.format("set setting %s=%s", settingName, value));
		}
	}

}
