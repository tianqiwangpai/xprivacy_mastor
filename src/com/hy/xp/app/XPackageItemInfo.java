package com.hy.xp.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.pm.PackageItemInfo;
import android.os.Binder;
import android.util.Log;

public class XPackageItemInfo extends XHook {

	private Methods mMethod;
	private String mClassName;
	private static final String cClassName = "android.content.pm.PackageItemInfo";
	
	private enum Methods {
		loadLabel
	};
	
	protected XPackageItemInfo(Methods method, String restrictionName, String className) {
		super(restrictionName, method.name(), null);
		mMethod = method;
		mClassName = className;
	}

	@Override
	public String getClassName() {
		return cClassName;
	}
	public static List<XHook> getInstances(String className)
	{
		List<XHook> listHook = new ArrayList<XHook>();
		if (!cClassName.equals(className)) {
			if (className == null)
				className = cClassName;

			listHook.add(new XPackageItemInfo(Methods.loadLabel, PrivacyManager.cSystem, className));
		}
		return listHook;
	}
	@Override
	protected void before(XParam param) throws Throwable {

	}

	@Override
	protected void after(XParam param) throws Throwable {
		switch (mMethod) {
			case loadLabel:{
				if (isRestricted(param)){
					List<Applist> value = (List<Applist>) PrivacyManager
							.getDefacedProp(Binder.getCallingUid(), "Appinfo");
					for(int i=0; i<value.size(); i++){
						//Log.w("LTZ", "param value :"+Arrays.toString(param.args));
						//Log.w("LTZ", "packagenameis :"+((PackageItemInfo)param.thisObject).packageName);
						if(((PackageItemInfo)param.thisObject).packageName.equals(value.get(i).getPkgname())){
							param.setResult(value.get(i).getAppname());
							return;
						}
					}
					//param.setResult("�������ڵ���");					
				}
				break;
			}
		}
	}

}
