package com.molmc.opensdkdemo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.molmc.opensdkdemo.base.MyApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * features:
 * Author：  hhe on 16-7-30 15:01
 * Email：   hhe@molmc.com
 */

public class Utils {

	private static int screenWidth;

	private static int screenHeight;

	public static int dip2px(int dipValue) {
		float reSize = MyApplication.getInstance().getResources().getDisplayMetrics().density;
		return (int) ((dipValue * reSize) + 0.5);
	}

	public static int px2dip(int pxValue) {
		float reSize = MyApplication.getInstance().getResources().getDisplayMetrics().density;
		return (int) ((pxValue / reSize) + 0.5);
	}

	public static float sp2px(int spValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, MyApplication.getInstance().getResources().getDisplayMetrics());
	}


	private static void setScreenInfo() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	public static int getScreenWidth() {
		if (screenWidth == 0)
			setScreenInfo();
		return screenWidth;
	}

	public static int getScreenHeight() {
		if (screenHeight == 0)
			setScreenInfo();
		return screenHeight;
	}

	/**
	 * 从topic中截取deviceId
	 * @param topic
	 * @return
	 */
	public static String getDeviceIdFromTopic(String topic){
		Pattern topicPattern = Pattern.compile("^v1\\/([\\w]+)\\/");
		Matcher uidMatcher = topicPattern.matcher(topic);
		String deviceId = "";
		if (uidMatcher.find()) {
			deviceId = uidMatcher.group(1);
		}
		return deviceId;
	}

}
