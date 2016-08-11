package com.molmc.opensdkdemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.molmc.opensdk.bean.UserTokenBean;
import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.openapi.SdkConstant;
import com.molmc.opensdk.utils.IntoUtil;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.UserBeanReq;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 用户注册
 * Author：  hhe on 16-7-28 12:01
 * Email：   hhe@molmc.com
 */

public class RegisterActivity extends BaseActivity {

	public static void launch(Activity from) {
		Intent intent = new Intent(from, RegisterActivity.class);
		from.startActivity(intent);
	}

	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.editUsername)
	EditText editUsername;
	@Bind(R.id.editAccount)
	EditText editAccount;
	@Bind(R.id.editPassword)
	EditText editPassword;
	@Bind(R.id.editVerifyCode)
	EditText editVerifyCode;
	@Bind(R.id.btnRegister)
	Button btnRegister;
	@Bind(R.id.btnVldCode)
	Button btnVldCode;

	//用户名
	private String userName;
	//帐号
	private String account;
	//密码
	private String password;
	//验证码
	private String vldCode;

	private int countTime;
	private Timer mTimer;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1: {
					String str = String.format(getString(R.string.resend_sms_count), msg.arg1);
					btnVldCode.setText(str);
					btnVldCode.setTextColor(getResources().getColor(R.color.colorPrimary));
					break;
				}
				case 2: {
					btnVldCode.setEnabled(true);
					btnVldCode.setText(R.string.resend_sms);
					btnVldCode.setTextColor(getResources().getColor(R.color.colorPrimary));
					if (mTimer!=null) {
						mTimer.cancel();
						mTimer = null;
					}
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.register);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@OnClick({R.id.btnVldCode, R.id.btnRegister})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnVldCode:
				getVerifyCode();
				break;
			case R.id.btnRegister:
				register();
				break;
		}
	}

	/**
	 * 获取验证码
	 */
	private void getVerifyCode() {
		account = editAccount.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			showToast(R.string.err_account_empty);
			return;
		}
		UserBeanReq userBean = new UserBeanReq();
		if (IntoUtil.isEmail(account)) {
			userBean.setEmail(account);
			userBean.setType(SdkConstant.HTTP_REQUEST_VLDCODE_EMAIL);
		}else{
			userBean.setZone("0086");
			userBean.setPhone(account);
			userBean.setType(SdkConstant.HTTP_REQUEST_VLDCODE_PHONE);
		}
		IntoRobotAPI.getInstance().requestVerifyCode(userBean, new HttpCallback() {

			@Override
			public void onSuccess(int code, Object result) {
				if (IntoUtil.isEmail(account)) {
					showToast(R.string.suc_email_vldcode);
				}else{
					showToast(R.string.suc_phone_vldcode);
				}
				btnVldCode.setEnabled(false);
				TimerTask(90);
			}

			@Override
			public void onFail(TaskException exception) {
				btnVldCode.setText(R.string.get_verify_code);
				showToast(exception.getCode()+ ": " + exception.getMessage());
				if (mTimer!=null){
					mTimer.cancel();
				}
			}
		});
	}


	/**
	 * 注册
	 */
	private void register() {
		userName = editUsername.getText().toString().trim();
		account = editAccount.getText().toString().trim();
		password = editPassword.getText().toString().trim();
		vldCode = editVerifyCode.getText().toString().trim();
		if (TextUtils.isEmpty(userName)) {
			showToast(R.string.err_username_empty);
			return;
		}
		if (TextUtils.isEmpty(account)) {
			showToast(R.string.err_account_empty);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showToast(R.string.err_password_empty);
			return;
		}
		if (TextUtils.isEmpty(vldCode)) {
			showToast(R.string.err_vldcode_empty);
			return;
		}
		if (password.length() < 6) {
			showToast(R.string.err_password_length_error);
			return;
		}
		UserBeanReq userTokenBeanReq = new UserBeanReq();
		userTokenBeanReq.setUsername(userName);
		if (IntoUtil.isEmail(account)) {
			userTokenBeanReq.setEmail(account);
		}else{
			userTokenBeanReq.setZone("0086");
			userTokenBeanReq.setPhone(account);
		}
		userTokenBeanReq.setPassword(password);
		userTokenBeanReq.setVldCode(vldCode);
		Logger.i(userTokenBeanReq.toString());
		IntoRobotAPI.getInstance().registerAccount(userTokenBeanReq, new HttpCallback<UserTokenBean>() {
			@Override
			public void onSuccess(int code, UserTokenBean result) {
				showToast(R.string.suc_register_account);
				finish();
			}

			@Override
			public void onFail(TaskException exception) {
				showToast(exception.getMessage());
				if (mTimer!=null){
					mTimer.cancel();
				}
			}
		});
	}


	private void TimerTask(int sec) {
		countTime = sec;
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				countTime--;
				if (countTime <= 0) {
					mHandler.sendEmptyMessage(2);
				} else {
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.arg1 = countTime;
					mHandler.sendMessage(msg);
				}

			}
		}, 0, 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTimer!=null){
			mTimer.cancel();
		}
	}
}
