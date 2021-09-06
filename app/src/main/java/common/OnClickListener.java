package common;

import android.view.View;

public interface OnClickListener
{
	public void onClick(View row, int position);
	public void onLongClick(View row, int position);
}

