/**
 * 系统项目名称
 * com.hy.xp.app
 * Applist.java
 * 
 * 2015年9月19日-下午8:01:29
 * 2015福建升腾咨询有限公司-版权所有
 *
 */
package com.hy.xp.app;

import java.io.Serializable;

/**
 *
 * @Class:Applist
 * @Author:Fellick
 * 2015年9月19日 下午8:01:29 * 
 * @version 1.0.0
 *
 */
public class Applist  implements Serializable{

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */
	
	private static final long serialVersionUID = 1L;
	private static final String table = "applist";
	
	//public final String dataid = "dataid";
	public final String PKGNAME = "pkgname";
	public final String APPNAME = "appname";
	public final String APPICON = "appicon";
	
	private String pkgname = "";	
	private String appname = "";	
	private String appicon = "";	
	
	
	public Applist(String pkgname, String appname, String appicon) {
		super();
		this.pkgname = pkgname;
		this.appname = appname;
		this.appicon = appicon;
	}
	public String getPkgname() {
		return pkgname;
	}
	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getAppicon() {
		return appicon;
	}
	public void setAppicon(String appicon) {
		this.appicon = appicon;
	}
	@Override
	public String toString() {
		return PKGNAME+":"+pkgname+"\n"
				+APPNAME+":"+appname+"\n"
				+APPICON+":"+appicon;
	}	

}
