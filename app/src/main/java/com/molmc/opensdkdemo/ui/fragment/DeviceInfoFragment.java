package com.molmc.opensdkdemo.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.molmc.opensdk.bean.DeviceBean;
import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.FragmentArgs;
import com.molmc.opensdkdemo.support.eventbus.UpdateDevice;
import com.molmc.opensdkdemo.ui.activity.BaseActivity;
import com.molmc.opensdkdemo.ui.activity.FragmentCommonActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 设备信息
 * Author：  hhe on 16-8-25 12:00
 * Email：   hhe@molmc.com
 */

public class DeviceInfoFragment extends BaseFragment {


	public static void launch(Activity from, DeviceBean device) {
		FragmentArgs args = new FragmentArgs();
		args.add("device", device);
		FragmentCommonActivity.launch(from, DeviceInfoFragment.class, args);
	}

	@Bind(R.id.deviceName)
	EditText deviceName;
	@Bind(R.id.deviceDesc)
	EditText deviceDesc;

	private DeviceBean mDevice;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_deviceinfo, container, false);
		ButterKnife.bind(this, view);
		if (getArguments().getSerializable("device")==null){
			mDevice = (DeviceBean) savedInstanceState.getSerializable("device");
		}else{
			mDevice = (DeviceBean) getArguments().getSerializable("device");
		}
		initView();
		return view;
	}

	private void initView() {
		if (mDevice!=null){
			deviceName.setText(mDevice.getName());
			deviceDesc.setText(mDevice.getDescription());
		}
		BaseActivity baseActivity = (BaseActivity) getActivity();
		baseActivity.getSupportActionBar().setTitle(R.string.device_info_title);
		setHasOptionsMenu(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("device", mDevice);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_save, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.menu_save){
			saveDeviceInfo();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 不存设备信息
	 */
	private void saveDeviceInfo() {
		final String devName = deviceName.getText().toString();
		final String devDesc = deviceDesc.getText().toString();
		if (TextUtils.isEmpty(devName)){
			showToast(R.string.err_devname_empty);
			return;
		}
		if (TextUtils.isEmpty(devDesc)){
			showToast(R.string.err_devdesc_empty);
			return;
		}

		if(devDesc.equals(mDevice.getDescription())&&devName.equals(mDevice.getName())){
			return;
		}
		DeviceBean mDeviceReq = new DeviceBean();
		mDeviceReq.setName(devName);
		mDeviceReq.setDescription(devDesc);
		IntoRobotAPI.getInstance().updateDeviceInfo(mDevice.getDeviceId(), mDeviceReq, new HttpCallback() {
			@Override
			public void onSuccess(int code, Object result) {
				showToast(R.string.suc_save);
				mDevice.setName(devName);
				mDevice.setDescription(devDesc);
				EventBus.getDefault().post(new UpdateDevice(mDevice));
				getActivity().finish();
			}

			@Override
			public void onFail(TaskException exception) {
				showToast(exception.getMessage());
			}
		});
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
