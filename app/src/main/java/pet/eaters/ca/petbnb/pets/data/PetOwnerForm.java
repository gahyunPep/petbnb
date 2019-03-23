package pet.eaters.ca.petbnb.pets.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

public class PetOwnerForm implements Parcelable {
    public final String ownerName;
    public final String ownerAddress;
    public final String ownerCity;
    public final int ownerProvince;
    public final String ownerZipCode;
    public final String ownerEmail;
    public final String ownerPhone;

    public PetOwnerForm(String ownerName, String ownerAddress, String ownerCity, int ownerProvince, String ownerZipCode, String ownerEmail, String ownerPhone) {
        this.ownerName = ownerName;
        this.ownerAddress = ownerAddress;
        this.ownerCity = ownerCity;
        this.ownerProvince = ownerProvince;
        this.ownerZipCode = ownerZipCode;
        this.ownerEmail = ownerEmail;
        this.ownerPhone = ownerPhone;
    }

    protected PetOwnerForm(Parcel in) {
        ownerName = in.readString();
        ownerAddress = in.readString();
        ownerCity = in.readString();
        ownerProvince = in.readInt();
        ownerZipCode = in.readString();
        ownerEmail = in.readString();
        ownerPhone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerName);
        dest.writeString(ownerAddress);
        dest.writeString(ownerCity);
        dest.writeInt(ownerProvince);
        dest.writeString(ownerZipCode);
        dest.writeString(ownerEmail);
        dest.writeString(ownerPhone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PetOwnerForm> CREATOR = new Creator<PetOwnerForm>() {
        @Override
        public PetOwnerForm createFromParcel(Parcel in) {
            return new PetOwnerForm(in);
        }

        @Override
        public PetOwnerForm[] newArray(int size) {
            return new PetOwnerForm[size];
        }
    };
}
