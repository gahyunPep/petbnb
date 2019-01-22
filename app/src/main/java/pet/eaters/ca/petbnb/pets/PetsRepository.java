package pet.eaters.ca.petbnb.pets;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetsRepository {
    //Fake object for test purposes
    public static final FirebasePet MOCK_PET = new FirebasePet("Brian", "Family dog", "Dog", 1,new ArrayList<String>(),
            "+17789179146", "704 Royal Street, New Westminster, BC", "V3L 2T6", 49.203664, -122.912863,
            26, 1);


    private static final String TAG = "PetsRepository";
    private static final String PETS_COLLECTION ="pets";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void post(FirebasePet pet) {
        DocumentReference document = db.collection(PETS_COLLECTION).document();

        document.set(pet).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error writing document", e);
            }
        });
    }

    public void getPets() {
        db.collection(PETS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Pet> pets = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        pets.add(new Pet(document.getId(), document.toObject(FirebasePet.class)));
                    }
                    String s = "";
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

}
