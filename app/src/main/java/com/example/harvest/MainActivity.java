package com.example.harvest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.harvest.crop.CropActivity;
import com.example.harvest.plant.PlantActivity;

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
		Intent intent = new Intent(getApplicationContext(), CropActivity.class);
//		intent.putExtra(PlantActivity.ALLOW_MULTISELECT, false);
		startActivity(intent);
	}
}