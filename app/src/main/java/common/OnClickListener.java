package common;

import android.view.View;

public interface OnClickListener
{
	public void onClick(View row, int rowIndex);
	public void onLongClick(View row, int rowIndex);
	public void onNestedButtonClick(int rowIndex);
}

