package com.intelygenz.circleitemdecorator;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

class CircularAdapter extends RecyclerView.Adapter<CircularAdapter.CircularHolder> {
	
	private static final int TOTAL_ITEMS = Integer.MAX_VALUE;
	
	private List<Integer> list;
	private final OnItemClickListener listener;
	
	CircularAdapter(List<Integer> list, OnItemClickListener listener) {
		this.list = list;
		this.listener = listener;
	}
	
	@NonNull
	@Override
	public CircularHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new CircularHolder(LayoutInflater
									.from(parent.getContext())
									.inflate(R.layout.circular_item, parent, false));
	}
	
	@Override
	public void onBindViewHolder(@NonNull CircularHolder holder, int position) {
		holder.bind(getItem(position), listener);
	}
	
	private int getItem(int position) {
		return position % list.size();
	}
	
	@Override
	public int getItemCount() {
		return TOTAL_ITEMS;
	}
	
	class CircularHolder extends RecyclerView.ViewHolder {
		
		CircularHolder(View itemView) {
			super(itemView);
		}
		
		void bind(final int item, final OnItemClickListener listener) {
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onItemClick(v.getLeft() + v.getWidth() / 2);
				}
			});
		}
		
	}
	
	interface OnItemClickListener {
		
		void onItemClick(int center);
	}
	
}