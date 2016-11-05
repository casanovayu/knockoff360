package com.zhihuigusu.knockoff360.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void showtoast(Context context,String text) {
		
		Toast.makeText(context, text, 0).show();
	}

}
