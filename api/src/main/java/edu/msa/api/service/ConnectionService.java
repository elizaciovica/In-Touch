package edu.msa.api.service;

import edu.msa.api.model.Client;
import edu.msa.api.model.Connection;
import edu.msa.api.model.ConnectionStatus;
import edu.msa.api.repository.ClientRepository;
import edu.msa.api.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ConnectionService(final ConnectionRepository connectionRepository,
                             final ClientRepository clientRepository) {
        this.connectionRepository = connectionRepository;
        this.clientRepository = clientRepository;
    }

    public void create(final String receiverEmail) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String senderId = authentication.getName();

        final Client sender = clientRepository.findByFirebaseId(senderId)
                                              .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        final Client receiver = clientRepository.findByEmail(receiverEmail)
                                                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        final Optional<Connection> connection = connectionRepository.findBySenderIdAndReceiverId(sender, receiver);
        if (connection.isPresent()) {
            throw new IllegalArgumentException("Connection already exists between " + senderId + " and " + receiverEmail);
        }

        final Optional<Connection> reversedConnection = connectionRepository.findBySenderIdAndReceiverId(receiver, sender);
        if (reversedConnection.isPresent()) {
            throw new IllegalArgumentException("Connection already exists between " + senderId + " and " + receiverEmail);
        }

        final Connection newConnection = new Connection();
        newConnection.setSenderId(sender);
        newConnection.setReceiverId(receiver);
        newConnection.setConnectionStatus(ConnectionStatus.PENDING.getStatus());

        connectionRepository.save(newConnection);
    }

    public Connection get(final Integer id) {
        return connectionRepository.findById(id)
                                   .orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    public Iterable<Connection> getAllConnectionsByConnectionStatus(final Integer connectionStatus) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String firebaseId = authentication.getName();

        final Client client = clientRepository.findByFirebaseId(firebaseId)
                                              .orElseThrow(() -> new IllegalArgumentException("Caller Client not found."));

        return connectionRepository.findAllByReceiverIdOrSenderIdAndConnectionStatus(client, client, connectionStatus);
    }

    public Iterable<Connection> getAllConnectionRequestsByConnectionStatus(final Integer connectionStatus) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String firebaseId = authentication.getName();

        final Client client = clientRepository.findByFirebaseId(firebaseId)
                                              .orElseThrow(() -> new IllegalArgumentException("Caller Client not found."));

        return connectionRepository.findAllByReceiverIdAndConnectionStatus(client, connectionStatus);
    }

    public void update(final Integer id, final Connection connection) {
        final Connection updatedConnection = get(id);

        updatedConnection.setSenderId(connection.getSenderId());

        connectionRepository.save(updatedConnection);
    }

    public ResponseEntity<?> changeConnectionStatusFromPendingToAccepted(final String senderId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String firebaseId = authentication.getName();

        final Client receiver = clientRepository.findByFirebaseId(firebaseId)
                                                .orElseThrow(() -> new IllegalArgumentException("Caller Client not found."));
        final Client sender = clientRepository.findByFirebaseId(senderId)
                                              .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        final Connection connection =
                connectionRepository.findByReceiverIdAndSenderId(receiver, sender)
                                    .orElseThrow(() -> new IllegalArgumentException("Connection not found."));

        connection.setConnectionStatus(ConnectionStatus.ACCEPTED.getStatus());
        connectionRepository.save(connection);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public ResponseEntity<?> deleteConnection(final String senderId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String firebaseId = authentication.getName();

        final Client receiver = clientRepository.findByFirebaseId(firebaseId)
                                                .orElseThrow(() -> new IllegalArgumentException("Caller Client not found."));
        final Client sender = clientRepository.findByFirebaseId(senderId)
                                              .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        final Connection connection =
                connectionRepository.findByReceiverIdAndSenderId(receiver, sender)
                                    .orElseThrow(() -> new IllegalArgumentException("Connection not found."));

        connectionRepository.delete(connection);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
