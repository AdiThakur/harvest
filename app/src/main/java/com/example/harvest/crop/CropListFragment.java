package com.example.harvest.crop;

import android.app.Activity;
import android.os.Bundle;
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

import com.example.harvest.OnClickListener;
import com.example.harvest.R;
import com.example.harvest.plant.PlantAdapter;
import com.example.harvest.plant.PlantAddFragment;

import java.util.List;

import data.models.Crop;

public class CropListFragment extends Fragment implements OnClickListener
{
	private RecyclerView recyclerView;
	private CropAdapter adapter;

	private CropVM cropVM;
	private List<Crop> crops;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Activity activity = getActivity();
		if (activity != null) {
			activity.setTitle("My Crops");
		}

		cropVM = (new ViewModelProvider(requireActivity())).get(CropVM.class);
		cropVM.lookupCrops();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_crop_list, container, false);
		Button addPlant = view.findViewById(R.id.cropList_addCropButton);
		addPlant.setOnClickListener((v) -> launchCropAddFragment());

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		recyclerView = view.findViewById(R.id.cropList_cropRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

		cropVM.lookupCrops().observe(getViewLifecycleOwner(), newCrops -> {
			crops = newCrops;
			adapter = new CropAdapter(requireActivity(), crops, this);
			recyclerView.setAdapter(adapter);
		});
	}

	// OnClickListener interface overrides for CropAdapter

	public void launchCropAddFragment()
	{
//		FragmentManager fragmentManager = getParentFragmentManager();
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		transaction.replace(R.id.crop_fragmentContainerView, CropAddFragment.class, null);
//		transaction.addToBackStack(null);
//		transaction.commit();

		new CropAddFragment().show(getChildFragmentManager(), "CropAddDialog");
	}

	@Override
	public void onClick(View row, int position) {

	}

	@Override
	public void onLongClick(View row, int position) {

	}
}