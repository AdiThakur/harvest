package com.example.harvest.harvest;

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

import common.BaseFragment;
import common.OnClickListener;

public class HarvestListFragment extends BaseFragment implements OnClickListener
{
	private HarvestListVM harvestListVM;

	private RecyclerView recyclerView;
	private HarvestAdapter adapter;

	// Lifecycle overrides

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		harvestListVM = getProvider(this).get(HarvestListVM.class);
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
		setTitle("My Harvests");

		adapter = new HarvestAdapter(getContext(), harvestListVM.getHarvests(), this);
		recyclerView = view.findViewById(R.id.harvestList_harvestRcv);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
		recyclerView.setAdapter(adapter);

		harvestListVM.error$.observe(getViewLifecycleOwner(), this::displayError);
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
			launchHarvestAddFragment();
		}

		return true;
	}

	// Callbacks for user-generated events

	private void launchHarvestAddFragment()
	{
		navigateTo(R.id.harvestListFragment, R.id.action_harvestListFragment_to_harvest_add_nav_graph);
	}

	// OnClickListener interface overrides for HarvestAdapter

	@Override
	public void onClick(View row, int position) {

	}

	@Override
	public void onLongClick(View row, int position) {

	}
}
