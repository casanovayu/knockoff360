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
				//跳出更新对话框
				showDownloadDialog();
				break;
			case ENTER_HOME:
				//进入主界面
				enterHome();
				break;
			case IO_ERROR:
				//io异常
				ToastUtil.showtoast(getApplicationContext(),"io异常");
				enterHome();
				break;
			case URL_ERROR:
				//url异常
				ToastUtil.showtoast(getApplicationContext(),"url异常");
				enterHome();
				break;
			case JSON_ERROR:
				//json异常
				ToastUtil.showtoast(getApplicationContext(),"json异常");
				enterHome();
				break;

			
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//初始化ui
		initUI();
		
		//初始化数据  读取版本名称
		initData();
		
	}

	/**
	 * 升级提示对话框
	 */
	protected void showDownloadDialog() {
		//创建对话框
		AlertDialog.Builder ab=new Builder(this);
		ab.setTitle("升级提示");
		ab.setIcon(R.drawable.ic_launcher);
		ab.setMessage(mVersionDes);
		//确认下载按钮
		ab.setPositiveButton("确定下载", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadAPK();
			}
		});
		//取消下载按钮
		ab.setNegativeButton("取消下载", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ToastUtil.showtoast(getApplicationContext(),"用户取消下载");
				
				enterHome();
				
			}
		});
		//取消对话框
		ab.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		ab.show();
		
	}

	/**
	 * 下载apk
	 */
	protected void downloadAPK() {
		//检测sd卡挂载状态
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		//xutils的download方法里的target需要指明保存下来的文件名和后缀，只有路径会出错
		String path=Environment.getExternalStorageDirectory().getPath()+File.separator+"knockoff360.apk";
		//用xutil下载
		HttpUtils hu=new HttpUtils();
		hu.download(mDownloadUrl, path, new RequestCallBack<File>() {
			
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Log.w(TAG, "下载成功");
				File file=arg0.result;
				installAPK(file);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.w(TAG, "下载失败");
				enterHome();
			}
			
			@Override
			public void onStart() {
				Log.w(TAG, "开始下载");
				super.onStart();
			}
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				Log.w(TAG, "正在下载");
				super.onLoading(total, current, isUploading);
			}
			
		});
		}else{
			ToastUtil.showtoast(getApplicationContext(),"无法找到存储卡");
		}
		
	}

	/**
	 * 安装file对象路径下的apk
	 * @param file  需要安装的文件对象
	 */
	protected void installAPK(File file) {
		//调用系统安装程序界面，调用其他应用的activity使用隐式意图
		Intent intent=new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivityForResult(intent, 1);
	}
	
	
	//用户在系统安装界面点击取消安装，会卡在splash，需用意图的回调来跳转到home界面
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
		
	}

	/**
	 * 进入主页面
	 */
	protected void enterHome() {

		Intent intent=new Intent(this,HomeActivity.class);
		
		startActivity(intent);
		finish();
	}

	/**
	 * 初始化ui
	 */
	private void initData() {
		//获取本地版本名称
		tv_version_name.setText("版本名称："+getVersionName());
		//获取本地版本号
		mLocalVersionCode = getVersionCode();
		//获取服务器端应用信息，提取版本号与本地相比，如需更新则下载安装包
		checkVersion();
	}

	/**
	 * 
	 * 获取服务器中的应用更新数据信息
	 * @return  返回服务器的应用信息  null则异常
	 */
	private void checkVersion() {
		
		//联网操作要在子线程中进行
		new Thread(){

			public void run() {
				
				Message msg=Message.obtain();
				long beginTime = System.currentTimeMillis();
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
						//获取json字符串
						String json = StreamUtil.stream2String(is);
						Log.e(getLocalClassName(), json);
						//获取json对象
						JSONObject jsonObject = new JSONObject(json);
						//获取json字段
						String versionName = jsonObject.getString("versionName");
						Log.w(TAG, versionName);
						mVersionDes = jsonObject.getString("versionDes");
						Log.w(TAG, mVersionDes);
						mVersionCode = Integer.parseInt(jsonObject.getString("versionCode"));
						Log.w(TAG, ""+mVersionCode);
						mDownloadUrl = jsonObject.getString("downloadUrl");
						Log.w(TAG, mDownloadUrl);
					}
					
					//比对版本号
					if(mLocalVersionCode<mVersionCode){
						//小于服务器版本，需要更新
						
						msg.what = UPDATA_APK;
					}else{
						//进入home页面
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
					//强制停留splash4秒钟
					long endTime=System.currentTimeMillis();
					if(endTime-beginTime<4000){
						try {
							Thread.sleep(4000-(endTime-beginTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//然后发送消息
					mHandler.sendMessage(msg);
				}
				
			};
		}.start();
		
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
