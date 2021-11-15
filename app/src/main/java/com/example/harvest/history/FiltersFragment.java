package com.example.harvest.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harvest.R;

import java.util.List;

import common.BaseFragment;
import data.models.Harvest;

public class FiltersFragment extends BaseFragment
{
	private FiltersVM filtersVM;

	private TextView selectSeasonsTextView;
	private TextView selectCropsTextView;
	private Button filterDataButton;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		filtersVM = getProvider(R.id.history_nav_graph).get(FiltersVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_filters, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("Apply Filters");

		selectSeasonsTextView = view.findViewById(R.id.filters_selectSeasons);
		selectSeasonsTextView.setOnClickListener(v -> {
			filtersVM.showSeasonsMultiChoice(getContext());
		});
		filtersVM.yearsMultiChoice.selected$.subscribe(selectedSeasons -> {
			selectSeasonsTextView.setText(selectedSeasons.toString());
			selectCropsTextView.setText("");
			selectCropsTextView.setEnabled(true);
		});

		selectCropsTextView = view.findViewById(R.id.filters_selectCrops);
		selectCropsTextView.setEnabled(false);
		selectCropsTextView.setOnClickListener(v -> {
			filtersVM.showCropsMultiChoice(getContext());
		});
		filtersVM.cropsMultiChoice.selected$.subscribe(selectedCrops -> {
			selectCropsTextView.setText(selectedCrops.toString());
		});

		filterDataButton = view.findViewById(R.id.filters_filterDataButton);
		filterDataButton.setOnClickListener(v -> {
			List<Harvest> harvests = filtersVM.filterData();
			harvests.forEach(harvest -> {
				Log.println(Log.DEBUG, "FiltersFragment", harvest.toString());
			});
		});
	}
}