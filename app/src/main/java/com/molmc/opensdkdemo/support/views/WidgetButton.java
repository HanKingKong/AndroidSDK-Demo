package com.molmc.opensdkdemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.opensdk.bean.SensorBean;
import com.molmc.opensdk.mqtt.PublishListener;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.utils.DialogUtil;

/**
 * features: 开关控件
 * Author：  hhe on 16-8-6 11:03
 * Email：   hhe@molmc.com
 */

public class WidgetButton extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

	private CheckBox mSwitch;
	private TextView tvName;
	private SensorBean sensor;
	private String deviceId;

	public WidgetButton(Context context) {
		this(context, null);
	}

	public WidgetButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.item_button, null);
		tvName = (TextView) view.getChildAt(0);
		mSwitch = (CheckBox) view.getChildAt(1);
		addView(view, params);
	}

	public void initData(String deviceId, SensorBean sensor) {
		this.sensor = sensor;
		this.deviceId = deviceId;
		if (tvName !=null){
			tvName.setText(sensor.getRemark());
		}
		if (mSwitch != null) {
			mSwitch.setOnCheckedChangeListener(this);
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		String payload;
		if (isChecked){
			payload = "1";
		}else{
			payload = "0";
		}
		IntoRobotAPI.getInstance().publisTopic(deviceId, sensor, payload, new PublishListener() {
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
