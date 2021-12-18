package com.example.harvest.history;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class MultiChoice<T>
{
	private List<T> options;
	private String[] stringOptions;
	private boolean[] selectedOptionsMap;
	private List<T> selectedOptions;

	private Subject<List<T>> selectedSubject;
	public Observable<List<T>> selected$;

	public MultiChoice()
	{
		selectedSubject = PublishSubject.create();
		selected$ = selectedSubject.hide();
	}

	public void setOptions(List<T> newOptions)
	{
		options = newOptions;
		stringOptions = options.stream().map(Object::toString).toArray(String[]::new);
		selectedOptionsMap = new boolean[options.size()];
		selectedOptions = new ArrayList<>();
	}

	private void select(int index, boolean isSelected)
	{
		if (isSelected) {
			T optionToSelect = options.get(index);
			if (!selectedOptions.contains(optionToSelect)) {
				selectedOptions.add(optionToSelect);
			}
		} else {
			selectedOptions.remove(options.get(index));
		}

		selectedOptionsMap[index] = isSelected;
	}

	private void set()
	{
		selectedSubject.onNext(selectedOptions);
	}

	private void checkAll(AlertDialog dialog, boolean checked)
	{
		ListView list = dialog.getListView();
		for (int i = 0; i < list.getCount(); i++) {
			list.setItemChecked(i, checked);
			select(i, checked);
		}

		set();
	}

	public void show(Context context, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMultiChoiceItems(stringOptions, selectedOptionsMap, (d, i, b) -> select(i, b));
		builder.setPositiveButton("Ok", (d, i) -> set());
		builder.setNegativeButton(
			"Select All", (d, i) -> checkAll((AlertDialog) d, true)
		);
		builder.setNeutralButton(
			"Clear All", (d, i) -> checkAll((AlertDialog) d, false)
		);

		builder.show();
	}
}
