package com.molmc.opensdkdemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molmc.opensdk.bean.SensorBean;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.DataType;
import com.molmc.opensdkdemo.bean.DeviceProduct;
import com.molmc.opensdkdemo.bean.FragmentArgs;
import com.molmc.opensdkdemo.bean.SensorType;
import com.molmc.opensdkdemo.support.views.WidgetButton;
import com.molmc.opensdkdemo.support.views.WidgetDashBoard;
import com.molmc.opensdkdemo.support.views.WidgetInput;
import com.molmc.opensdkdemo.support.views.WidgetSeekbar;
import com.molmc.opensdkdemo.support.views.WidgetString;
import com.molmc.opensdkdemo.support.views.WidgetSwitchSub;
import com.molmc.opensdkdemo.ui.activity.BaseActivity;
import com.molmc.opensdkdemo.ui.activity.FragmentCommonActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 设备展示界面
 * Author：  hhe on 16-8-3 16:51
 * Email：   hhe@molmc.com
 */

public class DeviceFragment extends BaseFragment {

	public static void launch(Activity from, DeviceProduct deviceProduct) {
		FragmentArgs args = new FragmentArgs();
		args.add("deviceProduct", deviceProduct);
		FragmentCommonActivity.launch(from, DeviceFragment.class, args);
	}

	@Bind(R.id.dataContainer)
	LinearLayout dataContainer;
	@Bind(R.id.cmdContainer)
	LinearLayout cmdContainer;
	@Bind(R.id.showText)
	TextView showText;
	@Bind(R.id.pubData)
	Button pubData;

	private DeviceProduct deviceProduct;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_device, container, false);
		ButterKnife.bind(this, view);
		initView();
		return view;
	}

	private void initView() {
		if (getArguments() != null) {
			deviceProduct = (DeviceProduct) getArguments().get("deviceProduct");
		}
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.getSupportActionBar().setTitle(deviceProduct.getDevice().getName());
		setHasOptionsMenu(false);
		setDeviceWidget();
	}

	private void setDeviceWidget() {
		if (deviceProduct.getProduct().getSensors() != null) {
			List<SensorBean> sensors = deviceProduct.getProduct().getSensors();
			int count = sensors.size();
			for (int i = 0; i < count; i++) {
				if (sensors.get(i).getSensorType().equals(SensorType.CMD.getSensorType())) {
					Logger.i("cmd controller...");
					setCmdView(sensors.get(i));
				} else if (sensors.get(i).getSensorType().equals(SensorType.DATA.getSensorType())) {
					Logger.i("data sample sensor...");
					setDataView(sensors.get(i));
				} else {
					Logger.i("other data type...");
				}
			}
		}
	}

	private void setCmdView(SensorBean sensorBean) {
		if (TextUtils.isEmpty(sensorBean.getDataType()))
			return;
		if (DataType.BOOL.getDataType().equals(sensorBean.getDataType())) {
			WidgetButton view = new WidgetButton(getActivity());
			view.initData(deviceProduct.getDevice().getDeviceId(), sensorBean);
			cmdContainer.addView(view);
		} else if (DataType.FLOAT.getDataType().equals(sensorBean.getDataType()) || DataType.INTEGER.getDataType().equals(sensorBean.getDataType())) {
			WidgetSeekbar view = new WidgetSeekbar(getActivity());
			view.initData(deviceProduct.getDevice().getDeviceId(), sensorBean);
			cmdContainer.addView(view);
		} else if (DataType.STRING.getDataType().equals(sensorBean.getDataType())) {
			WidgetInput view = new WidgetInput(getActivity());
			view.initData(deviceProduct.getDevice().getDeviceId(), sensorBean);
			cmdContainer.addView(view);
		}
	}

	private void setDataView(SensorBean sensorBean) {
		if (TextUtils.isEmpty(sensorBean.getDataType()))
			return;
		if (DataType.STRING.getDataType().equals(sensorBean.getDataType())) {
			WidgetString view = new WidgetString(getActivity());
			view.initData(deviceProduct.getDevice().getDeviceId(), sensorBean);
			dataContainer.addView(view);
		} else if (DataType.FLOAT.getDataType().equals(sensorBean.getDataType()) || DataType.INTEGER.getDataType().equals(sensorBean.getDataType())) {
			WidgetDashBoard view = new WidgetDashBoard(getActivity());
			view.initData(deviceProduct.getDevice().getDeviceId(), sensorBean);
			dataContainer.addView(view);
		} else if (DataType.ENUM.getDataType().equals(sensorBean.getDataType())) {
			TextView view = new TextView(getActivity());
			view.setText(sensorBean.getRemark());
			dataContainer.addView(view);
		} else if (DataType.BOOL.getDataType().equals(sensorBean.getDataType())) {
			WidgetSwitchSub view = new WidgetSwitchSub(getActivity());
			view.initData(deviceProduct.getDevice().getDeviceId(), sensorBean);
			dataContainer.addView(view);
		}
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@OnClick(R.id.pubData)
	public void onClick() {
	}

}
