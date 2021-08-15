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

import data.models.Plant;

class PlantViewHolder extends RecyclerView.ViewHolder
{
	public ImageView plantImageView;
	public TextView plantNameTextView;
	public TextView plantUnitWeightTextView;

	public PlantViewHolder(@NonNull View plantRowItem)
	{
		super(plantRowItem);

		plantImageView =  plantRowItem.findViewById(R.id.plantRcvItem_plantImage);
		plantNameTextView =  plantRowItem.findViewById(R.id.plantRcvItem_plantNameText);
		plantUnitWeightTextView = plantRowItem.findViewById(R.id.plantRcvItem_plantUnitWeightText);
	}
}

public class PlantAdapter extends RecyclerView.Adapter<PlantViewHolder>
{
	private List<Plant> plants;

	public PlantAdapter(List<Plant> plants)
	{
		this.plants = plants;
	}

	@NonNull
	@Override
	public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View plantRowItem = inflater.inflate(R.layout.add_plant_rcv_item, parent, false);

		return new PlantViewHolder(plantRowItem);
	}

	@Override
	public void onBindViewHolder(@NonNull PlantViewHolder holder, int position)
	{
		Plant plant = plants.get(position);
		holder.plantNameTextView.setText(plant.name);
		holder.plantUnitWeightTextView.setText(String.valueOf(plant.unitWeight));
		holder.plantImageView.setImageURI(plant.imageUri);
	}

	@Override
	public int getItemCount()
	{
		return plants.size();
	}
}
