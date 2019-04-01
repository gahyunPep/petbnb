package pet.eaters.ca.petbnb.core;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class FragmentUtils {

    public static Snackbar showError(@Nullable Fragment fragment, @Nullable Exception exception) {
        return showError(fragment, exception, 0, null);
    }

    public static Snackbar showError(@Nullable Fragment fragment, @Nullable Exception exception,
                                     @StringRes int action, @Nullable View.OnClickListener onClick) {
        if (fragment == null || exception == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        Snackbar snackbar = Snackbar.make(view, exception.getMessage(), Snackbar.LENGTH_INDEFINITE);

        if (action != 0 && onClick != null) {
            snackbar.setAction(action, onClick);
        }
        snackbar.show();

        return snackbar;
    }

}
