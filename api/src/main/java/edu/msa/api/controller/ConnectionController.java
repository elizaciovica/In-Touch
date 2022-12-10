package edu.msa.api.controller;

import edu.msa.api.model.Connection;
import edu.msa.api.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    @Autowired
    public ConnectionController(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/{receiverEmail}")
    public ResponseEntity<?> createConnection(@PathVariable("receiverEmail") final String receiverEmail) {
        connectionService.create(receiverEmail);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{connectionStatus}")
    public List<Connection> getAllConnectionsForCaller(@PathVariable("connectionStatus") final Integer connectionStatus) {
        return StreamSupport.stream(connectionService.getAllConnectionsByConnectionStatus(connectionStatus).spliterator(), false)
                            .collect(Collectors.toList());
    }
}
