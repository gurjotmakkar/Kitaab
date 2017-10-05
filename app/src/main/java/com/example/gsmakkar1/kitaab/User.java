package com.example.gsmakkar1.kitaab;

public class User {
    private String user;
    private String email;

    User(){
    }

    User(String u, String e){

        user = u;
        email = e;
    }

    public String getUser(){ return user; }

    public String getEmail(){ return email; }
}
