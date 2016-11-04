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
		
		//��ʼ��ui
		initUI();
		
		//��ʼ������  ��ȡ�汾����
		initData();
		
	}

	private void initData() {
		//��ȡ���ذ汾����
		tv_version_name.setText("�汾���ƣ�"+getVersionName());
		//��ȡ���ذ汾��
		mLocalVersionCode = getVersionCode();
		//��ȡ��������Ӧ����Ϣ����ȡ�汾���뱾����ȣ�������������ذ�װ��
		getUpdateInfo();
	}

	/**
	 * 
	 * ��ȡ�������е�Ӧ�ø���������Ϣ
	 * @return  ���ط�������Ӧ����Ϣ  null���쳣
	 */
	private String getUpdateInfo() {
		
		//��������Ҫ�����߳��н���
		new Thread(){
			public void run() {
				try {
					//����url���ӣ����ӵ���������ȡ����
					URL url = new URL("http://192.168.1.101:8080/updata.json");
					//��ȡurl����
					HttpURLConnection uc = (HttpURLConnection) url.openConnection();
					
					//�����������
					uc.setReadTimeout(2000);
					uc.setConnectTimeout(2000);
					if( uc.getResponseCode()==200){
						
						//��ȡ������
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
	 * ��ȡ��ǰӦ�õı��ذ汾��
	 * @return  ����ֵΪ-1�������쳣
	 */
	private int getVersionCode() {
		//��ȡ����������
		PackageManager packageManager = getPackageManager();
		try {
			//flagΪ0 ��ȡ������Ϣ
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			//���ذ汾����
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * ��ȡ��ǰӦ�õİ汾����
	 * @return  ����Ϊ�մ����쳣
	 */
	private String getVersionName() {
		//��ȡ����������
		PackageManager packageManager = getPackageManager();
		try {
			//flagΪ0 ��ȡ������Ϣ
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			//���ذ汾����
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ʼ��ui
	 */
	private void initUI() {
		//�ҵ��ؼ�
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
	}

	
}
