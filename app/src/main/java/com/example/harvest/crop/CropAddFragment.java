package com.example.harvest.crop;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.harvest.R;

public class CropAddFragment extends DialogFragment
{
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_crop_add, container, false);
		return view;
	}

	@Override
	public void onResume()
	{

		Dialog dialog = getDialog();
		Window window = null;

		if (dialog != null) {
			window = dialog.getWindow();
		}
		if (window != null) {
			getDialog().getWindow().setLayout(1200, 1500);
		}

		super.onResume();
	}
}