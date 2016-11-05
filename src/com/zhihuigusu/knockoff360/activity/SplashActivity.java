package com.zhihuigusu.knockoff360.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhihuigusu.knockoff360.R;
import com.zhihuigusu.knockoff360.utils.StreamUtil;
import com.zhihuigusu.knockoff360.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int UPDATA_APK = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int IO_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_version_name;
	private int mLocalVersionCode;
	private int mVersionCode;
	private String mDownloadUrl;
	private String mVersionDes;
	private Handler mHandler=new Handler(){
	
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_APK:
				//�������¶Ի���
				showDownloadDialog();
				break;
			case ENTER_HOME:
				//����������
				enterHome();
				break;
			case IO_ERROR:
				//io�쳣
				ToastUtil.showtoast(getApplicationContext(),"io�쳣");
				enterHome();
				break;
			case URL_ERROR:
				//url�쳣
				ToastUtil.showtoast(getApplicationContext(),"url�쳣");
				enterHome();
				break;
			case JSON_ERROR:
				//json�쳣
				ToastUtil.showtoast(getApplicationContext(),"json�쳣");
				enterHome();
				break;

			
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//��ʼ��ui
		initUI();
		
		//��ʼ������  ��ȡ�汾����
		initData();
		
	}

	/**
	 * ������ʾ�Ի���
	 */
	protected void showDownloadDialog() {
		//�����Ի���
		AlertDialog.Builder ab=new Builder(this);
		ab.setTitle("������ʾ");
		ab.setIcon(R.drawable.ic_launcher);
		ab.setMessage(mVersionDes);
		//ȷ�����ذ�ť
		ab.setPositiveButton("ȷ������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadAPK();
			}
		});
		//ȡ�����ذ�ť
		ab.setNegativeButton("ȡ������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ToastUtil.showtoast(getApplicationContext(),"�û�ȡ������");
				
				enterHome();
				
			}
		});
		//ȡ���Ի���
		ab.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		ab.show();
		
	}

	/**
	 * ����apk
	 */
	protected void downloadAPK() {
		//���sd������״̬
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		//xutils��download�������target��Ҫָ�������������ļ����ͺ�׺��ֻ��·�������
		String path=Environment.getExternalStorageDirectory().getPath()+File.separator+"knockoff360.apk";
		//��xutil����
		HttpUtils hu=new HttpUtils();
		hu.download(mDownloadUrl, path, new RequestCallBack<File>() {
			
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Log.w(TAG, "���سɹ�");
				File file=arg0.result;
				installAPK(file);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.w(TAG, "����ʧ��");
				enterHome();
			}
			
			@Override
			public void onStart() {
				Log.w(TAG, "��ʼ����");
				super.onStart();
			}
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Log.w(TAG, "��������");
				super.onLoading(total, current, isUploading);
			}
			
		});
		}else{
			ToastUtil.showtoast(getApplicationContext(),"�޷��ҵ��洢��");
		}
		
	}

	/**
	 * ��װfile����·���µ�apk
	 * @param file  ��Ҫ��װ���ļ�����
	 */
	protected void installAPK(File file) {
		//����ϵͳ��װ������棬��������Ӧ�õ�activityʹ����ʽ��ͼ
		Intent intent=new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivityForResult(intent, 1);
	}
	
	
	//�û���ϵͳ��װ������ȡ����װ���Ῠ��splash��������ͼ�Ļص�����ת��home����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
		
	}

	/**
	 * ������ҳ��
	 */
	protected void enterHome() {

		Intent intent=new Intent(this,HomeActivity.class);
		
		startActivity(intent);
		finish();
	}

	/**
	 * ��ʼ��ui
	 */
	private void initData() {
		//��ȡ���ذ汾����
		tv_version_name.setText("�汾���ƣ�"+getVersionName());
		//��ȡ���ذ汾��
		mLocalVersionCode = getVersionCode();
		//��ȡ��������Ӧ����Ϣ����ȡ�汾���뱾����ȣ�������������ذ�װ��
		checkVersion();
	}

	/**
	 * 
	 * ��ȡ�������е�Ӧ�ø���������Ϣ
	 * @return  ���ط�������Ӧ����Ϣ  null���쳣
	 */
	private void checkVersion() {
		
		//��������Ҫ�����߳��н���
		new Thread(){

			public void run() {
				
				Message msg=Message.obtain();
				long beginTime = System.currentTimeMillis();
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
						//��ȡjson�ַ���
						String json = StreamUtil.stream2String(is);
						Log.e(getLocalClassName(), json);
						//��ȡjson����
						JSONObject jsonObject = new JSONObject(json);
						//��ȡjson�ֶ�
						String versionName = jsonObject.getString("versionName");
						Log.w(TAG, versionName);
						mVersionDes = jsonObject.getString("versionDes");
						Log.w(TAG, mVersionDes);
						mVersionCode = Integer.parseInt(jsonObject.getString("versionCode"));
						Log.w(TAG, ""+mVersionCode);
						mDownloadUrl = jsonObject.getString("downloadUrl");
						Log.w(TAG, mDownloadUrl);
					}
					
					//�ȶ԰汾��
					if(mLocalVersionCode<mVersionCode){
						//С�ڷ������汾����Ҫ����
						
						msg.what = UPDATA_APK;
					}else{
						//����homeҳ��
						msg.what=ENTER_HOME;
					}
					
				} catch (MalformedURLException e) {
					msg.what=URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what=IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what=JSON_ERROR;
					e.printStackTrace();
				} finally {
					//ǿ��ͣ��splash4����
					long endTime=System.currentTimeMillis();
					if(endTime-beginTime<4000){
						try {
							Thread.sleep(4000-(endTime-beginTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//Ȼ������Ϣ
					mHandler.sendMessage(msg);
				}
				
			};
		}.start();
		
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
