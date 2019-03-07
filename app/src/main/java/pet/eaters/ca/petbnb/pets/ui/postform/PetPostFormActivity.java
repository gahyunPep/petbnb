package pet.eaters.ca.petbnb.pets.ui.postform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import pet.eaters.ca.petbnb.R;

import android.os.Bundle;

public class PetPostFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_post_form);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.formFragmentContainer, new PetFormFragment());
        fragmentTransaction.commit();
    }
}
