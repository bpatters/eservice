package com.myl.eservice.common.elasticsearch;

import com.myl.eservice.model.impl.DocumentStatus;

import java.util.Optional;

/**
 * Created by bpatterson on 2/6/15.
 */
public interface IDocument {
    public class Properties {
        public static final String ID = "_id";
        public static final String STATUS = "status";
        public static final String CREATED_DATE = "createdDate";
        public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    }

    String getId();

    Object setId(String id);

    Object setStatus(DocumentStatus status);
    DocumentStatus getStatus();

    Optional<Long> getVersion();

    Object setVersion(Long version);

    Object setCreatedDate(Long createdDate);
    Long getCreatedDate();

    Object setLastModifiedDate(Long lastModifiedDate);
    Long getLastModifiedDate();
}
