package com.example.harvest.crop;

import common.OnClickListener;
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
	public View row;
	public ImageView cropImageView;
	public TextView cropNameTextView;
	public TextView cropCountTextView;
	public TextView cropPlantedDateTextView;

	CropViewHolder(@NonNull View cropRowItem, OnClickListener listener)
	{
		super(cropRowItem);

		row = cropRowItem;
		cropImageView =  row.findViewById(R.id.cropRcvItem_cropImage);
		cropNameTextView =  row.findViewById(R.id.cropRcvItem_cropNameText);
		cropCountTextView =  row.findViewById(R.id.cropRcvItem_cropCountText);
		cropPlantedDateTextView =  row.findViewById(R.id.cropRcvItem_cropDatePlantedtext);

		row.setOnClickListener(view -> listener.onClick(view, getAdapterPosition()));
		row.setOnLongClickListener(view -> {
			listener.onLongClick(view, getAdapterPosition());
			return true;
		});
	}
}

public class CropAdapter extends RecyclerView.Adapter<CropViewHolder>
{
	private final List<Crop> crops;
	private final Context context;
	private final OnClickListener listener;

	public CropAdapter(Context context, List<Crop> crops, OnClickListener listener)
	{
		this.context = context;
		this.crops = crops;
		this.listener = listener;
	}

	@NonNull
	@Override
	public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		View plantRowItem = inflater.inflate(R.layout.add_crop_rcv_item, parent, false);

		return new CropViewHolder(plantRowItem, listener);
	}

	@Override
	public void onBindViewHolder(@NonNull CropViewHolder holder, int position)
	{
		Crop crop = crops.get(position);
		holder.cropImageView.setImageBitmap(Helper.loadBitmapFromImage(context, crop.plant.imageFileName));
		holder.cropNameTextView.setText(crop.plant.name);
		holder.cropCountTextView.setText(String.valueOf(crop.numberOfPlants));
		holder.cropPlantedDateTextView.setText(Helper.shortFormatOfDate(crop.datePlanted));
	}

	@Override
	public int getItemCount()
	{
		return crops.size();
	}
}
