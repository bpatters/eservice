package com.myl.eservice.common.config;

/**
 * Created by bpatterson on 3/12/15.
 */
public class ElasticIndexTypeConfiguration<DocType> {
    private String index;
    private String type;
    private DocType documentClass;


    public ElasticIndexTypeConfiguration(String index, String type, DocType documentClass) {
        this.setIndex(index);
        this.setType(type);
        this.setDocumentClass(documentClass);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DocType getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(DocType documentClass) {
        this.documentClass = documentClass;
    }
}
