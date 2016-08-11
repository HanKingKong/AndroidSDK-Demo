package com.molmc.opensdkdemo.support.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.opensdk.bean.SensorBean;
import com.molmc.opensdk.mqtt.PublishListener;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.utils.DialogUtil;

/**
 * features:
 * Author：  hhe on 16-8-5 23:34
 * Email：   hhe@molmc.com
 */

public class WidgetInput extends LinearLayout implements View.OnClickListener {

	private EditText etContent;
	private TextView btnSend;
	private TextView tvName;
	private SensorBean sensor;
	private String deviceId;

	public WidgetInput(Context context) {
		this(context, null);
	}

	public WidgetInput(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WidgetInput(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		setLayoutParams(params);
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.item_input, null);
		tvName = (TextView) view.getChildAt(0);
		etContent = (EditText) view.findViewById(R.id.etContent);
		btnSend = (TextView) view.findViewById(R.id.btnSend);
		addView(view, params);
	}

	public void initData(String deviceId, SensorBean sensor) {
		this.sensor = sensor;
		this.deviceId = deviceId;
		if (tvName !=null){
			tvName.setText(sensor.getRemark());
		}
		if (etContent != null) {
			btnSend.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		String content = etContent.getText().toString();
		IntoRobotAPI.getInstance().publisTopic(deviceId, sensor, content, new PublishListener() {
			@Override
			public void onSuccess(String topic) {
				etContent.setText("");
				DialogUtil.showToast(R.string.suc_send);
			}

			@Override
			public void onFailed(String topic, String errMsg) {
				DialogUtil.showToast(errMsg);
			}
		});
	}
}
