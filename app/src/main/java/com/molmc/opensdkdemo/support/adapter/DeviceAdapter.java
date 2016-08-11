package com.molmc.opensdkdemo.support.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.molmc.opensdk.bean.DeviceBean;
import com.molmc.opensdk.bean.ProductBean;
import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.DeviceProduct;
import com.molmc.opensdkdemo.ui.fragment.DeviceFragment;
import com.molmc.opensdkdemo.utils.DialogUtil;
import com.molmc.opensdkdemo.utils.Interface;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 设备列表适配器
 * Author：  hhe on 16-7-30 14:10
 * Email：   hhe@molmc.com
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

	private Context mContext;
	private List<DeviceBean> mDeviceList;

	/**
	 * Instantiates a new Device adapter.
	 *
	 * @param context    the context
	 * @param deviceList the device list
	 */
	public DeviceAdapter(Context context, List<DeviceBean> deviceList) {
		this.mContext = context;
		this.mDeviceList = deviceList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_device, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		DeviceBean deviceBean = mDeviceList.get(position);
		if (deviceBean==null){
			return;
		}
		initView(deviceBean, holder, position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mDeviceList == null ? 0 : mDeviceList.size();
	}

	/**
	 * 设置设备
	 *
	 * @param deviceList the device list
	 */
	public void changeData(List<DeviceBean> deviceList) {
		this.mDeviceList = deviceList;
		notifyDataSetChanged();
	}

	/**
	 * Add data.
	 *
	 * @param deviceList the device list
	 */
	public void addData(List<DeviceBean> deviceList) {
		if (mDeviceList == null) {
			changeData(deviceList);
		} else {
			mDeviceList.addAll(deviceList);
			notifyDataSetChanged();
		}
	}

	/**
	 * Sets device list.
	 *
	 * @param deviceList the device list
	 */
	public void setDeviceList(List<DeviceBean> deviceList) {
		this.mDeviceList = deviceList;
	}

	/**
	 * Gets device list.
	 *
	 * @return the device list
	 */
	public List<DeviceBean> getDeviceList() {
		return mDeviceList;
	}

	private void initView(DeviceBean dev, ViewHolder holder, int position){
		holder.txtName.setText(dev.getName());
		holder.txtDesc.setText(dev.getDescription());
		Glide.with(mContext).load(R.drawable.aircondition).into(holder.imgPhoto);
		holder.itemDevice.setOnClickListener(onClickListener(holder, dev));
		holder.itemView.setOnLongClickListener(onLongClickListener(position, dev));
		if ("online".equals(dev.getStatus())){
			holder.onlineStatus.setVisibility(View.VISIBLE);
		}else{
			holder.onlineStatus.setVisibility(View.GONE);
		}
	}

	private View.OnClickListener onClickListener(final ViewHolder holder, final DeviceBean device){
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntoRobotAPI.getInstance().getDeviceInfo(device.getDeviceId(), new HttpCallback<ProductBean>() {
					@Override
					public void onSuccess(int code, ProductBean result) {
						Logger.i(new Gson().toJson(result));
						if (result!=null) {
							DeviceProduct deviceProduct = new DeviceProduct();
							deviceProduct.setDevice(device);
							deviceProduct.setProduct(result);
							DeviceFragment.launch((Activity) mContext, deviceProduct);
						}
					}

					@Override
					public void onFail(TaskException exception) {
						DialogUtil.showToast(exception.getMessage());
					}
				});
			}
		};
	}

	private View.OnLongClickListener onLongClickListener(final int position, final DeviceBean device){
		return new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				DialogUtil.showDialog(mContext, R.string.tips, R.string.device_delete, R.string.cancel, R.string.confirm, new Interface.DialogCallback() {
					@Override
					public void onNegative() {

					}

					@Override
					public void onPositive() {
						IntoRobotAPI.getInstance().deleteDevice(device.getDeviceId(), new HttpCallback() {
							@Override
							public void onSuccess(int code, Object result) {
								mDeviceList.remove(position);
								notifyDataSetChanged();
							}

							@Override
							public void onFail(TaskException exception) {
								DialogUtil.showToast(exception.getMessage());
							}
						});
					}
				});
				return true;
			}
		};
	}

	/**
	 * The type View holder.
	 */
	class ViewHolder extends RecyclerView.ViewHolder {
		/**
		 * The Item device.
		 */
		@Bind(R.id.itemDevice)
		RelativeLayout itemDevice;
		/**
		 * The Img photo.
		 */
		@Bind(R.id.imgPhoto)
		ImageView imgPhoto;
		/**
		 * The Txt name.
		 */
		@Bind(R.id.txtName)
		TextView txtName;
		/**
		 * The Txt desc.
		 */
		@Bind(R.id.txtDesc)
		TextView txtDesc;
		/**
		 * The Online status.
		 */
		@Bind(R.id.onlineStatus)
		TextView onlineStatus;

		/**
		 * Instantiates a new View holder.
		 *
		 * @param view the view
		 */
		public ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
