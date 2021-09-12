package com.example.harvest.crop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import common.OnClickListener;
import com.example.harvest.R;
import com.example.harvest.harvest.HarvestAddVM;

import common.BaseFragment;
import data.models.Crop;

public class CropListFragment extends BaseFragment implements OnClickListener
{
	private CropListVM cropListVM;
	private HarvestAddVM harvestAddVM;

	private RecyclerView recyclerView;
	private CropAdapter adapter;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// TODO: Pass some basic data to this fragment that enables/disables the selection feature
		String caller = getCaller();
		if (caller.equals("fragment_harvest_add")) {
			harvestAddVM = getProvider(R.id.harvest_add_nav_graph).get(HarvestAddVM.class);
		}

		cropListVM = getProvider(R.id.crop_nav_graph).get(CropListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_crop_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("My Crops");

		adapter = new CropAdapter(getContext(), cropListVM.getCrops(), this);
		recyclerView = view.findViewById(R.id.cropList_cropRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
		recyclerView.setAdapter(adapter);

		cropListVM.deleteCrop$.observe(getViewLifecycleOwner(), this::cropDeletedObserver);
		cropListVM.error$.observe(getViewLifecycleOwner(), this::displayError);
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.add_new_item_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.addMenu_addButton) {
			launchCropAddFragment();
		}

		return true;
	}

	 // Observers

	// TODO: Rework deletion logic
	private void cropDeletedObserver(int position)
	{
		if (harvestAddVM != null) {
			harvestAddVM.setSelectedCrop(null);
		}
		adapter.notifyItemRemoved(position);
	}

	// Callbacks for user-generated events

	private void launchCropAddFragment()
	{
		navigateTo(R.id.cropListFragment, R.id.action_cropListFragment_to_crop_add_graph);
	}

	// OnClickListener interface overrides for CropAdapter

	@Override
	public void onClick(View row, int position) {
		if (harvestAddVM != null) {
			Crop cropSelected = cropListVM.getCrops().get(position);
			harvestAddVM.setSelectedCrop(cropSelected);
			navigateUp();
		}
	}

	@Override
	public void onLongClick(View row, int position)
	{
		Crop cropToDelete = cropListVM.getCrops().get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to delete " + cropToDelete.plant.name + " crop ?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) ->
			cropListVM.deleteCrop(cropToDelete, position)
		);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
