package com.molmc.opensdkdemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.anderson.dashboardview.view.DashboardView;
import com.molmc.opensdk.bean.SensorBean;
import com.molmc.opensdk.mqtt.ReceiveListener;
import com.molmc.opensdk.mqtt.SubscribeListener;
import com.molmc.opensdk.mqtt.UnSubscribeListener;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.utils.DialogUtil;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * features: 表盘控件
 * Author：  hhe on 16-8-5 16:13
 * Email：   hhe@molmc.com
 */

public class WidgetDashBoard extends LinearLayout implements ReceiveListener {

	private DashboardView dashboardView;
	private SensorBean sensor;
	private String deviceId;

	public WidgetDashBoard(Context context) {
		this(context, null);
	}

	public WidgetDashBoard(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetDashBoard(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.item_dashboard, null);
		dashboardView = (DashboardView) view.getChildAt(0);
		addView(view, params);
	}

	public void initData(String deviceId, SensorBean sensor) {
		this.sensor = sensor;
		this.deviceId = deviceId;
		if (dashboardView != null) {
			dashboardView.setMaxNum(sensor.getMax());
			dashboardView.setUnit(sensor.getUnit());
			dashboardView.setPercent(0);
			dashboardView.setText(sensor.getRemark());
		}
		IntoRobotAPI.getInstance().subscribeTopic(deviceId, sensor, new SubscribeListener() {
			@Override
			public void onSuccess(String topic) {

			}

			@Override
			public void onFailed(String topic, String errMsg) {
				DialogUtil.showToast(errMsg);
			}
		}, this);
	}


	@Override
	public void onReceive(String topic, MqttMessage message) {
		String payload = new String(message.getPayload());
		try {
			int percent = Integer.parseInt(payload) * 100 / sensor.getMax();
			dashboardView.setPercent(percent);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		onDestroy();
	}

	public void onDestroy(){
		IntoRobotAPI.getInstance().unsubscribeTopic(deviceId, sensor, new UnSubscribeListener() {
			@Override
			public void onSuccess(String topic) {

			}

			@Override
			public void onFailed(String topic, String errMsg) {
				DialogUtil.showToast(errMsg);
			}
		});
	}

}
