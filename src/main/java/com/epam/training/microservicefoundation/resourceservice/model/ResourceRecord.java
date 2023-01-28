package com.epam.training.microservicefoundation.resourceservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ResourceRecord implements Serializable {
    private static final long serialVersionUID = 10_11_2022_11_13L;

    @JsonProperty("Id")
    private long id;

    public ResourceRecord() {}

    public ResourceRecord(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ResourceRecord{" +
                "id=" + id + '}';
    }
}
