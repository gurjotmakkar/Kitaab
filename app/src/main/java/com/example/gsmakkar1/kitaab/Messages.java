package com.example.gsmakkar1.kitaab;

public class Messages {
    private String username;
    private String message;

    Messages(){}

    Messages(String u, String m){
        username = u;
        message = m;
    }

    public String getUsername(){ return username; }

    public String getMessage(){ return message; }

}
