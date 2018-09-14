package com.xinhui.handwrite;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.util.Log;

import com.artifex.mupdf.MuPDFActivity;
import com.artifex.mupdf.MuPDFPageView;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.xinhui.electronicsignature.R;



public class HandWriteToPDF {
	private String InPdfFilePath;
	private String outPdfFilePath;
	private String InPicFilePath;
	public static int writePageNumble;//要签名的页码
	HandWriteToPDF(){

	}
	public HandWriteToPDF(String InPdfFilePath,String outPdfFilePath,String InPicFilePath){
		this.InPdfFilePath = InPdfFilePath;
		this.outPdfFilePath = outPdfFilePath;
		this.InPicFilePath = InPicFilePath;
	}
	public String getInPdfFilePath() {
		return InPdfFilePath;
	}
	public void setInPdfFilePath(String inPdfFilePath) {
		InPdfFilePath = inPdfFilePath;
	}
	public String getOutPdfFilePath() {
		return outPdfFilePath;
	}
	public void setOutPdfFilePath(String outPdfFilePath) {
		this.outPdfFilePath = outPdfFilePath;
	}
	public String getInPicFilePath() {
		return InPicFilePath;
	}
	public void setInPicFilePath(String inPicFilePath) {
		InPicFilePath = inPicFilePath;
	}

	public void addText(){
		try{
			PdfReader reader = new PdfReader(InPdfFilePath, "PDF".getBytes());//选择需要印章的pdf
			FileOutputStream outStream = new FileOutputStream(outPdfFilePath);
			PdfStamper stamp;
			stamp = new PdfStamper(reader, outStream);//加完印章后的pdf
			PdfContentByte over = stamp.getOverContent(writePageNumble);//设置在第几页打印印章
			//用pdfreader获得当前页字典对象.包含了该页的一些数据.比如该页的坐标轴信
			PdfDictionary p = reader.getPageN(writePageNumble);
			//拿到mediaBox 里面放着该页pdf的大小信息.
			PdfObject po =  p.get(new PdfName("MediaBox"));
			//po是一个数组对象.里面包含了该页pdf的坐标轴范围.
			PdfArray pa = (PdfArray) po;
			Image img = Image.getInstance(InPicFilePath);//选择图片
			img.setAlignment(1);
			img.scaleAbsolute(300,150);//控制图片大小,原始比例720:360
			//调用书写pdf位置方法
			writingPosition(img ,pa.getAsNumber(pa.size()-1).floatValue());
			over.addImage(img);
			stamp.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 功能：处理要书写pdf位置
	 * @param img
	 */
	private void writingPosition(Image img ,float pdfHigth){

		int pdfSizeX = MuPDFPageView.pdfSizeX;
		int pdfSizeY = MuPDFPageView.pdfSizeY;
		int pdfPatchX = MuPDFPageView.pdfPatchX;
		int pdfPatchY = MuPDFPageView.pdfPatchY;
		int pdfPatchWidth = MuPDFPageView.pdfPatchWidth;
		int pdfPatchHeight = MuPDFPageView.pdfPatchHeight;
		int y = MuPDFActivity.y+180;
		float n = pdfPatchWidth*1.0f;
		float m = pdfPatchHeight*1.0f;
		n = pdfSizeX/n;
		m = pdfSizeY/m;
		if(n == 1.0f){
			//pdf页面没有放大时的比例
			if(MuPDFActivity.y >= 900){
				img.setAbsolutePosition(MuPDFActivity.x*5/6,0);
			}else if(MuPDFActivity.y <= 60){
				img.setAbsolutePosition(MuPDFActivity.x*5/6,pdfHigth-150);
			}else{
				img.setAbsolutePosition(MuPDFActivity.x*5/6,pdfHigth-((MuPDFActivity.y+120)*5/6));
			}
		}else{
			//pdf页面放大时的比例,这里坐标不精确？？？
			n = (MuPDFActivity.x+pdfPatchX)/n;
			m = (MuPDFActivity.y+pdfPatchY)/m;
			img.setAbsolutePosition(n*5/6,pdfHigth-((m+120)*5/6));
		}
	}
}
