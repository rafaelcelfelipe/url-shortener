package com.urlshortener.statistics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "link_accesses")
public class LinkAccess {

    @Id
    private String code;

    @Column(name = "access_count", nullable = false)
    private long accessCount;

    protected LinkAccess() {
    }

    public LinkAccess(String code, long accessCount) {
        this.code = code;
        this.accessCount = accessCount;
    }

    public String getCode() {
        return code;
    }

    public long getAccessCount() {
        return accessCount;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAccessCount(long accessCount) {
        this.accessCount = accessCount;
    }
}