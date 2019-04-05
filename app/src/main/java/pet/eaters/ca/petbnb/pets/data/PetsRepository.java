package pet.eaters.ca.petbnb.pets.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import pet.eaters.ca.petbnb.core.Result;

public class PetsRepository extends AbstractRepository implements IPetsRepository {
    //Fake object for test purposes
    public static final PetData MOCK_PET = new PetData("Brian", "Family dog", "Dog", 1,new ArrayList<String>(),
            "+17789179146", "704 Royal Street, New Westminster, BC", "V3L 2T6", 49.203664, -122.912863,
            26, 1, "someone");


    private static final String PETS_COLLECTION ="pets";
    private PhotoStorage photoStorage = new PhotoStorage();

    public PetsRepository() {
        super(PETS_COLLECTION);
    }

    @Override
    public LiveData<Result<Void>> post(final PetData pet) {
        final DocumentReference createdPet = collection.document();
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
        return executeTask(collection.document(petId).set(petData));
    }

    @Override
    public LiveData<Result<Void>> delete(String petId) {
        return executeTask(collection.document(petId).delete());
    }

    @Override
    public LiveData<Result<Pet>> get(String petId) {
        return executeTask(collection.document(petId).get(), petMapper());
    }

    @Override
    public LiveData<Result<List<Pet>>> getPets() {
        return executeTask(collection.get(), petListMapper());
    }

    @Override
    public void getPets(Callback<List<Pet>> callback) {
        executeTask(collection.get(), petListMapper(), callback);
    }

    private AbstractRepository.Mapper<QuerySnapshot, List<Pet>> petListMapper() {
        return new AbstractRepository.Mapper<QuerySnapshot, List<Pet>>() {
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

    private AbstractRepository.Mapper<DocumentSnapshot, Pet> petMapper() {
        return new AbstractRepository.Mapper<DocumentSnapshot, Pet>() {
            @Override
            public Pet map(DocumentSnapshot document) {
                return petFromDocument(document);
            }
        };
    }

    private Pet petFromDocument(DocumentSnapshot document) {
        return new Pet(document.getId(), document.toObject(PetData.class));
    }
}
