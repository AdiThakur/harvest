package com.example.harvest.crop;

import com.example.harvest.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import common.Helper;
import data.models.Crop;

class CropViewHolder extends RecyclerView.ViewHolder
{
	public ImageView plantImageView;
	public TextView plantNameTextView;
	public TextView plantCountTextView;
	public TextView plantedDateTextView;

	CropViewHolder(@NonNull View plantRowItem)
	{
		super(plantRowItem);

		plantImageView =  plantRowItem.findViewById(R.id.cropRcvItem_cropImage);
		plantNameTextView =  plantRowItem.findViewById(R.id.cropRcvItem_cropNameText);
		plantCountTextView =  plantRowItem.findViewById(R.id.cropRcvItem_cropCountText);
		plantedDateTextView =  plantRowItem.findViewById(R.id.cropRcvItem_cropDatePlantedtext);
	}
}

public class CropAdapter extends RecyclerView.Adapter<CropViewHolder>
{
	private List<Crop> crops;

	public CropAdapter(List<Crop> crops)
	{
		this.crops = crops;
	}

	@NonNull
	@Override
	public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View plantRowItem = inflater.inflate(R.layout.add_crop_rcv_item, parent, false);

		return new CropViewHolder(plantRowItem);
	}

	@Override
	public void onBindViewHolder(@NonNull CropViewHolder holder, int position)
	{
		Crop crop = crops.get(position);
		// TODO: Set the image of the plant dynamically
		holder.plantImageView.setImageResource(R.drawable.tomato);
		holder.plantNameTextView.setText(crop.plant.name);
		holder.plantCountTextView.setText(String.valueOf(crop.numberOfPlants));
		holder.plantedDateTextView.setText(Helper.shortFormatOfDate(crop.datePlanted));
	}

	@Override
	public int getItemCount()
	{
		return crops.size();
	}
}
