package com.molmc.opensdkdemo.bean;

import com.molmc.opensdk.bean.DeviceBean;
import com.molmc.opensdk.bean.ProductBean;

import org.greenrobot.greendao.annotation.Entity;

import java.io.Serializable;

/**
 * features: 设备产品
 * Author：  hhe on 16-8-10 17:46
 * Email：   hhe@molmc.com
 */

public class DeviceProduct implements Serializable{
	private static final long serialVersionUID = 7792658277322442433L;
	private DeviceBean device;
	private ProductBean product;

	public DeviceBean getDevice() {
		return device;
	}

	public void setDevice(DeviceBean device) {
		this.device = device;
	}

	public ProductBean getProduct() {
		return product;
	}

	public void setProduct(ProductBean product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "Device{" +
				"device=" + device +
				", product=" + product +
				'}';
	}
}
