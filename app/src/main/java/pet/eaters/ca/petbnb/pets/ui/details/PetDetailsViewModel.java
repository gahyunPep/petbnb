package pet.eaters.ca.petbnb.pets.ui.details;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import pet.eaters.ca.petbnb.PetBnbApplication;
import pet.eaters.ca.petbnb.core.QRCodeGenAndReader;
import pet.eaters.ca.petbnb.core.Result;
import pet.eaters.ca.petbnb.core.ui.Event;
import pet.eaters.ca.petbnb.pets.data.Pet;
import pet.eaters.ca.petbnb.pets.data.PetsRepository;

public class PetDetailsViewModel extends ViewModel {
    private PetsRepository repository = PetBnbApplication.petsRepository;
    private String petId;

    private LiveData<Result<Pet>> petResult;
    private MutableLiveData<Bitmap> qrCode = new MutableLiveData<>();

    private MutableLiveData<Event<String>> phoneCall = new MutableLiveData<>();
    private MutableLiveData<Event<Message>> messageSend = new MutableLiveData<>();

    public PetDetailsViewModel(String petId) {
        petResult = repository.get(petId);

        this.petId = petId;
    }

    public LiveData<Result<Pet>> getPet() {
        return petResult;
    }

    public LiveData<Bitmap> getQrCode(int size) {
        generateQRCode(petId, new QrCodeGeneration(qrCode, size));
        return qrCode;
    }

    public LiveData<Event<String>> getPhoneCall() {
        return phoneCall;
    }

    public LiveData<Event<Message>> getMessageSend() {
        return messageSend;
    }

    public void onCallClicked() {
        String phoneNumber = getPhoneNumber();
        if (phoneNumber == null) return;

        phoneCall.setValue(new Event<>("tel:" + phoneNumber));
    }

    public void onMessageClicked() {
        Pet pet = getPetSafe();
        if (pet == null) return;

        String phoneNumber = pet.getPhone();
        String message = "Hey, I saw " + pet.getName() + " at PetBnb, when I can help you?";

        messageSend.setValue(new Event<>(new Message(phoneNumber, message)));
    }

    private String getPhoneNumber() {
        try {
            return getPetSafe().getPhone();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private Pet getPetSafe() {
        try {
            return petResult.getValue().getData();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void generateQRCode(String petId, QrCodeGeneration qrCodeGeneration) {
        qrCodeGeneration.execute(petId);
    }

    private static class QrCodeGeneration extends AsyncTask<String, Void, Bitmap> {
        private MutableLiveData<Bitmap> qrCode;
        private int qrCodeSize;

        public QrCodeGeneration(MutableLiveData<Bitmap> qrCode, int qrCodeSize) {
            this.qrCode = qrCode;
            this.qrCodeSize = qrCodeSize;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return new QRCodeGenAndReader().generateQRCode(strings[0], qrCodeSize, qrCodeSize);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            qrCode.setValue(bitmap);
        }
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final String petId;

        public Factory(String petId) {
            this.petId = petId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(PetDetailsViewModel.class)) {
                //noinspection unchecked
                return (T) new PetDetailsViewModel(petId);
            } else {
                throw new IllegalArgumentException(
                        "Class "+ modelClass.getName() + "is not supported in this factory."
                );
            }
        }
    }

    public static class Message {
        public final String phone;
        public final String message;

        public Message(String phone, String message) {
            this.phone = phone;
            this.message = message;
        }
    }
}
