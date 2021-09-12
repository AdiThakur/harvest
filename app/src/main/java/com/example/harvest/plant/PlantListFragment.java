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
import android.widget.Button;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
	private boolean allowSelection;

	private RecyclerView recyclerView;
	private PlantAdapter adapter;
	private Button confirmButton;

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
			allowSelection = true;
		} else {
			cropAddVM = getProvider(R.id.plant_nav_graph).get(CropAddVM.class);
			allowSelection = false;
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

		confirmButton = view.findViewById(R.id.plantList_confirmButton);
		Button cancelButton = view.findViewById(R.id.plantList_cancelButton);

		if (allowSelection) {
			confirmButton.setEnabled(false);
			confirmButton.setOnClickListener((v) -> confirmSelection(true));
			cancelButton.setOnClickListener((v) -> confirmSelection(false));
		} else {
			confirmButton.setVisibility(View.GONE);
			cancelButton.setVisibility(View.GONE);

			// Force RecyclerView to take up entire screen
			ConstraintLayout constraintLayout = view.findViewById(R.id.plantList_constraintView);
			ConstraintSet constraintSet = new ConstraintSet();
			constraintSet.clone(constraintLayout);
			constraintSet.connect(
				R.id.plantList_plantRcv, ConstraintSet.BOTTOM,
				R.id.plantList_constraintView, ConstraintSet.BOTTOM,0);
			constraintSet.applyTo(constraintLayout);
		}

		plantListVM.selectedPlant$.observe(getViewLifecycleOwner(), this::plantSelectedObserver);
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

	private void plantSelectedObserver(Pair<Integer, Integer> positions)
	{
		paintRow(positions.first, R.color.card_grey);
		paintRow(positions.second, R.color.card_selected_blue);
		confirmButton.setEnabled(true);
	}

	// TODO: Rework deletion logic; currently, the selected plant in CropAdd fragment is cleared when ANY plant is deleted
	private void plantDeletedObserver(int position)
	{
		cropAddVM.setSelectedPlant(null);
		adapter.notifyItemRemoved(position);
	}

	// Callbacks for user-generated events

	private void launchPlantAddFragment()
	{
		navigateTo(R.id.plantListFragment, R.id.action_plantListFragment_to_plantAddFragment);
	}

	private void confirmSelection(boolean confirmed)
	{
		if (confirmed) {
			Plant selectedPlant = plantListVM.getPlants().get(plantListVM.selectedPlantPosition);
			cropAddVM.setSelectedPlant(selectedPlant);
		}

		navigateUp();
	}

	// Helpers

	private void paintRow(int position, @ColorRes int color)
	{
		if (position == -1) {
			return;
		}

		PlantViewHolder viewHolder =
			(PlantViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
		if (viewHolder != null) {
			viewHolder.row.setBackgroundColor(getResources().getColor(color));
		}
	}

	// OnClickListener interface overrides for PlantAdapter

	@Override
	public void onClick(View row, int position)
	{
		plantListVM.setSelectedPlantPosition(position);
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
