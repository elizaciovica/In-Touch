package edu.msa.api.controller;

import edu.msa.api.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    @Autowired
    public ConnectionController(final ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/{senderId}/connect/{receiverId}")
    public ResponseEntity<?> createConnection(@PathVariable("senderId") Integer senderId,
                                              @PathVariable("receiverId") Integer receiverId) {
        connectionService.create(senderId, receiverId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
