package pet.eaters.ca.petbnb;

import pet.eaters.ca.petbnb.pets.data.PetsRepository;

public class PetRepositorySingleton {
    private PetsRepository repository = new PetsRepository();

    public PetsRepository getRepository() {
        return repository;
    }
}
