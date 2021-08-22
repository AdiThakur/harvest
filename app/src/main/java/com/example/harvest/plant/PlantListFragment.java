package com.example.harvest.plant;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvest.R;

import java.util.ArrayList;
import java.util.List;

import data.models.Plant;

public class PlantListFragment extends Fragment
{
	private RecyclerView recyclerView;
	private PlantAdapter adapter;

	private PlantVM vm;
	private boolean allowMultiSelect = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		allowMultiSelect = requireArguments().getBoolean(PlantActivity.ALLOW_MULTISELECT);

		vm = new ViewModelProvider(requireActivity()).get(PlantVM.class);
		vm.lookupPlants();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_plant_list, container, false);
		Button addPlant = view.findViewById(R.id.plantList_addPlantButton);
		addPlant.setOnClickListener((v) -> launchPlantAddFragment());

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		recyclerView = view.findViewById(R.id.plantList_plantRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

		vm.lookupPlants().observe(getViewLifecycleOwner(), newPlants -> {
			adapter = new PlantAdapter(requireActivity(), newPlants);
			recyclerView.setAdapter(adapter);
		});
	}

	public void launchPlantAddFragment()
	{
		FragmentManager fragmentManager = getParentFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.plant_fragmentContainerView, PlantAddFragment.class, null);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}