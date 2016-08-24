package com.molmc.opensdkdemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;
import com.molmc.opensdk.bean.SensorBean;
import com.molmc.opensdk.mqtt.PublishListener;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.utils.DialogUtil;


/**
 * features:
 * Author：  hhe on 16-8-5 22:52
 * Email：   hhe@molmc.com
 */

public class WidgetSeekbar extends LinearLayout implements HoloCircleSeekBar.OnCircleSeekBarChangeListener {

	private HoloCircleSeekBar holoCircleSeekBar;
	private SensorBean sensor;
	private TextView tvName;
	private String deviceId;

	public WidgetSeekbar(Context context) {
		this(context, null);
	}

	public WidgetSeekbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.item_seekbar, null);
		tvName = (TextView) view.getChildAt(0);
		holoCircleSeekBar = (HoloCircleSeekBar) view.getChildAt(1);
		addView(view, params);
	}

	public void initData(String deviceId, SensorBean sensor) {
		this.sensor = sensor;
		this.deviceId = deviceId;
		if (holoCircleSeekBar != null) {
			holoCircleSeekBar.setMax(sensor.getMax());
			holoCircleSeekBar.setValue(sensor.getMin());
			holoCircleSeekBar.setOnSeekBarChangeListener(this);
		}
		if (tvName!=null){
			tvName.setText(sensor.getRemark());
		}
	}


	@Override
	public void onProgressChanged(HoloCircleSeekBar holoCircleSeekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {

	}

	@Override
	public void onStopTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
		String payload = String.valueOf(holoCircleSeekBar.getValue());
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
