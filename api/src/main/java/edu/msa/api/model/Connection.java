package edu.msa.api.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.SequenceGenerator;

@Entity(name = "Connection")
public class Connection extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "connection_sequence_generator")
    @SequenceGenerator(
            name = "connection_sequence_generator",
            sequenceName = "connection_sequence",
            allocationSize = 1
    )
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client senderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client receiverId;

    @Column(name = "connection_status", nullable = false)
    private Integer connectionStatus;

    @PostPersist
    public void afterSave() {
        System.out.println("Saved the Connection " + getId());
    }

    public Connection() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getSenderId() {
        return senderId;
    }

    public void setSenderId(Client senderId) {
        this.senderId = senderId;
    }

    public Client getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Client receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(Integer connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
}
