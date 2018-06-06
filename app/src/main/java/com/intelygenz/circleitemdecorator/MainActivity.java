package com.intelygenz.circleitemdecorator;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			list.add(i);
		}
		RecyclerView recyclerView = findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), list));
		recyclerView.addItemDecoration(new CircleItemDecoration());
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
			return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
		}
		
		@Override
		public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		}
		
		@Override
		public int getItemCount() {
			return list.size();
		}
		
		class MyViewHolder extends RecyclerView.ViewHolder {
			MyViewHolder(View itemView) {
				super(itemView);
			}
		}
		
	}
	
	class CircleItemDecoration extends RecyclerView.ItemDecoration {
		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		}
	}
	
}
