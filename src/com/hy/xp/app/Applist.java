/**
 * ϵͳ��Ŀ����
 * com.hy.xp.app
 * Applist.java
 * 
 * 2015��9��19��-����8:01:29
 * 2015����������ѯ���޹�˾-��Ȩ����
 *
 */
package com.hy.xp.app;

import java.io.Serializable;

/**
 *
 * @Class:Applist
 * @Author:Fellick
 * 2015��9��19�� ����8:01:29 * 
 * @version 1.0.0
 *
 */
public class Applist  implements Serializable{

	/**
	 * serialVersionUID:TODO����һ�仰�������������ʾʲô��
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
