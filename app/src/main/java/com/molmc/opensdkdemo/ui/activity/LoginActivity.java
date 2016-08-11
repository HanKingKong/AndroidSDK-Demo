package com.molmc.opensdkdemo.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.molmc.opensdk.bean.AppTokenBean;
import com.molmc.opensdk.bean.UserTokenBean;
import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.utils.IntoUtil;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdk.utils.StorageUtil;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.UserBeanReq;
import com.molmc.opensdkdemo.utils.Constant;
import com.molmc.opensdkdemo.utils.DialogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * features: 用户登录
 * Author：  hhe on 16-7-27 22:27
 * Email：   hhe@molmc.com
 */
public class LoginActivity extends BaseActivity {

	@Bind(R.id.editAccount)
	EditText editAccount;
	@Bind(R.id.editPassword)
	EditText editPassword;
	@Bind(R.id.btnRegister)
	Button btnLogin;
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.btnToRegister)
	TextView btnToRegister;
	@Bind(R.id.btnToForgetPwd)
	TextView btnToForgetPwd;


	private String account;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.login);
		requestAppToken();
		account = StorageUtil.getInstance().getShareData(Constant.USER_ACCOUNT);
		password = StorageUtil.getInstance().getShareData(Constant.USER_PASSWORD);
		if (!TextUtils.isEmpty(account)) {
			editAccount.setText(account);
			editAccount.setSelection(account.length());
		}
		if (!TextUtils.isEmpty(password)) {
			editPassword.setText(password);
		}
	}

	private void requestAppToken() {
		if (!IntoRobotAPI.getInstance().isAppTokenExpired()) {
			return;
		}
		IntoRobotAPI.getInstance().requestAppToken(new HttpCallback<AppTokenBean>() {
			@Override
			public void onSuccess(int code, AppTokenBean result) {
			}

			@Override
			public void onFail(TaskException exception) {
			}
		});
	}

	private void userLogin() {
		account = editAccount.getText().toString().trim();
		password = editPassword.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			showToast(R.string.err_account_empty);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showToast(R.string.err_password_empty);
			return;
		}
		DialogUtil.createProgressDialog(this, R.string.loading).show();
		UserBeanReq userBean = new UserBeanReq();
		if (IntoUtil.isEmail(account)) {
			userBean.setEmail(account);
		}else{
			userBean.setZone("0086");
			userBean.setPhone(account);
		}
		userBean.setPassword(password);
		IntoRobotAPI.getInstance().userLogin(userBean, new HttpCallback<UserTokenBean>() {
			@Override
			public void onSuccess(int code, UserTokenBean result) {
				Logger.i("onSuccess: " + result.toString());
				DialogUtil.dismissProgressDialog();
				StorageUtil.getInstance().putShareData(Constant.USER_ACCOUNT, account);
				StorageUtil.getInstance().putShareData(Constant.USER_PASSWORD, password);
				MainActivity.launch(LoginActivity.this);
				finish();
			}

			@Override
			public void onFail(TaskException exception) {
				DialogUtil.dismissProgressDialog();
				Logger.i("onFail: code= " + exception.getCode() + "; msg= " + exception.getMessage());
				showToast(exception.getMessage());
			}
		});
	}


	@OnClick({R.id.btnRegister, R.id.btnToRegister, R.id.btnToForgetPwd})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnRegister:
				userLogin();
				break;
			case R.id.btnToRegister:
				RegisterActivity.launch(this);
				break;
			case R.id.btnToForgetPwd:
				ResetPwdActivity.launch(this);
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
