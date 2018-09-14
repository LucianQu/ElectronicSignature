package com.xinhui.view;

import com.artifex.mupdf.MuPDFActivity;
import com.xinhui.interfaces.Shapable;
import com.xinhui.interfaces.ToolInterface;
import com.xinhui.painttools.PenAbstract;
import com.xinhui.painttools.PenType;
import com.xinhui.shapes.Curv;
import com.xinhui.utils.PaintConstants.PEN_TYPE;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/***
 * 项目：ElectronicSignature
 * 类名：ShowLineView
 * 功能：显示画笔样式线条
 * 创建时间：2013-12-13
 * 创建人：LXH
 */
public class ShowLineView extends View {
	private static final double PI = 3.1415926535;
	private ToolInterface mCurrentPainter ;
	private Curv mCurrentShape = null;
	private Paint mPaint = null ;
	private int mPaintType = PEN_TYPE.PLAIN_PEN;
	private int mPenSize = 6,mPenColor = Color.BLACK ;
	private Paint.Style mStyle = Paint.Style.STROKE;
	private Path mPath ;
	private float mCurrentX =40f ;
	private float mCurrentY =45f ;
	public ShowLineView(Context context ) {
		this(context, null) ;
	}

	public ShowLineView(Context context, AttributeSet attrs ) {
		super(context, attrs);
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(mStyle) ;
		//getMaskFilter(MuPDFActivity.mPenType);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPath = new Path();
		mPath.reset() ;
		setPath();
	}

	private void setPath() {
		mCurrentX =50f ;
		mCurrentY =55f ;
		mPath.moveTo(mCurrentX, mCurrentY) ;
		float x; float y ;
		for (int i = 0; i < 50; i++) {
			x = (float) (mCurrentX + 5) ;
			y = (float) (mCurrentY + 3*Math.cos(i*1/(2*PI))) ;
			drawBeziercurve( x, y);
			mCurrentX = x ;
			mCurrentY = y ;
		}
	}
	private void drawBeziercurve(float x, float y) {
		mPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX) / 2,
				(y + mCurrentY) / 2);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPath.reset() ;
		setPath();
		mCurrentPainter = new PenType(mPenSize, mPenColor, mStyle ,MuPDFActivity.mPenType);
		getMaskFilter(MuPDFActivity.mPenType);
		drawCanvas(canvas) ;
	}


	/**
	 * 功能：设置画笔风格
	 * @param mPaintType
	 * @return
	 */
	private MaskFilter getMaskFilter(int mPaintType){
		MaskFilter maskFilter = null;
		switch (mPaintType) {
			case PEN_TYPE.PLAIN_PEN://签字笔风格
				maskFilter = null;
				break;
			case PEN_TYPE.BLUR://铅笔模糊风格
				maskFilter = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
				break;
			case PEN_TYPE.EMBOSS://毛笔浮雕风格
				maskFilter = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
				break;
			case PEN_TYPE.TS_PEN://透明水彩风格
				maskFilter = null;
				mPaint.setAlpha(50);
				break;
			default:
				maskFilter = null;
				break;
		}
		mPaint.setMaskFilter(maskFilter);
		return maskFilter;
	}
	private void drawCanvas(Canvas canvas){
		/*canvas.clipPath(mPath);
		canvas.drawPath(mPath, mPaint);*/
		((PenAbstract) mCurrentPainter).setPath(mPath) ;
		mCurrentShape = new Curv((Shapable) mCurrentPainter);
		((Shapable)mCurrentPainter).setShap(mCurrentShape) ;
		mCurrentPainter.draw(canvas) ;
	}
	public void setAttr(int size ,int color,int type){
		mPenSize = size ;
		mPenColor = color ;
		mPaintType = type ;
		invalidate() ;
	}

}
