package com.example.harvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.harvest.R;
import com.example.harvest.crop.CropAddActivity;
import com.example.harvest.plant.PlantListActivity;

import data.bridges.SeasonBridge;

public class MainActivity extends AppCompatActivity
{
	SeasonBridge seasonBridge;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void test(View view)
	{
		Intent intent = new Intent(getApplicationContext(), PlantListActivity.class);
		startActivity(intent);
	}
}