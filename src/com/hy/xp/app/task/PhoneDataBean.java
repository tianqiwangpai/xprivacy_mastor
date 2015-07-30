package com.hy.xp.app.task;

import java.io.Serializable;

public class PhoneDataBean implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String table_name = "tb_phone";

	public static final String SERIAL = "Serial";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	public static final String ALTITUDE = "Altitude";
	public static final String MACADDRESS = "MacAddress";
	public static final String IPADDRESS = "IpAddress";
	public static final String IMEI = "Imei";
	public static final String PHONENUMBER = "PhoneNumber";
	public static final String ANDROIDID = "AndroidID";
	public static final String GSFID = "GsfId";
	public static final String ADVERTISEMENTID = "AdvertisementID";
	public static final String MCC = "Mcc";
	public static final String MNC = "Mnc";
	public static final String COUNTRY = "Country";
	public static final String OPERATOR = "Operator";
	public static final String ICCID = "IccId";
	public static final String GSMCALLID = "GsmCallID";
	public static final String GSMLAC = "GsmLac";
	public static final String IMSI = "IMSI";
	public static final String SSID = "SSID";
	public static final String UA = "Ua";
	public static final String MODEL = "Model";
	public static final String MANUFACTURER = "Manufacturer";
	public static final String PRODUCT = "Product";
	public static final String DATATYPE = "datatype";
	public static final String SYSTEMCODE = "SystemCode";
	public static final String ANDROIDCODE = "AndroidCode";
	public static final String DENSITY = "density";
	public static final String TIMEPAPP = "timeapp";
	
	//TODO add by ltz.support blutooth mac
	public static final String BMACADDRESS = "BMacAddress";

	public String getTimeapp()
	{
		return timeapp;
	}

	public void setTimeapp(String timeapp)
	{
		this.timeapp = timeapp;
	}

	public String getAndroidCode()
	{
		return AndroidCode;
	}

	public void setAndroidCode(String androidCode)
	{
		AndroidCode = androidCode;
	}

	private String density;

	public String getDatatype()
	{
		return datatype;
	}

	public void setDatatype(String datatype)
	{
		this.datatype = datatype;
	}

	public String getSystemCode()
	{
		return SystemCode;
	}

	public void setSystemCode(String systemCode)
	{
		SystemCode = systemCode;
	}

	public String getDensity()
	{
		return density;
	}

	public void setDensity(String density)
	{
		this.density = density;
	}

	public String getImsi()
	{
		return Imsi;
	}

	public void setImsi(String imsi)
	{
		Imsi = imsi;
	}

	public String getSsid()
	{
		return Ssid;
	}

	public void setSsid(String ssid)
	{
		Ssid = ssid;
	}

	private String Serial;
	private String Latitude;
	private String Longitude;
	private String Altitude;
	private String MacAddress;
	private String IpAddress;
	private String Imei;
	private String PhoneNumber;
	private String AndroidID;
	private String GsfId;
	private String AdvertisementID;
	private String Mcc;
	private String Mnc;
	private String Country;
	private String Operator;
	private String IccId;
	private String GsmCallID;
	private String GsmLac;
	private String Imsi;
	private String Ssid;
	private String Ua;
	private String Model;
	private String Manufacturer;
	private String Product;
	private String AndroidCode;
	private String timeapp;
	private String SystemCode;
	private String datatype;
	
	private String BMacAddress = "";

	public String getModel()
	{
		return Model;
	}

	public void setModel(String model)
	{
		Model = model;
	}

	public String getManufacturer()
	{
		return Manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		Manufacturer = manufacturer;
	}

	public String getProduct()
	{
		return Product;
	}

	public void setProduct(String product)
	{
		Product = product;
	}

	public String getMnc()
	{
		return Mnc;
	}

	public void setMnc(String mnc)
	{
		Mnc = mnc;
	}

	public String getSerial()
	{
		return Serial;
	}

	public String getLatitude()
	{
		return Latitude;
	}

	public String getLongitude()
	{
		return Longitude;
	}

	public String getAltitude()
	{
		return Altitude;
	}

	public String getMacAddress()
	{
		return MacAddress;
	}

	public String getIpAddress()
	{
		return IpAddress;
	}

	public String getImei()
	{
		return Imei;
	}

	public String getPhoneNumber()
	{
		return PhoneNumber;
	}

	public String getAndroidID()
	{
		return AndroidID;
	}

	public String getGsfId()
	{
		return GsfId;
	}

	public String getAdvertisementID()
	{
		return AdvertisementID;
	}

	public String getMcc()
	{
		return Mcc;
	}

	public String getCountry()
	{
		return Country;
	}

	public String getOperator()
	{
		return Operator;
	}

	public String getIccId()
	{
		return IccId;
	}

	public String getGsmCallID()
	{
		return GsmCallID;
	}

	public String getGsmLac()
	{
		return GsmLac;
	}

	public String getUa()
	{
		return Ua;
	}

	public void setSerial(String serial)
	{
		Serial = serial;
	}

	public void setLatitude(String latitude)
	{
		Latitude = latitude;
	}

	public void setLongitude(String longitude)
	{
		Longitude = longitude;
	}

	public void setAltitude(String altitude)
	{
		Altitude = altitude;
	}

	public void setMacAddress(String macAddress)
	{
		MacAddress = macAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		IpAddress = ipAddress;
	}

	public void setImei(String imei)
	{
		Imei = imei;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		PhoneNumber = phoneNumber;
	}

	public void setAndroidID(String androidID)
	{
		AndroidID = androidID;
	}

	public void setGsfId(String gsfId)
	{
		GsfId = gsfId;
	}

	public void setAdvertisementID(String advertisementID)
	{
		AdvertisementID = advertisementID;
	}

	public void setMcc(String mcc)
	{
		Mcc = mcc;
	}

	public void setCountry(String country)
	{
		Country = country;
	}

	public void setOperator(String operator)
	{
		Operator = operator;
	}

	public void setIccId(String iccId)
	{
		IccId = iccId;
	}

	public void setGsmCallID(String gsmCallID)
	{
		GsmCallID = gsmCallID;
	}

	public void setGsmLac(String gsmLac)
	{
		GsmLac = gsmLac;
	}

	public void setUa(String ua)
	{
		Ua = ua;
	}

	public String getBMacAddress() {
		return BMacAddress;
	}

	public void setBMacAddress(String bMacAddress) {
		BMacAddress = bMacAddress;
	}

	@Override
	public String toString()
	{
		return "PhoneDataBean [datatype=" + datatype + ", SystemCode=" + SystemCode + ", AndroidCode=" + AndroidCode + ", density=" + density + ", Serial=" + Serial + ", Latitude=" + Latitude + ", Longitude=" + Longitude + ", Altitude=" + Altitude + ", MacAddress=" + MacAddress + ", IpAddress=" + IpAddress + ", Imei=" + Imei + ", PhoneNumber=" + PhoneNumber + ", AndroidID=" + AndroidID
				+ ", GsfId=" + GsfId + ", AdvertisementID=" + AdvertisementID + ", Mcc=" + Mcc + ", Mnc=" + Mnc + ", Country=" + Country + ", Operator=" + Operator + ", IccId=" + IccId + ", GsmCallID=" + GsmCallID + ", GsmLac=" + GsmLac + ", Imsi=" + Imsi + ", Ssid=" + Ssid + ", Ua=" + Ua + ", Model=" + Model + ", Manufacturer=" + Manufacturer + ", Product=" + Product + ", BMacAddress=" + BMacAddress + "]";
	}

}
