package com.example.gsmakkar1.kitaab;

public class Book {
    private String title;
    private String link;

    Book(){
    }

    Book(String t, String l){
        title = t;
        link = l;
    }

    public String getTitle(){
        return title;
    }

    public String getLink(){
        return link;
    }

}
