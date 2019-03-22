package pet.eaters.ca.petbnb.pets.data;

import java.util.List;

public class ScanData {
    private String petID;
    private String userID;
    private List<Long> timestamps;

    public ScanData() {
    }

    public ScanData(String petID, String userID, List<Long> timestamps) {
        this.petID = petID;
        this.userID = userID;
        this.timestamps = timestamps;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }
}
