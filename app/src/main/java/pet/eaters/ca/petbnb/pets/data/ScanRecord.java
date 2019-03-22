package pet.eaters.ca.petbnb.pets.data;

public class ScanRecord {
    private String id;
    private ScanData scanData;

    public ScanRecord() {}

    public ScanRecord(String id, ScanData scanData) {
        this.id = id;
        this.scanData = scanData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
