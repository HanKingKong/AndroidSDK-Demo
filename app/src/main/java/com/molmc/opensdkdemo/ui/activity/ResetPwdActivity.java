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

import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.openapi.SdkConstant;
import com.molmc.opensdk.utils.IntoUtil;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.UserBeanReq;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 忘记密码/重置密码
 * Author：  hhe on 16-7-28 23:51
 * Email：   hhe@molmc.com
 */

public class ResetPwdActivity extends BaseActivity {

	public static void launch(Activity from){
		Intent intent = new Intent(from, ResetPwdActivity.class);
		from.startActivity(intent);
	}

	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.editAccount)
	EditText editAccount;
	@Bind(R.id.editPassword)
	EditText editPassword;
	@Bind(R.id.editVerifyCode)
	EditText editVerifyCode;
	@Bind(R.id.btnVldCode)
	Button btnVldCode;
	@Bind(R.id.btnResetPwd)
	Button btnResetPwd;

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
		setContentView(R.layout.activity_reset_pwd);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.reset_password);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
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
		UserBeanReq userBean = new UserBeanReq();if (IntoUtil.isEmail(account)) {
			userBean.setEmail(account);
			userBean.setType(SdkConstant.HTTP_REQUEST_VLDCODE_EMAIL);
		}else{
			userBean.setZone("0086");
			userBean.setPhone(account);
			userBean.setType(SdkConstant.HTTP_REQUEST_VLDCODE_PHONE);
		}
		userBean.setType(SdkConstant.HTTP_REQUEST_VLDCODE_EMAIL);
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


	public void resetPwd(){
		account = editAccount.getText().toString().trim();
		password = editPassword.getText().toString().trim();
		vldCode = editVerifyCode.getText().toString().trim();
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
		UserBeanReq userBean = new UserBeanReq();
		if (IntoUtil.isEmail(account)) {
			userBean.setEmail(account);
		}else{
			userBean.setZone("0086");
			userBean.setPhone(account);
		}
		userBean.setPassword(password);
		userBean.setVldCode(vldCode);
		IntoRobotAPI.getInstance().resetUserPassword(userBean, new HttpCallback() {
			@Override
			public void onSuccess(int code, Object result) {
				showToast(R.string.suc_reset_password);
				finish();
			}

			@Override
			public void onFail(TaskException exception) {
				showToast(exception.getCode());
				if (mTimer!=null){
					mTimer.cancel();
				}
			}
		});
	}

	@OnClick({R.id.btnVldCode, R.id.btnResetPwd})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnVldCode:
				getVerifyCode();
				break;
			case R.id.btnResetPwd:
				resetPwd();
				break;
		}
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
