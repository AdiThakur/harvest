package com.example.harvest.plant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Pair;
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
import com.example.harvest.crop.CropAddVM;

import common.BaseFragment;
import data.models.Plant;

public class PlantListFragment extends BaseFragment implements OnClickListener
{
	private PlantListVM plantListVM;
	private CropAddVM cropAddVM;

	private RecyclerView recyclerView;
	private PlantAdapter adapter;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// TODO: Pass some basic data to this fragment that enables/disables the selection feature
		String caller = getCaller();
		if (caller.equals("fragment_crop_add")) {
			cropAddVM = getProvider(R.id.crop_add_graph).get(CropAddVM.class);
		}

		plantListVM = getProvider(R.id.plant_nav_graph).get(PlantListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_plant_list, container, false);
	}

	@SuppressLint("FragmentLiveDataObserve")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("My Plants");

		adapter = new PlantAdapter(getContext(), plantListVM.getPlants(), this);
		recyclerView = view.findViewById(R.id.plantList_plantRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
		recyclerView.setAdapter(adapter);

		plantListVM.deletePlant$.observe(getViewLifecycleOwner(), this::plantDeletedObserver);
		plantListVM.error$.observe(getViewLifecycleOwner(), this::displayError);
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
		if (item.getItemId() == R.id.addMenu_addButton) {
			launchPlantAddFragment();
		}

		return true;
	}

	// Observers

	private void plantDeletedObserver(Pair<Long, Integer> deletedPlantInfo)
	{
		long deletedPlantUid = deletedPlantInfo.first;
		int deletedPlantPosition = deletedPlantInfo.second;

		// Only reset CropAddVM's selectedPlant Plant if it was deleted.
		if (cropAddVM != null && deletedPlantUid == cropAddVM.getSelectedPlant().uid) {
			cropAddVM.setSelectedPlant(null);
		}

		adapter.notifyItemRemoved(deletedPlantPosition);
	}

	// Callbacks for user-generated events

	private void launchPlantAddFragment()
	{
		navigateTo(R.id.plantListFragment, R.id.action_plantListFragment_to_plantAddFragment);
	}

	// OnClickListener interface overrides for PlantAdapter

	@Override
	public void onClick(View row, int position)
	{
		if (cropAddVM != null) {
			Plant selectedPlant = plantListVM.getPlants().get(position);
			cropAddVM.setSelectedPlant(selectedPlant);
			navigateUp();
		}
	}

	@Override
	public void onLongClick(View row, int position)
	{
		Plant plantToDelete = plantListVM.getPlants().get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to delete " + plantToDelete.name + " ?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) ->
			plantListVM.deletePlant(plantToDelete, position)
		);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
