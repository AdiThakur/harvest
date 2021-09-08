package com.example.harvest.plant;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import common.OnClickListener;
import com.example.harvest.R;
import com.example.harvest.crop.CropAddVM;

import common.BaseFragment;
import data.models.Plant;

public class PlantListFragment extends BaseFragment implements OnClickListener
{
	private PlantVM plantVM;
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

		plantVM = getProvider(R.id.plant_nav_graph).get(PlantVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_plant_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("My Plants");

		adapter = new PlantAdapter(getContext(), plantVM.getPlants(), this);
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

		plantVM.selectedPlantObservable.observe(
			getViewLifecycleOwner(), this::plantSelectedObserver
		);
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_add_menu , menu);
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

	public void plantSelectedObserver(Pair<Integer, Integer> positions)
	{
		paintRow(positions.first, R.color.card_grey);
		paintRow(positions.second, R.color.card_selected_blue);
		confirmButton.setEnabled(true);
	}

	// Callbacks for user-generated events

	private void launchPlantAddFragment()
	{
		navigateTo(R.id.plantListFragment, R.id.action_plantListFragment_to_plantAddFragment);
	}

	private void deletePlant(Plant plantToDelete, int position)
	{
		boolean success = plantVM.deletePlant(plantToDelete);

		if (success) {
			adapter.notifyItemRemoved(position);
			return;
		}

		String message = "Couldn't remove " + plantToDelete.name;
		Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
	}

	private void confirmSelection(boolean confirmed)
	{
		if (confirmed) {
			Plant selectedPlant = plantVM.getPlants().get(plantVM.selectedPlantPosition);
			cropAddVM.setSelectedPlant(selectedPlant);
		}

		navigateUp();
	}

	// OnClickListener interface overrides for PlantAdapter

	@Override
	public void onClick(View row, int position)
	{
		plantVM.setSelectedPlantPosition(position);
	}

	@Override
	public void onLongClick(View row, int position)
	{
		Plant plantToDelete = plantVM.getPlants().get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to delete " + plantToDelete.name + " ?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) ->
			deletePlant(plantToDelete, position)
		);

		AlertDialog dialog = builder.create();
		dialog.show();
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
			viewHolder.row.setBackgroundColor(
				getResources().getColor(color)
			);
		}
	}
}
