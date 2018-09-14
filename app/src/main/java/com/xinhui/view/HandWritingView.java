package com.xinhui.view;

import com.artifex.mupdf.MuPDFActivity;
import com.artifex.mupdf.ReaderView;
import com.xinhui.electronicsignature.R;
import com.xinhui.utils.PaintConstants.PEN_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/***
 * 项目：ElectronicSignature
 * 类名：HandWritingView
 * 功能：手写画板
 * 创建时间：2013-12-12
 * 创建人：LXH
 */
public class HandWritingView extends View {

    private Paint paint = null;
    private Canvas canvas = null;
    private static Bitmap originalBitmap = null;
    public static Bitmap new1Bitmap = null;
    private static Bitmap new2Bitmap = null;
    private static Bitmap tempBitmap  ;
    public static Bitmap saveImage = null;
    private float clickX = 0, clickY = 0;
    private float startX = 0, startY = 0;
    private boolean isClear = false;
    private int color = Color.BLACK; //设置画笔的颜色
    private float strokeWidth = 5.0f;
    private Path path;
    public HandWritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.outWidth = ReaderView.screenWidth;
        options.outHeight = options.outWidth/2;
        tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qianming, options) ;
        originalBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);

        new1Bitmap = Bitmap.createBitmap(originalBitmap);
    }
    public void clear() {
        isClear = true;
        //recyclingResources.recycleBitmap(new1Bitmap);
        new2Bitmap = Bitmap.createBitmap(originalBitmap);
        invalidate();
    }



    public Bitmap saveImage() {
        if (saveImage == null) {
            return null;
        }
        return saveImage;
    }

    public void setImge() {
        saveImage = null;
    }

    public void setstyle(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }


    /**
     * 功能：完成画板的相关操作
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(0, 50, 720, 360);//控制画板的区域坐标(x,y,x+width,y+high);
        canvas.drawColor(Color.argb(150, 120, 120, 120));//控制画板的背景颜色
        canvas.drawBitmap(HandWriting(new1Bitmap), 0, 0, null);
        canvas.drawPath(path, paint);
    }

    /**
     * 功能：完成画笔的操作，并返回bitmap对象
     * @param originalBitmap
     * @return
     */
    @SuppressLint("HandlerLeak")

    public  Bitmap HandWriting(Bitmap originalBitmap) {

        if (isClear) {
            canvas = new Canvas(new2Bitmap);
        } else {
            canvas = new Canvas(originalBitmap);
        }
        paint = new Paint();
        paint.setColor(MuPDFActivity.penColor); //设置画笔的颜色
        paint.setStrokeWidth(MuPDFActivity.penSize);//设置画笔的粗细
        getMaskFilter(MuPDFActivity.mPenType);
        paint.setStyle(Style.STROKE);
        //paint.setStrokeWidth(strokeWidth);//设置画笔的粗细
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setSubpixelText(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (isClear) {
            return new2Bitmap;
        }
        return originalBitmap;
    }

    /**
     * 功能：完成对画笔路径的操作,为了保证画笔效果的光滑性，采用贝尔曲线法
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startX = event.getX();
        startY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                return true;
            case MotionEvent.ACTION_UP:
                touchUp(event);
                return true;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 功能：手指点下屏幕时调用
     * @param event
     */
    private void touchDown(MotionEvent event){
        clickX = startX;
        clickY = startY;
        //path绘制的绘制起点
        path.moveTo(startX, startY);
        invalidate();
    }

    /**
     * 功能：手指在屏幕上滑动时调用
     * @param event
     */
    private void touchMove(MotionEvent event){
        //二次贝塞尔，实现平滑曲线；clickX, clickY为操作点，(clickX+startX)/2, (clickY+startY)/2为终点
        path.quadTo(clickX, clickY, (clickX+startX)/2, (clickY+startY)/2);
        //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
        clickX = startX;
        clickY = startY;
        invalidate();
    }

    /**
     * 功能：手指离开屏幕时调用
     * @param event
     */
    private void touchUp(MotionEvent event){
        //鼠标弹起保存最后状态
        canvas.drawPath(path, paint);
        //重置绘制路线，即隐藏之前绘制的轨迹
        path.reset();
    }

    /**
     * 功能：画板界面离开是调用
     */
    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
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
                paint.setAlpha(50);
                break;
            default:
                maskFilter = null;
                break;
        }
        paint.setMaskFilter(maskFilter);
        return maskFilter;
    }
}
