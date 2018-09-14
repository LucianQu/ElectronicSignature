package com.xinhui.interfaces;

import com.xinhui.painttools.FirstCurrentPosition;

import android.graphics.Path;


public interface Shapable {
	public Path getPath();
	public void setPath(Path path);
	public FirstCurrentPosition getFirstLastPoint();

	void setShap(ShapesInterface shape);
}
