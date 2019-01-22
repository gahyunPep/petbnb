package pet.eaters.ca.petbnb.pets;

import java.util.List;

public class Pet extends FirebasePet {
    protected String id;

    public Pet(String id, String name, String info, String type, int size, List<String> images, String phone, String address, String zipCode, double latitude, double longitude, int age, int gender) {
        super(name, info, type, size, images, phone, address, zipCode, latitude, longitude, age, gender);
        this.id = id;
    }

    public Pet(String id, FirebasePet pet) {
        super(pet.name, pet.info, pet.type, pet.size, pet.images, pet.phone, pet.address, pet.zipCode, pet.latitude, pet.longitude, pet.age, pet.gender);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
