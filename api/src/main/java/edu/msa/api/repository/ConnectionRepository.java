package edu.msa.api.repository;

import edu.msa.api.model.Client;
import edu.msa.api.model.Connection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConnectionRepository extends CrudRepository<Connection, Integer> {

    Optional<Connection> findBySenderIdAndReceiverId(@Param(value = "sender_id") final Client sender,
                                                     @Param(value = "receiver_id") final Client receiver);

    Optional<Connection> findByReceiverIdAndSenderId(@Param(value = "sender_id") final Client receiver,
                                                     @Param(value = "receiver_id") final Client sender);
}
