package com.hy.xp.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ApplicationEx extends Application
{
	private Thread.UncaughtExceptionHandler mPrevHandler;
	// private static Context context;
	private static ApplicationEx mcontext;

	@Override
	public void onCreate()
	{
		super.onCreate();
		// context = getApplicationContext();
		mcontext = this;
		Util.log(null, Log.WARN, "UI started");
		mPrevHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
		{
			@Override
			public void uncaughtException(Thread thread, Throwable ex)
			{
				Util.bug(null, ex);
				if (mPrevHandler != null)
					mPrevHandler.uncaughtException(thread, ex);
			}
		});
	}

	public static Context getContextObject()
	{
		return mcontext;
	}

	public static Context setContext(Context mContext)
	{
		return mContext = mcontext;
	}

	public void onDestroy()
	{
		Util.log(null, Log.WARN, "UI stopped");
	}
}
