/**
 * ϵͳ��Ŀ����
 * com.hy.xp.app
 * Contacts.java
 * 
 * 2015��9��19��-����8:01:47
 * 2015����������ѯ���޹�˾-��Ȩ����
 *
 */
package com.hy.xp.app;

import java.io.Serializable;

/**
 *
 * @Class:Contacts
 * @Author:Fellick
 * 2015��9��19�� ����8:01:47 * 
 * @version 1.0.0
 *
 */
public class Contacts  implements Serializable{

	/**
	 * serialVersionUID:TODO����һ�仰�������������ʾʲô��
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
