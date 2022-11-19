package edu.msa.api.service;

import edu.msa.api.model.Client;
import edu.msa.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(final ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void create(final Client client) {
        clientRepository.save(client);
    }

    public Optional<Client> get(final String firebaseId) {
        return clientRepository.findByFirebaseId(firebaseId);
    }

    public Iterable<Client> getAll() {
        return clientRepository.findAll();
    }

    public void update(final String firebaseId, final Client client) {
//        final Client updatedClient = get(firebaseId);
//
//        updatedClient.setUsername(client.getUsername());
//        updatedClient.setFirstName(client.getFirstName());
//        updatedClient.setLastName(client.getLastName());
//        updatedClient.setEmail(client.getEmail());
//
//        clientRepository.save(updatedClient);
    }
}

