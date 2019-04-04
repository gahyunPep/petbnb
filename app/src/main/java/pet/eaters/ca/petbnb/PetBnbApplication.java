package pet.eaters.ca.petbnb;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

public class PetBnbApplication extends MultiDexApplication {
    public static PetsRepository petsRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        petsRepository = new PetsRepository();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
