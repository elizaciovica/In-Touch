package edu.msa.api.model;

public enum ConnectionStatus {
    PENDING(1),
    ACCEPTED(2),
    REJECTED(3);

    private final Integer status;

    ConnectionStatus(final Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
