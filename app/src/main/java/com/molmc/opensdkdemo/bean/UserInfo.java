package com.molmc.opensdkdemo.bean;

import com.molmc.opensdk.bean.UserInfoBean;

/**
 * features:
 * Author：  hhe on 16-8-6 15:00
 * Email：   hhe@molmc.com
 */

public class UserInfo extends UserInfoBean{
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "UserInfo{" +
				"username='" + username + '\'' +
				'}';
	}
}
