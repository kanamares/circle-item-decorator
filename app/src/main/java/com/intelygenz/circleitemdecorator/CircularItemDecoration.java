package com.intelygenz.circleitemdecorator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

class CircularItemDecoration extends RecyclerView.ItemDecoration {
	
	private boolean initialized = false;
	
	private float startAngleSlice = -1;
	private float endAngleSlide = -1;
	private float eachAngleSlide = -1;
	
	private float outRadius = -1f;
	private float inRadius = -1f;
	private float middleRadius = -1f;
	private float centerX = -1f;
	
	private Paint paintSpokes;
	private Paint paintSelected;
	private Paint paintBackground;
	
	CircularItemDecoration() {
		initPaintSpokes();
		initPaintSelected();
		initPaintBackground();
	}
	
	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (!initialized) {
			centerX = getCenterX(parent);
			outRadius = parent.getWidth() / 2;
			inRadius = parent.getWidth() / 3;
			middleRadius = outRadius - (outRadius - inRadius) / 2;
			initialized = true;
		}
		int childCount = parent.getChildCount();
		List<Float> angles = new ArrayList<>();
		for (int i = 0; i < childCount; i++) {
			final View child = parent.getChildAt(i);
			float childCenter = child.getLeft() + child.getWidth() / 2;
			float totalX;
			float currentItemAngle;
			if (childCenter < centerX) {
				totalX = centerX - childCenter;
				currentItemAngle = 270 - (90 * totalX / outRadius);
			} else {
				totalX = childCenter;
				currentItemAngle = 180 + (90 * totalX / outRadius);
			}
			angles.add(currentItemAngle);
			if (currentItemAngle < startAngleSlice || currentItemAngle > endAngleSlide) {
				child.setVisibility(View.GONE);
			} else {
				child.setVisibility(View.VISIBLE);
			}
			double radians3 = Math.toRadians(currentItemAngle);
			float x3 = (outRadius - child.getWidth() / 2) + (float) Math.cos(radians3) * middleRadius;
			float y3 = (outRadius - child.getHeight() / 2) + (float) Math.sin(radians3) * middleRadius;
			child.setY(y3);
			child.setX(x3);
		}
		eachAngleSlide = Math.abs(angles.get(0) - angles.get(1));
		startAngleSlice = angles.get(0) - eachAngleSlide / 2;
		endAngleSlide = angles.get(angles.size() - 1) + eachAngleSlide / 2;
		drawSlices(c);
		if (parent.getScrollState() == SCROLL_STATE_IDLE) {
			drawSelected(c);
			if (!angles.contains(270f)) {
				parent
					.getChildAt(0)
					.performClick();
			}
		}
	}
	
	@Override
	public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
	}
	
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
	}
	
	private void initPaintBackground() {
		paintBackground = new Paint();
		paintBackground.setColor(Color.WHITE);
		paintBackground.setAntiAlias(true);
		paintBackground.setDither(true);
		paintBackground.setStyle(Paint.Style.FILL);
		paintBackground.setStrokeJoin(Paint.Join.ROUND);
		paintBackground.setStrokeCap(Paint.Cap.ROUND);
	}
	
	private void initPaintSelected() {
		paintSelected = new Paint();
		paintSelected.setColor(Color.RED);
		paintSelected.setAntiAlias(true);
		paintSelected.setDither(true);
		paintSelected.setStyle(Paint.Style.FILL);
		paintSelected.setStrokeJoin(Paint.Join.ROUND);
		paintSelected.setStrokeCap(Paint.Cap.ROUND);
	}
	
	private void initPaintSpokes() {
		paintSpokes = new Paint();
		paintSpokes.setColor(Color.GRAY);
		paintSpokes.setAntiAlias(true);
		paintSpokes.setDither(true);
		paintSpokes.setStyle(Paint.Style.STROKE);
		paintSpokes.setStrokeWidth(1);
	}
	
	private float getCenterX(RecyclerView parent) {
		return parent.getLeft() + parent.getWidth() / 2;
	}
	
	private void drawSelected(Canvas canvas) {
		Path path = new Path();
		float start = 270f - eachAngleSlide / 2;
		//out
		RectF outCircle = new RectF(0, 0, outRadius * 2, outRadius * 2);
		path.arcTo(outCircle, start, eachAngleSlide, false);
		//in
		float innerAdjust = outRadius * 0.02f;
		RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
		path.arcTo(inCircle, start, eachAngleSlide, false);
		path.reset();
		path.arcTo(outCircle, start, eachAngleSlide, false);
		path.arcTo(inCircle, start + eachAngleSlide, -eachAngleSlide, false);
		path.close();
		canvas.drawPath(path, paintSelected);
	}
	
	private void drawSlices(Canvas canvas) {
		float currentAngle = startAngleSlice;
		while (currentAngle < endAngleSlide) {
			double radians = Math.toRadians(currentAngle);
			float x2 = outRadius + (float) Math.cos(radians) * outRadius;
			float y2 = outRadius + (float) Math.sin(radians) * outRadius;
			float x1 = outRadius + (float) Math.cos(radians) * inRadius;
			float y1 = outRadius + (float) Math.sin(radians) * inRadius;
			canvas.drawLine(x1, y1, x2, y2, paintSpokes);
			Path path = new Path();
			//out
			RectF outCircle = new RectF(0, 0, outRadius * 2, outRadius * 2);
			path.arcTo(outCircle, currentAngle, eachAngleSlide, false);
			//in
			float innerAdjust = outRadius - inRadius;
			RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
			path.arcTo(inCircle, currentAngle, eachAngleSlide, false);
			path.reset();
			path.arcTo(outCircle, currentAngle, eachAngleSlide, false);
			path.arcTo(inCircle, currentAngle + eachAngleSlide, -eachAngleSlide, false);
			path.close();
			canvas.drawPath(path, paintBackground);
			currentAngle += eachAngleSlide;
		}
	}
	
}

