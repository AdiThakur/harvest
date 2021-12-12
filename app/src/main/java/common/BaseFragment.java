package common;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.example.harvest.R;
import com.google.android.material.snackbar.Snackbar;

public class BaseFragment extends Fragment
{
	public static long UI_DELAY_SHORT = 250;
	public static long UI_DELAY_LONG = 500;
	private AlertDialog loadingDialog;

	private ViewModelStoreOwner getStoreOwner(@IdRes int id)
	{
		NavController controller = NavHostFragment.findNavController(this);
		return controller.getViewModelStoreOwner(id);
	}

	protected void setTitle(String title)
	{
		Activity activity = getActivity();
		if (activity != null) {
			activity.setTitle(title);
		}
	}

	protected ViewModelProvider getProvider(@IdRes int id)
	{
		return (new ViewModelProvider(getStoreOwner(id)));
	}

	protected ViewModelProvider getProvider(ViewModelStoreOwner owner)
	{
		return (new ViewModelProvider(owner));
	}

	protected void navigateUp()
	{
		NavHostFragment.findNavController(this).navigateUp();
	}

	protected void navigateTo(@IdRes int source, @IdRes int action)
	{
		navigateTo(source, action, null);
	}

	protected void navigateTo(@IdRes int source, @IdRes int action, Bundle bundle)
	{
		NavDestination currentDestination =
				NavHostFragment.findNavController(this).getCurrentDestination();
		if (currentDestination != null && currentDestination.getId() == source) {
			NavHostFragment.findNavController(this).navigate(action, bundle);
		}
	}

	protected String getCaller()
	{
		try {
			NavController controller = NavHostFragment.findNavController(this);
			NavBackStackEntry entry = controller.getPreviousBackStackEntry();
			return entry.getDestination().getLabel().toString();
		} catch (NullPointerException e) {
			return "";
		}
	}

	protected void displayWarning(String error)
	{
		Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
	}

	protected void displayError(View root, Event<String> error, int duration)
	{
		if (!error.isFreshPiece()) {
			return;
		}

		Snackbar scoobySnacks = Snackbar.make(root, error.get(), duration);
		scoobySnacks.setAction("Ok", view -> {
			scoobySnacks.dismiss();
		});
		scoobySnacks.show();
	}

	protected void displayError(View root, Event<String> error)
	{
		displayError(root, error, Snackbar.LENGTH_INDEFINITE);
	}

	protected void showLoadingDialog(boolean show)
	{
		if (loadingDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
			builder.setView(R.layout.loading_dialog);
			loadingDialog = builder.create();
		}

		if (show) {
			loadingDialog.show();
		} else {
			loadingDialog.hide();
		}
	}
}
