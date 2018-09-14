package com.xinhui.interfaces;


import android.graphics.Canvas;

/**
 * the tool used ,such as pen,eraser
 * @author rooter
 *
 */
public interface ToolInterface {
	public void draw(Canvas canvas);

	public void touchDown(float x, float y);

	public void touchMove(float x, float y);

	public void touchUp(float x, float y);

	public boolean hasDraw();
}
