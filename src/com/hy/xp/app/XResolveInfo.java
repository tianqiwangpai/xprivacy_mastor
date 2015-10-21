package com.hy.xp.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.pm.PackageItemInfo;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.util.Log;

public class XResolveInfo  extends XHook {

	private Methods mMethod;
	private String mClassName;
	private static final String cClassName = "android.content.pm.ResolveInfo";
	
	private enum Methods {
		RloadLabel
	};
	
	protected XResolveInfo(Methods method, String restrictionName, String className) {
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

			listHook.add(new XResolveInfo(Methods.RloadLabel, PrivacyManager.cSystem, className));
		}
		return listHook;
	}
	@Override
	protected void before(XParam param) throws Throwable {

	}

	@Override
	protected void after(XParam param) throws Throwable {
		switch (mMethod) {
			case RloadLabel:{
				if (isRestricted(param)){
					List<Applist> value = (List<Applist>) PrivacyManager
							.getDefacedProp(Binder.getCallingUid(), "Appinfo");
					/*for(int i=0; i<value.size(); i++){
						if(((ResolveInfo)param.thisObject).activityInfo.packageName.equals(value.get(i).getPlable())){
							param.setResult(value.get(i).getPname());
							return;
						}
					}*/
					param.setResult("ÁõÌì×÷ÔÚµ·ÂÒ");					
				}
				break;
			}
		}
	}

}
