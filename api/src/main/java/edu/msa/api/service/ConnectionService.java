package edu.msa.api.service;

import edu.msa.api.model.Client;
import edu.msa.api.model.Connection;
import edu.msa.api.model.ConnectionStatus;
import edu.msa.api.repository.ClientRepository;
import edu.msa.api.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void create(final String receiverId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String senderId = authentication.getName();

        final Client sender = clientRepository.findByFirebaseId(senderId)
                                              .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        final Client receiver = clientRepository.findByFirebaseId(receiverId)
                                                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        final Optional<Connection> connection = connectionRepository.findBySenderIdAndReceiverId(sender, receiver);
        if (connection.isPresent()) {
            throw new IllegalArgumentException("Connection already exists between " + senderId + " and " + receiverId);
        }

        final Optional<Connection> reversedConnection = connectionRepository.findBySenderIdAndReceiverId(receiver, sender);
        if (reversedConnection.isPresent()) {
            throw new IllegalArgumentException("Connection already exists between " + senderId + " and " + receiverId);
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

    public Iterable<Connection> getAll() {
        return connectionRepository.findAll();
    }

    public void update(final Integer id, final Connection connection) {
        final Connection updatedConnection = get(id);

        updatedConnection.setSenderId(connection.getSenderId());

        connectionRepository.save(updatedConnection);
    }
}
