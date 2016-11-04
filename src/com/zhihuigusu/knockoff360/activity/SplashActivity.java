package com.zhihuigusu.knockoff360.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.zhihuigusu.knockoff360.R;
import com.zhihuigusu.knockoff360.utils.StreamUtil;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private TextView tv_version_name;
	private int mLocalVersionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//初始化ui
		initUI();
		
		//初始化数据  读取版本名称
		initData();
		
	}

	private void initData() {
		//获取本地版本名称
		tv_version_name.setText("版本名称："+getVersionName());
		//获取本地版本号
		mLocalVersionCode = getVersionCode();
		//获取服务器端应用信息，提取版本号与本地相比，如需更新则下载安装包
		getUpdateInfo();
	}

	/**
	 * 
	 * 获取服务器中的应用更新数据信息
	 * @return  返回服务器的应用信息  null则异常
	 */
	private String getUpdateInfo() {
		
		//联网操作要在子线程中进行
		new Thread(){
			public void run() {
				try {
					//创建url连接，连接到服务器获取数据
					URL url = new URL("http://192.168.1.101:8080/updata.json");
					//获取url连接
					HttpURLConnection uc = (HttpURLConnection) url.openConnection();
					
					//设置请求参数
					uc.setReadTimeout(2000);
					uc.setConnectTimeout(2000);
					if( uc.getResponseCode()==200){
						
						//获取输入流
						InputStream is = uc.getInputStream();
						String info = StreamUtil.stream2String(is);
						Log.e(getLocalClassName(), info);
						
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			};
		}.start();
		return null;
		
	}

	/**
	 * 获取当前应用的本地版本号
	 * @return  返回值为-1，则发生异常
	 */
	private int getVersionCode() {
		//获取包名管理器
		PackageManager packageManager = getPackageManager();
		try {
			//flag为0 获取基本信息
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			//返回版本名称
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 获取当前应用的版本名称
	 * @return  返回为空代表异常
	 */
	private String getVersionName() {
		//获取包名管理器
		PackageManager packageManager = getPackageManager();
		try {
			//flag为0 获取基本信息
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			//返回版本名称
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化ui
	 */
	private void initUI() {
		//找到控件
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
	}

	
}
