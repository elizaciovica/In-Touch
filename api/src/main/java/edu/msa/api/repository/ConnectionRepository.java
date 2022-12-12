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

    @Query(value = "SELECT * " +
                   "FROM connection " +
                   "WHERE (sender_id = ?1 OR receiver_id = ?2) " +
                   "AND connection_status = ?3",
           nativeQuery = true)
    Iterable<Connection> findAllByReceiverIdOrSenderIdAndConnectionStatus(final Client sender,
                                                                          final Client receiver,
                                                                          final Integer connectionStatus);

    @Query(value = "SELECT * " +
                   "FROM connection " +
                   "WHERE receiver_id = ?1 " +
                   "AND connection_status = ?2",
           nativeQuery = true)
    Iterable<Connection> findAllByReceiverIdAndConnectionStatus(final Client receiver,
                                                                final Integer connectionStatus);

}
