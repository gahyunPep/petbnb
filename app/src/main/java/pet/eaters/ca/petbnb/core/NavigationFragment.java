package pet.eaters.ca.petbnb.core;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import pet.eaters.ca.petbnb.R;

public class NavigationFragment extends Fragment {
    protected NavigationActivity navigationActivity;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationActivity.initWith(view.<Toolbar>findViewById(R.id.toolbar));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigationActivity = (NavigationActivity) context;
    }

    public interface NavigationActivity {
        void initWith(Toolbar toolbar);
    }

}
