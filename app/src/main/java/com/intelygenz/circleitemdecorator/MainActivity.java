package com.intelygenz.circleitemdecorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class MainActivity extends AppCompatActivity {
	
	private static final String TAG = "CIRCLE_ITEM_DECORATOR";
	private static final int TOTAL_ITEMS = 100;
	
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Integer> list = new ArrayList<>();
		int total = 11;
		for (int i = 0; i < total; i++) {
			list.add(i);
		}
		recyclerView = findViewById(R.id.recycler_view);
		linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), list));
		recyclerView.addItemDecoration(new CircleItemDecoration());
		final SnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(recyclerView);
		recyclerView
			.getLayoutManager()
			.scrollToPosition(TOTAL_ITEMS / 2);
	}
	
	private float getCenterY(RecyclerView parent) {
		return parent.getTop() + parent.getHeight() / 2;
	}
	
	private float getCenterX(RecyclerView parent) {
		return parent.getLeft() + parent.getWidth() / 2;
	}
	
	private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
		
		private Context context;
		private List<Integer> list;
		
		RecyclerViewAdapter(Context context, List<Integer> list) {
			this.context = context;
			this.list = list;
		}
		
		@NonNull
		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new MyViewHolder(LayoutInflater
										.from(context)
										.inflate(R.layout.item, parent, false));
		}
		
		@Override
		public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		}
		
		@Override
		public int getItemCount() {
			return TOTAL_ITEMS;
		}
		
		class MyViewHolder extends RecyclerView.ViewHolder {
			
			MyViewHolder(View itemView) {
				super(itemView);
			}
		}
		
	}
	
	class CircleItemDecoration extends RecyclerView.ItemDecoration {
		
		private boolean initialized = false;
		
		private float startAngleSlice = -1;
		private float endAngleSlide = -1;
		private float eachAngleSlide = -1;
		
		private float outRadius = -1f;
		private float inRadius = -1f;
		private float middleRadius = -1f;
		private float centerX = -1f;
		private float centerY = -1f;
		private int totalItemsVisible = -1;
		
		private Paint paintSpokes;
		private Paint paintSelected;
		private Paint paintBackground;
		
		CircleItemDecoration() {
			initPaintSpokes();
			initPaintSelected();
			initPaintBackground();
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
		
		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			super.onDraw(c, parent, state);
			if (!initialized) {
				centerX = getCenterX(parent);
				centerY = getCenterY(parent);
				outRadius = parent.getWidth() / 2;
				inRadius = parent.getWidth() / 3;
				middleRadius = outRadius - (outRadius - inRadius) / 2;
				initialized = true;
				totalItemsVisible = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition() + 1;
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
			Log.d(TAG, "parent.isAnimating(): " + parent.isAnimating() + " state: " + parent.getScrollState());
			eachAngleSlide = Math.abs(angles.get(0) - angles.get(1));
			startAngleSlice = angles.get(0) - eachAngleSlide / 2;
			endAngleSlide = angles.get(angles.size() - 1) + eachAngleSlide / 2;
			drawSlices(c);
			if (parent.getScrollState() == SCROLL_STATE_IDLE) {
				drawSelected(c);
			}
		}
		
		@Override
		public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
		}
		
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
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
			while (currentAngle <= endAngleSlide) {
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
	
}
