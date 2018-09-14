package com.xinhui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
/**
 * 项目：ElectronicSignature
 * 类名：ScreenShotView
 * 功能：选定书写位置示意图
 * 创建时间：2013-12-17
 * 创建人：LXH
 */
public class ScreenShotView extends View {
	private int x;
	private int y;
	private int m;
	private int n;
	private boolean sign;//绘画标记位
	private Paint paint;//画笔
	public ScreenShotView(Context context) {
		super(context);
		paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(sign){
			paint.setColor(Color.TRANSPARENT);
		}else{
			paint.setColor(Color.RED);
			paint.setAlpha(80);
			canvas.drawRect(new Rect(x, y, m, n), paint);
		}
		super.onDraw(canvas);
	}

	public void setSeat(int x,int y,int m,int n){
		this.x = x;
		this.y = y;
		this.m = m;
		this.n = n;
	}

	public boolean isSign() {
		return sign;
	}

	public void setSign(boolean sign) {
		this.sign = sign;
	}
}
