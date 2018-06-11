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
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	
	private static final String TAG = "CIRCLE_ITEM_DECORATOR";
	private static final int TOTAL_ITEMS = 100;
	
	private RecyclerView recyclerView;
	
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
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), list));
		recyclerView.addItemDecoration(new CircleItemDecoration());
		final SnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(recyclerView);
		recyclerView
			.getLayoutManager()
			.scrollToPosition(TOTAL_ITEMS / 2);
	}
	
	private int getCenterY(RecyclerView parent) {
		return parent.getTop() + parent.getHeight() / 2;
	}
	
	private int getCenterX(RecyclerView parent) {
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
			holder.textView.setText("P:" + position + "\nL:" + getItem(position));
		}
		
		@Override
		public int getItemCount() {
			return TOTAL_ITEMS;
		}
		
		private int getItem(int position) {
			return position % list.size();
		}
		
		class MyViewHolder extends RecyclerView.ViewHolder {
			
			private TextView textView;
			
			MyViewHolder(View itemView) {
				super(itemView);
				textView = itemView.findViewById(R.id.text);
			}
		}
		
	}
	
	class CircleItemDecoration extends RecyclerView.ItemDecoration {
		
		int startAngleSlice = 180 - 15;
		
		float outRadius = -1f;
		float inRadius = -1f;
		float middleRadius = -1f;
		float centerX = -1f;
		float centerY = -1f;
		
		CircleItemDecoration() {
		}
		
		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			super.onDraw(c, parent, state);
			centerX = getCenterX(parent);
			centerY = getCenterY(parent);
			outRadius = parent.getWidth() / 2;
			inRadius = parent.getWidth() / 3;
			middleRadius = outRadius - (outRadius - inRadius) / 2;
			final int childCount = parent.getChildCount();
			Log.d(TAG, "childCount: " + childCount + " centerX: " + centerX + " centerY: " + centerY);
			for (int i = 0; i < childCount; i++) {
				if(i > 7) continue;
				final View child = parent.getChildAt(i);
				float childCenter = child.getLeft() + child.getWidth() / 2;
				float totalX;
				float currentItemAngle;
				if(childCenter < centerX) {
					totalX = centerX - childCenter;
					currentItemAngle = 270 - (90 * totalX / outRadius);
				} else {
					totalX = childCenter;
					currentItemAngle = 180 + (90 * totalX / outRadius);
				}
				if(currentItemAngle < 180 || currentItemAngle > 360) {
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
			drawBackground(c);
			drawSelected(c);
			drawGuides(c);
			drawSpokes(c, parent.getChildAt(3));
		}
		
		@Override
		public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
		}
		
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		}
		
		private void drawGuides(Canvas canvas) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			paint.setColor(Color.BLUE);
			Path path = new Path();
			//out
			RectF outCircle = new RectF(0, 0, outRadius * 2, outRadius * 2);
			path.arcTo(outCircle, 0, 359.9999f, false);
			//in
			float innerAdjust = outRadius - inRadius;
			RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
			path.arcTo(inCircle, 0, 359.9999f, false);
			//middle
			float middleAdjust = outRadius - middleRadius;
			RectF middleCircle = new RectF(middleAdjust, middleAdjust, outRadius * 2 - middleAdjust, outRadius * 2 - middleAdjust);
			path.arcTo(middleCircle, 0, 359.9999f, false);
			path.close();
			canvas.drawPath(path, paint);
		}
		
		private void drawBackground(Canvas canvas) {
			Path path = new Path();
			Paint paint = new Paint();
			paint.setDither(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setColor(Color.WHITE);
			paint.setAntiAlias(true);
			//out
			RectF outCircle = new RectF(0, 0, outRadius * 2, outRadius * 2);
			path.arcTo(outCircle, startAngleSlice, 210f, false);
			//in
			float innerAdjust = outRadius - inRadius;
			RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
			path.arcTo(inCircle, startAngleSlice, 210f, false);
			path.reset();
			path.arcTo(outCircle, startAngleSlice, 210f, false);
			path.arcTo(inCircle, startAngleSlice + 210, -210, false);
			path.close();
			canvas.drawPath(path, paint);
		}
		
		private void drawSelected(Canvas canvas) {
			Path path = new Path();
			Paint paint = new Paint();
			paint.setDither(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setColor(Color.RED);
			paint.setAntiAlias(true);
			//out
			RectF outCircle = new RectF(0, 0, outRadius * 2, outRadius * 2);
			path.arcTo(outCircle, 255f, 30f, false);
			//in
			float innerAdjust = outRadius * 0.02f;
			RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
			path.arcTo(inCircle, 255, 30f, false);
			path.reset();
			path.arcTo(outCircle, 255, 30f, false);
			path.arcTo(inCircle, 255 + 30, -30, false);
			path.close();
			canvas.drawPath(path, paint);
		}
		
		private void drawSpokes(Canvas canvas, View child) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			//Spokes
			for (int i = 0; i < 15; i++) {
				if (i % 2 != 0) {
					paint.setColor(Color.GREEN);
				} else {
					paint.setColor(Color.BLUE);
				}
				int currentAngle = startAngleSlice + (15 * i);
				double radians = Math.toRadians(currentAngle);
				float x2 = outRadius + (float) Math.cos(radians) * outRadius;
				float y2 = outRadius + (float) Math.sin(radians) * outRadius;
				float x1 = outRadius + (float) Math.cos(radians) * inRadius;
				float y1 = outRadius + (float) Math.sin(radians) * inRadius;
				canvas.drawLine(x1, y1, x2, y2, paint);
			}
		}
		
	}
	
}
