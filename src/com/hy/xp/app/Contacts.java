/**
 * 系统项目名称
 * com.hy.xp.app
 * Contacts.java
 * 
 * 2015年9月19日-下午8:01:47
 * 2015福建升腾咨询有限公司-版权所有
 *
 */
package com.hy.xp.app;

import java.io.Serializable;

/**
 *
 * @Class:Contacts
 * @Author:Fellick
 * 2015年9月19日 下午8:01:47 * 
 * @version 1.0.0
 *
 */
public class Contacts  implements Serializable{

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */
	
	private static final long serialVersionUID = 1L;
	public static final String table_name = "contact";

	//public final String dataid = "dataid";
	public final String NAME = "name";
	public final String TELEPHONE = "telephone";
	
	private String name = "";
	private String telephone = "";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Override
	public String toString() {
		return NAME+":"+name+"\n"
				+TELEPHONE+":"+telephone;
	}
	
	
	
}
