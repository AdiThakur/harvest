package com.example.harvest.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import use_cases.GetCurrentSeasonUC;

public class HomeVM extends AndroidViewModel
{
	GetCurrentSeasonUC getCurrentSeasonUC;

	public HomeVM(@NonNull Application application)
	{
		super(application);
		getCurrentSeasonUC = new GetCurrentSeasonUC(application.getApplicationContext());
	}

	public long getCurrentSeason()
	{
		return getCurrentSeasonUC.use().year;
	}
}
