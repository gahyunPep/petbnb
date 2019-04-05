package pet.eaters.ca.petbnb.pets.data;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import pet.eaters.ca.petbnb.core.Result;

public class AbstractRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected CollectionReference collection;

    public AbstractRepository(String collectionName) {
        collection = db.collection(collectionName);

    }

    protected interface Mapper<F, T> {
        T map(F object);
    }

    protected <T> Mapper<T, T> transparentMapper() {
        return new Mapper<T, T>() {
            @Override
            public T map(T document) {
                return document;
            }
        };
    }

    protected <F, T> LiveData<Result<T>> executeTask(Task<F> task, Mapper<F, T> mapper) {
        final MutableLiveData<Result<T>> result = new MutableLiveData<>();
        task.addOnSuccessListener(successListener(result, mapper))
                .addOnFailureListener(failureListener(result));

        return result;
    }

    protected <F, T> void executeTask(Task<F> task, Mapper<F, T> mapper, IPetsRepository.Callback<T> callback) {
        task.addOnSuccessListener(successListener(callback, mapper))
                .addOnFailureListener(failureListener(callback));
    }

    protected <T> LiveData<Result<T>> executeTask(Task<T> task) {
        return executeTask(task, this.<T>transparentMapper());
    }

    protected <F, T> OnSuccessListener<F> successListener(final IPetsRepository.Callback<T> callback, final Mapper<F, T> mapper) {
        return new OnSuccessListener<F>() {
            @Override
            public void onSuccess(F o) {
                callback.onResult(Result.success(mapper.map(o)));
            }
        };
    }

    protected <T> OnFailureListener failureListener(final IPetsRepository.Callback<T> callback) {
        return new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                callback.onResult(Result.<T>failed(e));
            }
        };
    }

    protected <F, T> OnSuccessListener<F> successListener(final MutableLiveData<Result<T>> result, final Mapper<F, T> mapper) {
        return new OnSuccessListener<F>() {
            @Override
            public void onSuccess(F o) {
                result.postValue(Result.success(mapper.map(o)));
            }
        };
    }

    protected <T> OnFailureListener failureListener(final MutableLiveData<Result<T>> result) {
        return new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                result.postValue(Result.<T>failed(e));
            }
        };
    }
}
