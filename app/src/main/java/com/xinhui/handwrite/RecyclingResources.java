package com.xinhui.handwrite;

import android.graphics.Bitmap;

public class RecyclingResources {

	/**
	 * 功能：回收资源，释放内存
	 * @param bitmap
	 */
	public void recycleBitmap(Bitmap bitmap) {
		if(bitmap != null && bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
			System.gc();//提醒系统回收
		}
	}
}
