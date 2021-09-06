package com.example.harvest.crop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvest.OnClickListener;
import com.example.harvest.R;

import common.BaseFragment;
import data.models.Crop;

public class CropListFragment extends BaseFragment implements OnClickListener
{
	private CropListVM cropListVM;

	private RecyclerView recyclerView;
	private CropAdapter adapter;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		cropListVM = (new ViewModelProvider(requireActivity())).get(CropListVM.class);
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
			launchCropAddFragment();
		}

		return true;
	}

	// Callbacks for user-generated events

	private void launchCropAddFragment()
	{
		navigateTo(R.id.cropListFragment, R.id.add_crop_graph);
	}

	private void deleteCrop(Crop cropToDelete, int position)
	{
		boolean success = cropListVM.deleteCrop(cropToDelete);

		if (success) {
			adapter.notifyItemRemoved(position);
			return;
		}

		String message = "Couldn't remove " + cropToDelete.plant.name;
		Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
	}

	// OnClickListener interface overrides for CropAdapter

	@Override
	public void onClick(View row, int position) {}

	@Override
	public void onLongClick(View row, int position)
	{
		Crop cropToDelete = cropListVM.getCrops().get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setMessage("Are you sure you want to delete " + cropToDelete.plant.name + " crop ?");
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) ->
			deleteCrop(cropToDelete, position)
		);

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
