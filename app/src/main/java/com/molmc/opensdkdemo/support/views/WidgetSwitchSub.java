package com.molmc.opensdkdemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leaking.slideswitch.SlideSwitch;
import com.molmc.opensdk.bean.SensorBean;
import com.molmc.opensdk.mqtt.ReceiveListener;
import com.molmc.opensdk.mqtt.SubscribeListener;
import com.molmc.opensdk.mqtt.UnSubscribeListener;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.utils.DialogUtil;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * features: 自定义toggle开关
 * Author：  hhe on 16-8-5 09:38
 * Email：   hhe@molmc.com
 */

public class WidgetSwitchSub extends LinearLayout implements ReceiveListener{

	private SlideSwitch slideSwitch;
	private TextView tvSensorName;
	private SensorBean sensor;
	private String deviceId;

	public WidgetSwitchSub(Context context) {
		this(context, null);
	}

	public WidgetSwitchSub(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetSwitchSub(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.item_switch, null);
		tvSensorName = (TextView) view.getChildAt(0);
		slideSwitch = (SlideSwitch) view.getChildAt(1);
		addView(view, params);
	}

	public void initData(String deviceId, SensorBean sensor) {
		this.sensor = sensor;
		this.deviceId = deviceId;
		if (tvSensorName != null) {
			tvSensorName.setText(sensor.getRemark());
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
			if ("1".equals(payload)){
				slideSwitch.setState(true);
			}else{
				slideSwitch.setState(false);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		Logger.i("unsubscribeTopic");
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
