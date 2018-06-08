package com.intelygenz.circleitemdecorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
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
	
	private RecyclerView recyclerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
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
			.scrollToPosition(Integer.MAX_VALUE / 2);
	}
	
	private int getCenterY(RecyclerView parent) {
		int y = parent.getTop() + parent.getHeight() / 2;
		return y;
	}
	
	private int getCenterX(RecyclerView parent) {
		int x = parent.getLeft() + parent.getWidth() / 2;
		return x;
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
			return Integer.MAX_VALUE;
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
		
		float outRadius = -1f;
		float inRadius = -1f;
		float middleRadius = -1f;
		float bottomRadius = -1f;
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
			middleRadius = outRadius - (parent
				                            .getChildAt(0)
				                            .getHeight() / 2);
			bottomRadius = outRadius - parent
				.getChildAt(0)
				.getHeight();
			final int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				double childTop = child.getTop();
				double childCenter = child.getLeft() + child.getWidth() / 2;
				double x = childCenter - outRadius;
				float y = 0;
				if (x > middleRadius) {
					y = outRadius - child.getWidth() / 2;
				} else if (x < -middleRadius) {
					y = outRadius - child.getWidth() / 2;
				} else {
					y = (float) (middleRadius - Math.sqrt(Math.pow(middleRadius, 2) - Math.pow(x, 2)));
				}
				if (i == 0) {
					Log.d(TAG, "child: " + i + " | top: " + childTop + " | left: " + childCenter + " | x: " + x + " | y: " + y);
				}
				child.setY(y);
			}
			drawBackground(c);
			drawGuides(c);
			drawSeparators(c);
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
			path.arcTo(outCircle, 0, 359.99f, false);
			
			//in
			float innerAdjust = outRadius - inRadius;
			RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
			path.arcTo(inCircle, 0, 359.99f, false);
			
			//middle
			float middleAdjust = outRadius - middleRadius;
			RectF middleCircle = new RectF(middleAdjust, middleAdjust, outRadius * 2 - middleAdjust, outRadius * 2 - middleAdjust);
			path.arcTo(middleCircle, 0, 359.99f, false);
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
			path.arcTo(outCircle, 180, 180f, false);
			
			//in
			float innerAdjust = outRadius - inRadius;
			RectF inCircle = new RectF(innerAdjust, innerAdjust, outRadius * 2 - innerAdjust, outRadius * 2 - innerAdjust);
			path.arcTo(inCircle, 180, 180f, false);
			
			path.reset();
			path.arcTo(outCircle, 180, 180, false);
			path.arcTo(inCircle, 180 + 180, -180, false);
			path.close();
			
			canvas.drawPath(path, paint);
		}
		
		private void drawSeparators(Canvas canvas) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);
			paint.setColor(Color.GREEN);
			canvas.drawLine(outRadius, outRadius - inRadius, outRadius, 0, paint);
		}
		
	}
	
}
