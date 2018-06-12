package com.intelygenz.circleitemdecorator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import java.util.List;

public class CircularRecyclerView extends RecyclerView {
	
	private static final String TAG = "CIRCLE_ITEM_DECORATOR";
	private static final int TOTAL_ITEMS = Integer.MAX_VALUE;
	
	private CircularAdapter.OnItemClickListener onItemClickListener;
	
	public CircularRecyclerView(Context context) {
		super(context);
		init(context);
	}
	
	public CircularRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public CircularRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setItems(List<Integer> list) {
		setAdapter(new CircularAdapter(list, onItemClickListener));
		int middle = TOTAL_ITEMS / 2;
		final int center = (middle / list.size()) * list.size();
		getLayoutManager().scrollToPosition(center);
	}
	
	private void init(Context context) {
		if (isInEditMode()) {
			return;
		}
		setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
		onItemClickListener = new CircularAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(int center) {
				smoothScrollBy(center - getWidth() / 2, 0);
			}
		};
		addItemDecoration(new CircularItemDecoration());
		final LinearSnapHelper helper = new LinearSnapHelper();
		helper.attachToRecyclerView(this);
	}
	
}
