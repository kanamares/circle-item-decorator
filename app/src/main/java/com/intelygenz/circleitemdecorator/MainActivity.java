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
		for (int i = 0; i < 3; i++) {
			list.add(i);
		}
		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), list));
		recyclerView.addItemDecoration(new CircleItemDecoration());
		SnapHelper helper = new LinearSnapHelper();
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
		float bottomRadius = -1f;
		float centerX = -1f;
		float centerY = -1f;
		
		Path myPath;
		RectF outterCircle;
		RectF innerCircle;
		Paint paint;
		
		CircleItemDecoration() {
			myPath = new Path();
			outterCircle = new RectF();
			innerCircle = new RectF();
			paint = new Paint();
			paint.setDither(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setAntiAlias(true);
			
		}
		
		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			super.onDraw(c, parent, state);
			centerX = getCenterX(parent);
			centerY = getCenterY(parent);
			outRadius = parent.getWidth() / 2;
			inRadius = outRadius - (parent
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
				if (x > inRadius) {
					y = outRadius - child.getWidth() / 2;
				} else if (x < -inRadius) {
					y = outRadius - child.getWidth() / 2;
				} else {
					y = (float) (inRadius - Math.sqrt(Math.pow(inRadius, 2) - Math.pow(x, 2)));
				}
				if (i == 0) {
					Log.d(TAG, "child: " + i + " | top: " + childTop + " | left: " + childCenter + " | x: " + x + " | y: " + y);
				}
				child.setY(y);
			}
			drawBackground(c, 0, 60, outRadius);
		}
		
		@Override
		public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
			Paint mPaint = new Paint();
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(1);
			mPaint.setColor(Color.WHITE);
			canvas.drawCircle(centerX, centerY, outRadius, mPaint);
			canvas.drawCircle(centerX, centerY, inRadius, mPaint);
			canvas.drawCircle(centerX, centerY, bottomRadius, mPaint);
		}
		
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		}
		
		private void drawBackground(Canvas canvas, float start, float sweep, float radius) {
			setGradient(0xffffffff, 0xffffffff, radius);
			paint.setStrokeWidth(radius / 14.0f);
			float adjust = .038f * radius;
			outterCircle.set(adjust, adjust, radius * 2 - adjust, radius * 2 - adjust);
			adjust = .276f * radius;
			innerCircle.set(adjust, adjust, radius * 2 - adjust, radius * 2 - adjust);
			myPath.reset();
			myPath.arcTo(outterCircle, start, sweep, false);
			myPath.arcTo(innerCircle, start + sweep, -sweep, false);
			myPath.close();
			canvas.drawPath(myPath, paint);
		}
		
		private void setGradient(int sColor, int eColor, float radius) {
			paint.setShader(
				new RadialGradient(radius, radius, radius - 5, new int[] { sColor, eColor }, new float[] { .6f, .95f }, Shader.TileMode.CLAMP));
		}
		
	}
	
}
