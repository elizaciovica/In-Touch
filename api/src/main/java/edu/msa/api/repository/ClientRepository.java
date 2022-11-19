package edu.msa.api.repository;

import edu.msa.api.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Integer> {
    Optional<Client> findByFirebaseId(final String firebaseId);
}
