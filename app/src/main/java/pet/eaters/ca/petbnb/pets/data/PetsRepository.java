package pet.eaters.ca.petbnb.pets.data;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import pet.eaters.ca.petbnb.core.Result;

public class PetsRepository implements IPetsRepository {
    //Fake object for test purposes
    public static final PetData MOCK_PET = new PetData("Brian", "Family dog", "Dog", 1,new ArrayList<String>(),
            "+17789179146", "704 Royal Street, New Westminster, BC", "V3L 2T6", 49.203664, -122.912863,
            26, 1, "someone");


    private static final String TAG = "PetsRepository";
    private static final String PETS_COLLECTION ="pets";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference pets = db.collection(PETS_COLLECTION);
    private PhotoStorage photoStorage = new PhotoStorage();

    @Override
    public LiveData<Result<Void>> post(final PetData pet) {
        final DocumentReference createdPet = pets.document();
        return Transformations.switchMap(photoStorage.uploadFiles(pet.getImages(), createdPet.getId()),
                new Function<List<String>, LiveData<Result<Void>>>() {
            @Override
            public LiveData<Result<Void>> apply(List<String> input) {
                pet.setImages(input);
                return executeTask(createdPet.set(pet));
            }
        });
    }

    @Override
    public LiveData<Result<Void>> update(String petId, PetData petData) {
        return executeTask(pets.document(petId).set(petData));
    }

    @Override
    public LiveData<Result<Void>> delete(String petId) {
        return executeTask(pets.document(petId).delete());
    }

    @Override
    public LiveData<Result<Pet>> get(String petId) {
        return executeTask(pets.document(petId).get(), petMapper());
    }

    @Override
    public LiveData<Result<List<Pet>>> getPets() {
        return executeTask(pets.get(), petListMapper());
    }

    @Override
    public void getPets(Callback<List<Pet>> callback) {
        executeTask(pets.get(), petListMapper(), callback);
    }

    private interface Mapper<F, T> {
        T map(F object);
    }

    private Mapper<QuerySnapshot, List<Pet>> petListMapper() {
        return new Mapper<QuerySnapshot, List<Pet>>() {
            @Override
            public List<Pet> map(QuerySnapshot snapshot) {
                List<Pet> pets = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    pets.add(petFromDocument(document));
                }
                return pets;
            }
        };
    }

    private Mapper<DocumentSnapshot, Pet> petMapper() {
        return new Mapper<DocumentSnapshot, Pet>() {
            @Override
            public Pet map(DocumentSnapshot document) {
                return petFromDocument(document);
            }
        };
    }

    private Pet petFromDocument(DocumentSnapshot document) {
        return new Pet(document.getId(), document.toObject(PetData.class));
    }

    private <T> Mapper<T, T> transparentMapper() {
        return new Mapper<T, T>() {
            @Override
            public T map(T document) {
                return document;
            }
        };
    }

    private <F, T> LiveData<Result<T>> executeTask(Task<F> task, Mapper<F, T> mapper) {
        final MutableLiveData<Result<T>> result = new MutableLiveData<>();
        task.addOnSuccessListener(successListener(result, mapper))
                .addOnFailureListener(failureListener(result));

        return result;
    }

    private <F, T> void executeTask(Task<F> task, Mapper<F, T> mapper, Callback<T> callback) {
        task.addOnSuccessListener(successListener(callback, mapper))
                .addOnFailureListener(failureListener(callback));
    }

    private <T> LiveData<Result<T>> executeTask(Task<T> task) {
        return executeTask(task, this.<T>transparentMapper());
    }

    private <F, T> OnSuccessListener<F> successListener(final Callback<T> callback, final Mapper<F, T> mapper) {
        return new OnSuccessListener<F>() {
            @Override
            public void onSuccess(F o) {
                callback.onResult(Result.success(mapper.map(o)));
            }
        };
    }

    private <T> OnFailureListener failureListener(final Callback<T> callback) {
        return new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.<T>failed(e));
            }
        };
    }

    private <F, T> OnSuccessListener<F> successListener(final MutableLiveData<Result<T>> result, final Mapper<F, T> mapper) {
        return new OnSuccessListener<F>() {
            @Override
            public void onSuccess(F o) {
                result.postValue(Result.success(mapper.map(o)));
            }
        };
    }

    private <T> OnFailureListener failureListener(final MutableLiveData<Result<T>> result) {
        return new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                result.postValue(Result.<T>failed(e));
            }
        };
    }
}
