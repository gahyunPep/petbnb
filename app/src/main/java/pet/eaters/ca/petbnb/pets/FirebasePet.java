package pet.eaters.ca.petbnb.pets;

import java.util.List;

public class FirebasePet {
    protected String name;
    protected String info;
    protected String type;
    protected int size;
    protected List<String> images;
    protected String phone;
    protected String address;
    protected String zipCode;
    protected double latitude;
    protected double longitude;
    protected int age;
    protected int gender;

    //Empty constructor for firebase
    public FirebasePet() {}

    public FirebasePet(String name, String info, String type, int size, List<String> images, String phone, String address, String zipCode, double latitude, double longitude, int age, int gender) {
        this.name = name;
        this.info = info;
        this.type = type;
        this.size = size;
        this.images = images;
        this.phone = phone;
        this.address = address;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.age = age;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
