package com.hy.xp.app.task;

public class Appinfo {
	public final static String packagename = "packagename";
	public final static String lable = "packagename";
	
	public Appinfo(String pname, String plable) {
		super();
		this.pname = pname;
		this.plable = plable;
	}
	private String pname;
	private String plable;
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPlable() {
		return plable;
	}
	public void setPlable(String plable) {
		this.plable = plable;
	}
	@Override
	public String toString() {
		return "Appinfo:["+packagename+":"+pname+";"+lable+":"+plable+"]";
	}
	
}
