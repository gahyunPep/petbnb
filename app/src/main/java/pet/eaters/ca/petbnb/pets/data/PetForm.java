package pet.eaters.ca.petbnb.pets.data;


import android.os.Parcel;
import android.os.Parcelable;

public class PetForm implements Parcelable {

    public final String petName;
    public final int petGender;
    public final int petType;
    public final int petAge;
    public final int petSize;
    public final String petDesc;

    public PetForm(String petName, int petGender, int petType, int petAge, int petSize, String petDesc) {
        this.petName = petName;
        this.petGender = petGender;
        this.petType = petType;
        this.petAge = petAge;
        this.petSize = petSize;
        this.petDesc = petDesc;
    }

    protected PetForm(Parcel in) {
        petName = in.readString();
        petGender = in.readInt();
        petType = in.readInt();
        petAge = in.readInt();
        petSize = in.readInt();
        petDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(petName);
        dest.writeInt(petGender);
        dest.writeInt(petType);
        dest.writeInt(petAge);
        dest.writeInt(petSize);
        dest.writeString(petDesc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PetForm> CREATOR = new Creator<PetForm>() {
        @Override
        public PetForm createFromParcel(Parcel in) {
            return new PetForm(in);
        }

        @Override
        public PetForm[] newArray(int size) {
            return new PetForm[size];
        }
    };
}
