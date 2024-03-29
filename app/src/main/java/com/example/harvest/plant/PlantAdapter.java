package com.example.harvest.plant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvest.R;

import java.util.List;

import common.Helper;
import common.ImageHelper;
import common.OnClickListener;
import data.models.Plant;

class PlantViewHolder extends RecyclerView.ViewHolder
{
	public View row;
	public ImageView plantImageView;
	public TextView plantNameTextView;
	public TextView plantUnitWeightTextView;
	public ImageView editPlantImageView;

	public PlantViewHolder(@NonNull View plantRowItem, OnClickListener listener)
	{
		super(plantRowItem);

		row = plantRowItem;
		plantImageView =  row.findViewById(R.id.plantRcvItem_plantImage);
		plantNameTextView =  row.findViewById(R.id.plantRcvItem_plantNameText);
		plantUnitWeightTextView = row.findViewById(R.id.plantRcvItem_plantUnitWeightText);
		editPlantImageView = row.findViewById(R.id.plantRcvItem_editPlantImageView);

		row.setOnClickListener(view -> listener.onClick(view, getAdapterPosition()));
		row.setOnLongClickListener(view -> {
			listener.onLongClick(view, getAdapterPosition());
			return true;
		});
		editPlantImageView.setOnClickListener((View v) -> {
			listener.onNestedButtonClick(getAdapterPosition());
		});
	}
}

public class PlantAdapter extends RecyclerView.Adapter<PlantViewHolder>
{
	private final List<Plant> plants;
	private final Context context;
	private final OnClickListener listener;

	public PlantAdapter(Context context, List<Plant> plants, OnClickListener listener)
	{
		this.context = context;
		this.plants = plants;
		this.listener = listener;
	}

	@NonNull
	@Override
	public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View plantRowItem = inflater.inflate(R.layout.plant_rcv_item, parent, false);

		return new PlantViewHolder(plantRowItem, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull PlantViewHolder holder, int position)
	{
		Plant plant = plants.get(position);

		holder.plantNameTextView.setText(plant.name);
		holder.plantImageView.setImageBitmap(ImageHelper.loadBitmapFromImage(context, plant.imageFileName));
		holder.plantUnitWeightTextView.setText(Helper.formatUnitWeight(plant.unitWeight, true));
	}

	@Override
	public int getItemCount() {
		return plants.size();
	}
}
