package com.xinhui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PenCircleView extends View {
	private int penSize ;
	private int penColor ;
	private Paint innerPaint ;
	private Paint outterPaint ;
	public PenCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		innerPaint = new Paint() ;
		outterPaint = new Paint() ;
		innerPaint.setAntiAlias(true);
		outterPaint.setAntiAlias(true);
		
		outterPaint.setColor(Color.GRAY) ;
		innerPaint.setColor(Color.WHITE) ;
	}

	public PenCircleView(Context context) {
		this(context ,null ) ;
	}
	
	public void penAttrChange(int size,int color){
		penSize = size ;
		penColor = color ;
		invalidate() ;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(20, 20, 15, outterPaint) ;
		innerPaint.setColor(penColor) ;
		canvas.drawCircle(20, 20, penSize/2, innerPaint) ;
	}
}
