package com.hy.xp.app.task;

import java.io.Serializable;

public class xpmodel implements Serializable
{

	private static final long serialVersionUID = 1L;

	public static final String TABLE = "tb_xp_model";
	public static final String MODEL = "model";
	public static final String PRODUCT = "product";
	public static final String MANUFACTURER = "manufacturer";
	public static final String DENSITY = "density";
	public static final String FALG = "flag";

	private String model;
	private String product;
	private String manufacturer;
	private String density;
	private String flag;

	public String getFlag()
	{
		return flag;
	}

	public void setFlag(String flag)
	{
		this.flag = flag;
	}

	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getProduct()
	{
		return product;
	}

	public void setProduct(String product)
	{
		this.product = product;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public String getDensity()
	{
		return density;
	}

	public void setDensity(String density)
	{
		this.density = density;
	}

	@Override
	public String toString()
	{
		return "xpmodel [model=" + model + ", product=" + product + ", manufacturer=" + manufacturer + ", density=" + density + "]";
	}

}
