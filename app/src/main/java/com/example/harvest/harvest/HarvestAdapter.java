package com.example.harvest.harvest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvest.R;

import java.text.DecimalFormat;
import java.util.List;

import common.Helper;
import common.OnClickListener;
import data.models.Harvest;

class HarvestViewHolder extends RecyclerView.ViewHolder
{
	public TextView dateHarvestedTextView;
	public ImageView cropImageView;
	public TextView cropNameTextView;
	public TextView unitsHarvestedTagTextView;
	public TextView unitsHarvestedTextView;
	public TextView totalWeightTextView;
	public TextView totalWeightTagTextView;
	public ImageView editHarvestImageView;

	public HarvestViewHolder(@NonNull View row, OnClickListener listener)
	{
		super(row);

		ConstraintLayout dateContainer = row.findViewById(R.id.harvestRcvItem_dateContainer);
		dateHarvestedTextView = dateContainer.findViewById(R.id.harvestRcvItem_dateHarvestedTextView);

		cropImageView = row.findViewById(R.id.harvestRcvItem_cropImage);
		cropNameTextView = row.findViewById(R.id.harvestRcvItem_cropNameTextView);
		totalWeightTagTextView = row.findViewById(R.id.harvestRcvItem_totalWeightTagTextView);
		totalWeightTextView = row.findViewById(R.id.harvestRcvItem_totalWeightTextView);
		unitsHarvestedTagTextView = row.findViewById(R.id.harvestRcvItem_unitsHarvestedTagTextView);
		unitsHarvestedTextView = row.findViewById(R.id.harvestRcvItem_unitsHarvestedTextView);
		editHarvestImageView = row.findViewById(R.id.harvestRcvItem_editHarvestImageView);

		row.setOnClickListener((View v )-> listener.onClick(v, getAdapterPosition()));
		row.setOnLongClickListener((View v) -> {
			listener.onLongClick(v, getAdapterPosition());
			return true;
		});
		editHarvestImageView.setOnClickListener((View v) -> {
			listener.onNestedButtonClick(getAdapterPosition());
		});
	}
}

public class HarvestAdapter extends RecyclerView.Adapter<HarvestViewHolder>
{
	private final List<Harvest> harvests;
	private final Context context;
	private final OnClickListener listener;

	public HarvestAdapter(Context context, List<Harvest> harvests, OnClickListener listener)
	{
		this.context = context;
		this.harvests = harvests;
		this.listener = listener;
	}

	@NonNull
	@Override
	public HarvestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View harvestRowItem = inflater.inflate(R.layout.harvest_rcv_item, parent, false);

		return new HarvestViewHolder(harvestRowItem, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull HarvestViewHolder holder, int position)
	{
		Harvest harvest = harvests.get(position);
		String filename = harvest.crop.plant.imageFileName;
		DecimalFormat df = new DecimalFormat("#.00");

		String totalWeightString = "";
		if (harvest.totalWeight != 0) {
			totalWeightString = df.format(harvest.totalWeight);
		} else {
			if (harvest.crop.plant.unitWeight == 0) {
				totalWeightString = "No data";
			} else {
				double estimatedTotalWeight = harvest.unitsHarvested * harvest.crop.plant.unitWeight;
				totalWeightString = df.format(estimatedTotalWeight);
				holder.totalWeightTagTextView.setText("Estimated Weight (g)");
			}
		}

		String unitsHarvestedString = "";
		if (harvest.unitsHarvested != 0) {
			unitsHarvestedString = String.valueOf(harvest.unitsHarvested);
		} else {
			if (harvest.crop.plant.unitWeight == 0) {
				unitsHarvestedString = "No data";
			} else {
				int estimatedUnitsHarvested = (int) (harvest.totalWeight / harvest.crop.plant.unitWeight);
				unitsHarvestedString = String.valueOf(estimatedUnitsHarvested);
				holder.unitsHarvestedTagTextView.setText("Estimated Units");
			}
		}

		holder.dateHarvestedTextView.setText(Helper.shortFormatOfDate(harvest.dateHarvested));
		holder.cropImageView.setImageBitmap(Helper.loadBitmapFromImage(context, filename));
		holder.cropNameTextView.setText(harvest.crop.plant.name);
		holder.totalWeightTextView.setText(totalWeightString);
		holder.unitsHarvestedTextView.setText(unitsHarvestedString);
	}

	@Override
	public int getItemCount()
	{
		return harvests.size();
	}
}
