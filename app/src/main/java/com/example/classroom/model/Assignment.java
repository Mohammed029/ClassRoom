package com.example.classroom.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Assignment {
    private String docId;
    private String postBody;
    @ServerTimestamp
    private Date postTime;
    private String userTId;
    private String userTName;
    private List<String> studentList;

    public Assignment() {
    }

    public Assignment(String docId, String postBody, Date postTime, String userTId, String userTName,
                      List<String> studentList) {
        this.docId = docId;
        this.postBody = postBody;
        this.postTime = postTime;
        this.userTId = userTId;
        this.userTName = userTName;
        this.studentList=studentList ;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public String getUserTId() {
        return userTId;
    }

    public void setUserTId(String userTId) {
        this.userTId = userTId;
    }

    public String getUserTName() {
        return userTName;
    }

    public void setUserTName(String userTName) {
        this.userTName = userTName;
    }

    public List<String> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<String> studentList) {
        this.studentList = studentList;
    }
}

