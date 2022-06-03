package com.example.classroom.model;

public class Answers {
    private String docId;
    private String uri;

    public Answers() {

    }
    public Answers(String docId, String uri) {
        this.docId = docId;
        this.uri = uri;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
