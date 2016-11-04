package com.zhihuigusu.knockoff360.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 
	 * ���������е�����ͨ������ת�����ַ���
	 * @param is  ������
	 * @return  ���е��ַ���  ����null���쳣
	 */
	public static String stream2String(InputStream is) {
		//������������ַ������ݴ��ڻ����У���bytearray
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//���������Ŵ��������ж�ȡ������
		byte[] buffer=new byte[1024];
		//���������Ŵ��������ж�ȡ�����ݸ���
		int len=0;
		//��ʼ��ȡ
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
