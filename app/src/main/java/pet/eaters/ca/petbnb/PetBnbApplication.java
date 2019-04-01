package pet.eaters.ca.petbnb;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class PetBnbApplication extends MultiDexApplication {
    public static PetRepositorySingleton repositorySingleton;

    @Override
    public void onCreate() {
        super.onCreate();
        repositorySingleton = new PetRepositorySingleton();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
