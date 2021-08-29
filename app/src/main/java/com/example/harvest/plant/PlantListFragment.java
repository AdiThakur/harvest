package com.example.harvest.plant;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.harvest.OnClickListener;
import com.example.harvest.R;
import data.models.Plant;

public class PlantListFragment extends Fragment implements OnClickListener
{
	private RecyclerView recyclerView;
	private PlantAdapter adapter;

	private PlantVM plantVM;
	private List<Plant> plants;
	private int selectedPlantPosition = -1;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActivity().setTitle("My Plants");
		plantVM = new ViewModelProvider(requireActivity()).get(PlantVM.class);
		plantVM.lookupPlants();
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

		plantVM.lookupPlants().observe(getViewLifecycleOwner(), newPlants -> {
			plants = newPlants;
			adapter = new PlantAdapter(requireActivity(), plants, this);
			recyclerView.setAdapter(adapter);
			selectedPlantPosition = -1;
		});
	}

	// Callbacks for user-generated events

	public void launchPlantAddFragment()
	{
		FragmentManager fragmentManager = getParentFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.plant_fragmentContainerView, PlantAddFragment.class, null);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void deletePlant(Plant plantToDelete)
	{
		String message;
		boolean success = plantVM.deletePlant(plantToDelete);

		if (success) {
			message = plantToDelete.name + " removed!";
		} else {
			message = "Couldn't remove " + plantToDelete.name;
		}

		Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
	}

	// OnClickListener interface overrides for PlantAdapter

	@Override
	public void onClick(View row, int position)
	{
		// Clear previously selected row
		if (selectedPlantPosition != -1) {
			PlantViewHolder oldViewHolder =
				(PlantViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedPlantPosition);
			if (oldViewHolder != null) {
				oldViewHolder.row.setBackgroundColor(getResources().getColor(R.color.card_grey));
			}
		}

		row.setBackgroundColor(getResources().getColor(R.color.card_selected_blue));
		selectedPlantPosition = position;
	}

	@Override
	public void onLongClick(View row, int position)
	{
		Plant plantToDelete = plants.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to delete " + plantToDelete.name + " ?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) -> deletePlant(plantToDelete));

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}