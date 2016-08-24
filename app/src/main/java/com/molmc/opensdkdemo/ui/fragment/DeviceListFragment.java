package com.molmc.opensdkdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.molmc.opensdk.bean.DeviceBean;
import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.mqtt.ReceiveListener;
import com.molmc.opensdk.mqtt.SubscribeListener;
import com.molmc.opensdk.mqtt.UnSubscribeListener;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.support.adapter.DeviceAdapter;
import com.molmc.opensdkdemo.support.views.ListRecyclerView;
import com.molmc.opensdkdemo.ui.activity.FragmentCommonActivity;
import com.molmc.opensdkdemo.utils.Utils;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 设备列表
 * Author：  hhe on 16-7-30 10:10
 * Email：   hhe@molmc.com
 */

public class DeviceListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
		HttpCallback<ArrayList<DeviceBean>>, ReceiveListener {

	public static DeviceListFragment newInstance() {
		DeviceListFragment fragment = new DeviceListFragment();
		return fragment;
	}

	@Bind(R.id.recyclerView)
	ListRecyclerView recyclerView;
	@Bind(R.id.swipeRefreshLayout)
	SwipeRefreshLayout swipeRefreshLayout;
	@Bind(R.id.txtEmpty)
	TextView txtEmpty;
	@Bind(R.id.btnAddDevice)
	Button btnAddDevice;
	@Bind(R.id.layoutEmpty)
	RelativeLayout layoutEmpty;


	private DeviceAdapter deviceAdapter;
	private ArrayList<DeviceBean> devices;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.lay_refresh_list, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState!=null){
			devices = (ArrayList<DeviceBean>) savedInstanceState.getSerializable("devices");
		}
		initView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("devices", devices);
	}

	private void initView() {
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		swipeRefreshLayout.setVisibility(View.VISIBLE);
		deviceAdapter = new DeviceAdapter(getActivity(), new ArrayList<DeviceBean>());
		recyclerView.setAdapter(deviceAdapter);
		if (devices!=null){
			deviceAdapter.changeData(devices);
		}else {
			requestData();
		}
	}

	private void requestData() {
		IntoRobotAPI.getInstance().getDeviceList(this);
	}

	@Override
	public void onRefresh() {
		requestData();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unsubDeviceStatus();
		ButterKnife.unbind(this);
	}

	@Override
	public void onSuccess(int code, ArrayList<DeviceBean> result) {
		swipeRefreshLayout.setRefreshing(false);
		devices = result;
		if (devices == null || devices.size() < 1) {
			layoutEmpty.setVisibility(View.VISIBLE);
		} else {
			layoutEmpty.setVisibility(View.GONE);
		}
		deviceAdapter.changeData(result);
		subDeviceStatus();
	}


	@Override
	public void onFail(TaskException exception) {
		swipeRefreshLayout.setRefreshing(false);
		if (devices == null || devices.size() < 1) {
			layoutEmpty.setVisibility(View.VISIBLE);
		} else {
			layoutEmpty.setVisibility(View.GONE);
		}
	}

	private void subDeviceStatus(){
		if (devices==null){
			return;
		}
		for (DeviceBean device : devices){
			IntoRobotAPI.getInstance().getDeviceOnlineStatus(device.getDeviceId(), new SubscribeListener() {
				@Override
				public void onSuccess(String topic) {

				}

				@Override
				public void onFailed(String topic, String errMsg) {
					showToast(errMsg);
				}
			}, this);
		}
	}

	@OnClick(R.id.btnAddDevice)
	public void onClick() {
		Logger.i("click add device...");
		FragmentCommonActivity.launch(getActivity(), ImlinkNetworkFragment.class, null);
	}

	@Override
	public void onReceive(String topic, MqttMessage message) {
		String payload = new String(message.getPayload());
		String deviceId = Utils.getDeviceIdFromTopic(topic);
		Logger.i("topic: " + topic + "; payload: " + payload);
		try {
			String json = new JSONObject(payload).optString("key", "offline");
			for (DeviceBean dev : devices) {
				if (!TextUtils.isEmpty(dev.getDeviceId()) && dev.getDeviceId().equals(deviceId)) {
					dev.setStatus(json);
					deviceAdapter.changeData(devices);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取消订阅设备在线状态
	 */
	private void unsubDeviceStatus(){
		if (devices==null){
			return;
		}
		for (DeviceBean device : devices){
			IntoRobotAPI.getInstance().unSubDeviceOnlineStatus(device.getDeviceId(), new UnSubscribeListener() {
				@Override
				public void onSuccess(String topic) {

				}

				@Override
				public void onFailed(String topic, String errMsg) {
					showToast(errMsg);
				}
			});
		}
	}
}
