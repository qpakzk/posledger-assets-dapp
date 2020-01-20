package com.poscoict.assets.model;

import java.util.Date;
import java.util.List;

public class User {

    private String id;

    private Date createDate;

    private String name;
    
    private String password;

    /*public User() {

    }*/

    public User(String _id, String _passwd) {
        id = _id;
        password = _passwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreatedate(Date createDate) {
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}