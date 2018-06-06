package com.intelygenz.circleitemdecorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
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
		for (int i = 0; i < 11; i++) {
			list.add(i);
		}
		recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), list));
		recyclerView.addItemDecoration(new CircleItemDecoration());
		SnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(recyclerView);
		recyclerView.getLayoutManager().scrollToPosition(Integer.MAX_VALUE / 2);
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
			holder.textView.setText("P:"+position+"|L:"+getItem(position));
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
		
		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			super.onDraw(c, parent, state);
			int centerX = getCenterX(parent);
			int centerY = getCenterY(parent);
			final int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				int childTop = child.getTop();
				int childLeft = child.getLeft();
				Log.d(TAG, "child: " + i + " | top: " + childTop + " | left: " + childLeft);
				child.setY((Math.abs(centerX - childLeft)) / 2);
			}
			Log.d(TAG, "");
			Log.d(TAG, "");
		}
		
		@Override
		public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
			super.onDrawOver(c, parent, state);
		}
		
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		}
		
	}
	
}
