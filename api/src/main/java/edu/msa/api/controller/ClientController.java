package edu.msa.api.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import edu.msa.api.model.Client;
import edu.msa.api.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
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
    public String test(Principal principal) throws FirebaseAuthException, IOException {
        String finalToken = "Token not found";
        if (RequestContextHolder.getRequestAttributes() != null) {
            finalToken =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                            .getHeader("Authorization");
        }

        //TODO: add the file in resources and copy the local path here
        FileInputStream serviceAccount =
                new FileInputStream("C:\\Users\\eliza\\StudioProjects\\In-Touch\\api\\src\\main\\resources\\intouch-c623b-firebase-adminsdk-1y17n-cbd7da8104.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(finalToken);
        String uid = decodedToken.getUid();


        System.out.println(uid);
        System.out.println(principal.getName());
        return finalToken;
    }
}
