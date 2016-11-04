package com.zhihuigusu.knockoff360.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 
	 * 将输入流中的数据通过缓存转换成字符串
	 * @param is  输入流
	 * @return  流中的字符串  返回null则异常
	 */
	public static String stream2String(InputStream is) {
		//创建输出流，字符串数据存在缓存中，用bytearray
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//定义数组存放从输入流中读取的数据
		byte[] buffer=new byte[1024];
		//定义变量存放从输入流中读取的数据个数
		int len=0;
		//开始读取
		try {
			while((len=is.read(buffer))!=-1){
				bos.write(buffer, 0, len);
			}
			return bos.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
