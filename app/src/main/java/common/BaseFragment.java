package common;

import android.app.Activity;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

public class BaseFragment extends Fragment
{
	protected void setTitle(String title)
	{
		Activity activity = getActivity();
		if (activity != null) {
			activity.setTitle(title);
		}
	}

	protected ViewModelStoreOwner getStoreOwner(@IdRes int id)
	{
		NavController controller = NavHostFragment.findNavController(this);
		return controller.getViewModelStoreOwner(id);
	}

	protected void navigateUp()
	{
		NavHostFragment.findNavController(this).navigateUp();
	}

	protected void navigateTo(@IdRes int source, @IdRes int action)
	{
		NavDestination currentDestination =
			NavHostFragment.findNavController(this).getCurrentDestination();
		if (currentDestination != null && currentDestination.getId() == source) {
			NavHostFragment.findNavController(this).navigate(action);
		}
	}
}
