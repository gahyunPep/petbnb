package pet.eaters.ca.petbnb.pets;

import java.util.List;

public class Pet {
    private String id;
    private PetData data;

    public Pet(String id, String name, String info, String type, int size, List<String> images, String phone, String address, String zipCode, double latitude, double longitude, int age, int gender) {
        this.id = id;
        this.data = new PetData(name, info, type, size, images, phone, address, zipCode, latitude, longitude, age, gender);
    }

    public Pet(String id, PetData petData) {
        this.id = id;
        this.data = petData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return data.getName();
    }

    public void setName(String name) {
        data.setName(name);
    }

    public String getInfo() {
        return data.getInfo();
    }

    public void setInfo(String info) {
        data.setInfo(info);
    }

    public String getType() {
        return data.getType();
    }

    public void setType(String type) {
        data.setType(type);
    }

    public int getSize() {
        return data.getSize();
    }

    public void setSize(int size) {
        data.setSize(size);
    }

    public List<String> getImages() {
        return data.getImages();
    }

    public void setImages(List<String> images) {
        data.setImages(images);
    }

    public String getPhone() {
        return data.getPhone();
    }

    public void setPhone(String phone) {
        data.setPhone(phone);
    }

    public String getAddress() {
        return data.getAddress();
    }

    public void setAddress(String address) {
        data.setAddress(address);
    }

    public String getZipCode() {
        return data.getZipCode();
    }

    public void setZipCode(String zipCode) {
        data.setZipCode(zipCode);
    }

    public double getLatitude() {
        return data.getLatitude();
    }

    public void setLatitude(double latitude) {
        data.setLatitude(latitude);
    }

    public double getLongitude() {
        return data.getLongitude();
    }

    public void setLongitude(double longitude) {
        data.setLongitude(longitude);
    }

    public int getAge() {
        return data.getAge();
    }

    public void setAge(int age) {
        data.setAge(age);
    }

    public int getGender() {
        return data.getGender();
    }

    public void setGender(int gender) {
        data.setGender(gender);
    }
}
