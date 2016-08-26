package com.molmc.opensdkdemo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.molmc.opensdk.bean.UserInfoBean;
import com.molmc.opensdk.http.HttpCallback;
import com.molmc.opensdk.http.TaskException;
import com.molmc.opensdk.mqtt.ConnectCallback;
import com.molmc.opensdk.openapi.IntoRobotAPI;
import com.molmc.opensdk.utils.IntoUtil;
import com.molmc.opensdk.utils.Logger;
import com.molmc.opensdk.utils.StorageUtil;
import com.molmc.opensdkdemo.R;
import com.molmc.opensdkdemo.bean.UserInfo;
import com.molmc.opensdkdemo.ui.fragment.ChangePwdFragment;
import com.molmc.opensdkdemo.ui.fragment.DeviceListFragment;
import com.molmc.opensdkdemo.ui.fragment.ImlinkNetworkFragment;
import com.molmc.opensdkdemo.utils.Constant;
import com.molmc.opensdkdemo.utils.DialogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * features: 主界面
 * Author：  hhe on 16-7-29 22:27
 * Email：   hhe@molmc.com
 */

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

	public static void launch(Activity from) {
		Intent intent = new Intent(from, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		from.startActivity(intent);
	}

	@Bind(R.id.frameContent)
	FrameLayout frameContent;
	@Bind(R.id.mainNav)
	NavigationView mainNav;
	@Bind(R.id.drawer_layout)
	DrawerLayout drawerLayout;
	@Bind(R.id.toolbar)
	Toolbar toolbar;

	private TextView userName;
	private ImageView userHead;

	private String mqttUser = "intorobot_admin";
	private String mqttPwd = "intorobot_admin";
	private boolean canFinish = false;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(R.string.device_title);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawerLayout.setDrawerListener(toggle);
		toggle.syncState();
		mainNav.setNavigationItemSelectedListener(this);

		userName = (TextView) mainNav.getHeaderView(0).findViewById(R.id.userName);
		userHead = (ImageView) mainNav.getHeaderView(0).findViewById(R.id.userHead);
		userHead.setOnClickListener(headClickListener);
		// 开启一个Fragment事务
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.frameContent, DeviceListFragment.newInstance()).commit();
		createMqtt();
		getUserInfo();
	}

	/**
	 * 创建mqtt连接
	 */
	private void createMqtt() {
		mqttUser = StorageUtil.getInstance().getMqttToken();
		mqttPwd = StorageUtil.getInstance().getUserId();
		IntoRobotAPI.getInstance().createMqttConnection(mqttUser, mqttPwd, new ConnectCallback() {
			@Override
			public void onConnectSuccess() {

			}

			@Override
			public void onConnectFailure() {
				showToast(R.string.err_mqtt_connection);
			}
		});
	}


	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
		IntoRobotAPI.getInstance().getUserInfo(StorageUtil.getInstance().getUserId(), new HttpCallback<UserInfoBean>() {
			@Override
			public void onSuccess(int code, UserInfoBean result) {
				userName.setText(result.getUsername());
			}

			@Override
			public void onFail(TaskException exception) {
				Logger.i(exception.getMessage());
			}
		});

	}

	private View.OnClickListener headClickListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			DialogUtil.inputDialog(MainActivity.this, R.string.change_username, new MaterialDialog.InputCallback() {
				@Override
				public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
					changeUserInfo(input.toString());
				}
			});
		}
	};


	/**
	 * 修改用户昵称
	 * @param nickname
	 */
	private void changeUserInfo(String nickname){
		if (TextUtils.isEmpty(nickname)){
			DialogUtil.showToast(R.string.err_nickname_empty);
			return;
		}
		String account = StorageUtil.getInstance().getShareData(Constant.USER_ACCOUNT);
		UserInfo userReq = new UserInfo();
		userReq.setUsername(nickname);
		if (IntoUtil.isEmail(account)){
			userReq.setEmail(account);
		}else{
			userReq.setPhone(account);
		}
		Logger.i(new Gson().toJson(userReq));
		IntoRobotAPI.getInstance().updateUserInfo(StorageUtil.getInstance().getUserId(), userReq, new HttpCallback() {
			@Override
			public void onSuccess(int code, Object result) {
				DialogUtil.showToast(R.string.suc_change);
			}

			@Override
			public void onFail(TaskException exception) {
				DialogUtil.showToast(exception.getMessage());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.menu_device_add) {
			ImlinkNetworkFragment.launch(this, null);
			return true;
		} else if (id == R.id.menu_logout) {
			IntoRobotAPI.getInstance().unSubscribeAll();
			IntoRobotAPI.getInstance().disconnectMqtt();
			IntoRobotAPI.getInstance().userLogout(StorageUtil.getInstance().getUserId(), new HttpCallback() {
				@Override
				public void onSuccess(int code, Object result) {
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
					MainActivity.this.finish();
				}

				@Override
				public void onFail(TaskException exception) {
					showToast(exception.getMessage());
				}
			});
			return true;
		} else if (id == R.id.menu_qr_scan) {
			QRCaptureActivity.launch(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onBackClick() {
		if (!canFinish) {
			canFinish = true;
			showToast(R.string.comm_hint_exit);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					canFinish = false;
				}

			}, 1500);
			return true;
		}
		IntoRobotAPI.getInstance().unSubscribeAll();
		IntoRobotAPI.getInstance().disconnectMqtt();
		return super.onBackClick();
	}

	@Override
	protected void onDestroy() {
		Logger.i("main activity onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_change_userinfo) {
			// Handle the camera action
			DialogUtil.inputDialog(MainActivity.this, R.string.change_username, new MaterialDialog.InputCallback() {
				@Override
				public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
					changeUserInfo(input.toString());
				}
			});
		} else if (id == R.id.nav_change_password) {
			ChangePwdFragment.launch(this);
		} else if (id == R.id.nav_refresh_token) {
			IntoRobotAPI.getInstance().refreshUserToken(new HttpCallback() {
				@Override
				public void onSuccess(int code, Object result) {
					showToast(R.string.suc_refresh);
				}

				@Override
				public void onFail(TaskException exception) {
					showToast(exception.getMessage());
				}
			});
		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
