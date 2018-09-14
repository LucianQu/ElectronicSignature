package com.artifex.mupdf;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
/***
 * ��Ŀ��ElectronicSignature
 * ������MuPDFPageView
 * ���ܣ�������ʾpdf��ͼ��������pdf�ļ�����Ϣ
 * ����ʱ�䣺2013-12-11
 * �����ˣ�LXH
 */
public class MuPDFPageView extends PageView {
	private final MuPDFCore mCore;
	public static int pdfSizeX;
	public static int pdfSizeY;
	public static int pdfPatchX;
	public static int pdfPatchY;
	public static int pdfPatchWidth;
	public static int pdfPatchHeight;
	public MuPDFPageView(Context c, MuPDFCore core, Point parentSize) {
		super(c, parentSize);
		mCore = core;
	}

	public int hitLinkPage(float x, float y) {
		float scale = mSourceScale*(float)getWidth()/(float)mSize.x;
		float docRelX = (x - getLeft())/scale;
		float docRelY = (y - getTop())/scale;
		return mCore.hitLinkPage(mPageNumber, docRelX, docRelY);
	}
	
	/**
	 * patchX,patchY:pdf�ļ���ǰ����Ļ��ʾ������ԭ�㡣
	 * patchWidth��patchHeight��pdf�ļ�ԭʼ��͸ߡ�
	 * �������ȡpdfԭʼ��ߺ͵�ǰ��Ļ��ʾ����λ�ã����ڶ�λ��д���ꡣ
	 */
	@Override
	protected void drawPage(Bitmap bm, int sizeX, int sizeY,
			int patchX, int patchY, int patchWidth, int patchHeight) {
		mCore.drawPage(mPageNumber, bm, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
		pdfSizeX = sizeX;
		pdfSizeY = sizeY;
		pdfPatchX = patchX;
		pdfPatchY = patchY;
		pdfPatchWidth = patchWidth;
		pdfPatchHeight = patchHeight;
	}

	@Override
	protected LinkInfo[] getLinkInfo() {
		return mCore.getPageLinks(mPageNumber);
	}
	
}
