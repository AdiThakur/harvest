package com.example.harvest.harvest.list;

import android.app.AlertDialog;
import android.os.Bundle;
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

import com.example.harvest.R;
import com.example.harvest.harvest.HarvestAdapter;
import com.example.harvest.harvest.edit.HarvestEditFragment;

import common.BaseFragment;
import common.Event;
import common.Helper;
import common.OnClickListener;
import data.models.Harvest;

public class HarvestListFragment extends BaseFragment implements OnClickListener
{
	private HarvestListVM vm;

	private RecyclerView recyclerView;
	private HarvestAdapter adapter;
	private AlertDialog loadingDialog;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		vm = getProvider(R.id.harvest_nav_graph).get(HarvestListVM.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_harvest_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		setTitle("This Season's Harvests");

		recyclerView = view.findViewById(R.id.harvestList_harvestRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

		observe();
		vm.init();
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.new_item_menu_option, menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		if (id == R.id.addMenu_addButton) {
			launchHarvestAddFragment();
		}

		return true;
	}

	private void observe()
	{
		vm.loading$.observe(getViewLifecycleOwner(), isLoading -> {
			showLoadingDialog(isLoading);
		});

		vm.loadHarvests$.observe(getViewLifecycleOwner(), harvests -> {
			adapter = new HarvestAdapter(getContext(), harvests, this);
			recyclerView.setAdapter(adapter);
		});

		vm.deleteHarvest$.observe(getViewLifecycleOwner(), position -> {
			adapter.notifyItemRemoved(position);
		});

		vm.error$.observe(getViewLifecycleOwner(), (Event<String> e) -> {
			this.displayError(getView(), e);
		});
	}

	// Callbacks for user-generated events

	private void launchHarvestAddFragment()
	{
		navigateTo(
			R.id.harvestListFragment,
			R.id.action_harvestListFragment_to_harvest_add_nav_graph
		);
	}

	// OnClickListener interface overrides for HarvestAdapter

	@Override
	public void onClick(View row, int position) {}

	@Override
	public void onLongClick(View row, int position)
	{
		Harvest harvestToDelete = vm.getHarvests().get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

		builder.setTitle("Delete Harvest?");
		String message =
			"Information about the Harvest of " + harvestToDelete.crop.plant.name +
			" on " + Helper.shortFormatOfDate(harvestToDelete.dateHarvested) + " will be lost!";
		builder.setMessage(message);
		builder.setNegativeButton("No", (dialogInterface, i) -> {});
		builder.setPositiveButton("Yes", (dialogInterface, i) ->
			vm.deleteHarvest(harvestToDelete, position)
		);

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onNestedButtonClick(int position)
	{
		Harvest harvestToEdit = vm.getHarvests().get(position);
		Bundle bundle = new Bundle();
		bundle.putLong(HarvestEditFragment.HARVEST_UID_KEY, harvestToEdit.uid);

		navigateTo(
			R.id.harvestListFragment,
			R.id.action_harvestListFragment_to_harvestEditFragment,
			bundle
		);
	}
}
