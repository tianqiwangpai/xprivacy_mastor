/**
 * ϵͳ��Ŀ����
 * com.hy.xp.app
 * Calllog.java
 * 
 * 2015��9��19��-����8:01:37
 * 2015����������ѯ���޹�˾-��Ȩ����
 *
 */
package com.hy.xp.app;

import java.io.Serializable;

/**
 *
 * @Class:Calllog
 * @Author:Fellick
 * 2015��9��19�� ����8:01:37 * 
 * @version 1.0.0
 *
 */
public class Calllog  implements Serializable{

	/**
	 * serialVersionUID:TODO����һ�仰�������������ʾʲô��
	 *
	 * @since 1.0.0
	 */
	
	private static final long serialVersionUID = 1L;
	public static final String table_name = "contact_call";
	
	//public final String dataid = "dataid";
	public final String TELEPHONE = "telephone";
	public final String DATETIME = "datetime";
	
	private String telephone = "";
	private long datetime = 0L;
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public long getDatetime() {
		return datetime;
	}
	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}
	
	@Override
	public String toString() {
		return TELEPHONE+":"+telephone+
				"\n"+DATETIME+":"+datetime;
	}
	

}
