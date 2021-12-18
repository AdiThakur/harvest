package common;

import android.view.View;

public interface OnClickListener
{
	void onClick(View row, int rowIndex);
	void onLongClick(View row, int rowIndex);
	void onNestedButtonClick(int rowIndex);
}

