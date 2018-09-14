package com.xinhui.painttools;

import com.xinhui.utils.PaintConstants.PEN_TYPE;

import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;

/***
 * 项目：ElectronicSignature
 * 类名：PenType
 * 功能：显示画笔样式
 * 创建时间：2013-12-13
 * 创建人：LXH
 */
public class PenType extends PenAbstract {

	private MaskFilter mBlur;

	public PenType(int penSize, int penColor) {
		this(penSize, penColor, Paint.Style.STROKE ,PEN_TYPE.PLAIN_PEN);//
	}

	public PenType(int size, int penColor, Paint.Style style ,int mPaintType) {
		super(size, penColor, style);
		mBlur = getMaskFilter(mPaintType);
		mPenPaint.setMaskFilter(mBlur);
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
				mPenPaint.setAlpha(50);
				break;
			default:
				maskFilter = null;
				break;
		}
		mPenPaint.setMaskFilter(maskFilter);
		return maskFilter;
	}
	@Override
	public String toString() {
		return "type:blurPen: " + "\tshap: " + currentShape + "\thasDraw: "
				+ hasDraw() + "\tsize: " + penSize + "\tstyle:" + style;
	}
}
