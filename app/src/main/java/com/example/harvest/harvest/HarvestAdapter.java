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
	public ImageView cropImageView;
	public TextView cropNameTextView;
	public TextView harvestDateTextView;
	public TextView unitsHarvestedTextView;
	public TextView totalWeightTextView;

	public HarvestViewHolder(@NonNull View harvestRowItem, OnClickListener listener)
	{
		super(harvestRowItem);

		cropImageView = harvestRowItem.findViewById(R.id.harvestRcvItem_cropImage);

		ConstraintLayout infoContainer = harvestRowItem.findViewById(R.id.harvestRcvItem_InfoContainer);
		cropNameTextView = infoContainer.findViewById(R.id.harvestRcvItem_cropNameText);
		harvestDateTextView = infoContainer.findViewById(R.id.harvestRcvItem_cropDatePlantedText);
		totalWeightTextView = infoContainer.findViewById(R.id.harvestRcvItem_TotalWeightText);

		ConstraintLayout countContainer = harvestRowItem.findViewById(R.id.harvestRcv_countContainer);
		unitsHarvestedTextView = countContainer.findViewById(R.id.harvestRcvItem_TotalCountText);

		harvestRowItem.setOnClickListener(view -> listener.onClick(view, getAdapterPosition()));
		harvestRowItem.setOnLongClickListener(view -> {
			listener.onLongClick(view, getAdapterPosition());
			return true;
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

		holder.cropImageView.setImageBitmap(
			Helper.loadBitmapFromImage(context, harvest.crop.plant.imageFileName)
		);
		holder.cropNameTextView.setText(harvest.crop.plant.name);
		holder.harvestDateTextView.setText(Helper.shortFormatOfDate(harvest.dateHarvested));

		double totalWeight = harvest.count * harvest.crop.plant.unitWeight;
		DecimalFormat df = new DecimalFormat("#.00");
		holder.totalWeightTextView.setText(df.format(totalWeight));

		holder.unitsHarvestedTextView.setText(String.valueOf(harvest.count));
	}

	@Override
	public int getItemCount()
	{
		return harvests.size();
	}
}
