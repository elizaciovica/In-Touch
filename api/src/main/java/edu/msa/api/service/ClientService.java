package edu.msa.api.service;

import edu.msa.api.model.Client;
import edu.msa.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Client get(final Integer id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    public Iterable<Client> getAll() {
        return clientRepository.findAll();
    }

    public void update(final Integer id, final Client client) {
        final Client updatedClient = get(id);

        updatedClient.setUsername(client.getUsername());
        updatedClient.setFirstName(client.getFirstName());
        updatedClient.setLastName(client.getLastName());
        updatedClient.setEmail(client.getEmail());

        clientRepository.save(updatedClient);
    }
}

