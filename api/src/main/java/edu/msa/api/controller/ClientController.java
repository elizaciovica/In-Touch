package edu.msa.api.controller;

import edu.msa.api.model.Client;
import edu.msa.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/users")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(final ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody final Client client) {
        clientService.create(client);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{firebaseId}")
    public ResponseEntity<Client> getClient(@PathVariable final String firebaseId) {
        return clientService.get(firebaseId)
                            .map(client -> ResponseEntity.ok().body(client))
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Client> getAllClients() {
        return StreamSupport.stream(clientService.getAll().spliterator(), false)
                            .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClients(@PathVariable final String firebaseId, @RequestBody Client client) {
        clientService.update(firebaseId, client);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
