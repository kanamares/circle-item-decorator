package com.intelygenz.circleitemdecorator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List<Integer> list = new ArrayList<>();
		final int total = 7;
		for (int i = 0; i < total; i++) {
			list.add(i);
		}
		CircularRecyclerView circularRecyclerView = findViewById(R.id.recycler_view);
		circularRecyclerView.setItems(list);
	}
	
}
