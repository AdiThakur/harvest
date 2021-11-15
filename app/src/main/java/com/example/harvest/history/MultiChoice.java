package com.example.harvest.history;

import android.app.AlertDialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MultiChoice<T>
{
	private List<T> options;
	private String[] stringOptions;
	private boolean[] selectedOptionsMap;
	private List<T> selectedOptions;

	private Subject<List<T>, List<T>> selectedSubject;
	public Observable<List<T>> selected$;

	public MultiChoice() {
		selectedSubject = PublishSubject.create();
		selected$ = selectedSubject.asObservable();
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
			selectedOptions.add(options.get(index));
		} else {
			selectedOptions.remove(options.get(index));
		}

		selectedOptionsMap[index] = isSelected;
	}

	private void set()
	{
		selectedSubject.onNext(selectedOptions);
	}

	public void show(Context context, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMultiChoiceItems(stringOptions, selectedOptionsMap, (d, i, b) -> select(i, b));
		builder.setPositiveButton("Ok", (d, i) -> set());

		builder.show();
	}
}
