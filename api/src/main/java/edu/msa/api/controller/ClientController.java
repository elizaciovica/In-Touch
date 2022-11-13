package edu.msa.api.controller;

import com.google.firebase.auth.FirebaseAuthException;
import edu.msa.api.model.Client;
import edu.msa.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public Client getClient(@PathVariable final Integer id) {
        return clientService.get(id);
    }

    @GetMapping
    public List<Client> getAllClients() {
        return StreamSupport.stream(clientService.getAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClients(@PathVariable final Integer id, @RequestBody Client client) {
        clientService.update(id, client);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("test")
    public String test() throws FirebaseAuthException {
        return "Success!";
    }
}
