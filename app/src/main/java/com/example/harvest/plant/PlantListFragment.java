package com.example.harvest.plant;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
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

	private RecyclerView recyclerView;
	private PlantAdapter adapter;
	private Button confirmButton;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		cropAddVM = (new ViewModelProvider(getStoreOwner(R.id.add_crop_graph))).get(CropAddVM.class);
		plantVM = (new ViewModelProvider(getStoreOwner(R.id.select_plant_graph))).get(PlantVM.class);
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

		confirmButton = view.findViewById(R.id.plantList_confirmButton);
		confirmButton.setEnabled(false);
		confirmButton.setOnClickListener((v) -> confirmSelection(true));

		Button cancelButton = view.findViewById(R.id.plantList_cancelButton);
		cancelButton.setOnClickListener((v) -> confirmSelection(false));

		adapter = new PlantAdapter(getContext(), plantVM.getPlants(), this);
		recyclerView = view.findViewById(R.id.plantList_plantRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
		recyclerView.setAdapter(adapter);

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
		int id = item.getItemId();

		if (id == R.id.addMenu_addButton) {
			launchPlantAddFragment();
		}

		return true;
	}

	// Observers

	public void plantSelectedObserver(Pair<Integer, Integer> positions)
	{
		int oldPos = positions.first;
		int newPos = positions.second;

		// Clear previously selected row's style
		if (oldPos != -1) {
			PlantViewHolder oldViewHolder =
				(PlantViewHolder) recyclerView.findViewHolderForAdapterPosition(oldPos);
			if (oldViewHolder != null) {
				oldViewHolder.row.setBackgroundColor(
					getResources().getColor(R.color.card_grey)
				);
			}
		}

		PlantViewHolder newViewHolder =
			(PlantViewHolder) recyclerView.findViewHolderForAdapterPosition(newPos);
		if (newViewHolder != null) {
			newViewHolder.row.setBackgroundColor(
				getResources().getColor(R.color.card_selected_blue)
			);
		}

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
}
