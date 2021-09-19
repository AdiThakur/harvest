package com.example.harvest.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import use_cases.GetCurrentSeasonIdUC;

public class HomeVM extends AndroidViewModel
{
	GetCurrentSeasonIdUC getCurrentSeasonIdUC;

	public HomeVM(@NonNull Application application)
	{
		super(application);
		getCurrentSeasonIdUC = new GetCurrentSeasonIdUC(application.getApplicationContext());
	}

	public long getCurrentSeason()
	{
		return getCurrentSeasonIdUC.use();
	}
}
