package com.myl.eservice.common.elasticsearch.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myl.eservice.model.impl.DocumentStatus;
import com.myl.eservice.common.elasticsearch.IDocument;

import java.util.Optional;

/**
 * Created by bpatterson on 2/6/15.
 */
public abstract class AbstractElasticDocument implements IDocument {
    protected transient String id;
    private transient Optional<Long> version = Optional.empty();

    @JsonProperty(IDocument.Properties.STATUS)
    private DocumentStatus status = DocumentStatus.ACTIVE;

    @JsonProperty(IDocument.Properties.CREATED_DATE)
    private Long createdDate;
    @JsonProperty(IDocument.Properties.LAST_MODIFIED_DATE)
    private Long lastModifiedDate;

    @Override
    @JsonIgnore
    public String getId() {
        return id;
    }

    @Override
    public Object setId(final String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    @Override
    public Optional<Long> getVersion() {
        return this.version;
    }

    @Override
    public Object setVersion(Long version) {
        this.version = Optional.ofNullable(version);

        return this;
    }

    @Override
    public Long getCreatedDate() {
        return this.createdDate;
    }

    @Override
    public Object setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;

        return this;
    }

    @Override
    public Long getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    @Override
    public Object setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;

        return this;
    }

    @Override
    public DocumentStatus getStatus() {
        return status;
    }

    @Override
    public Object setStatus(DocumentStatus status) {
        this.status = status;

        return this;
    }
}
