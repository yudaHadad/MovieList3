package com.example.yuda.movielist;

/**
 * Created by yuda on 06/02/2017.
 */

public class myMovie
{
    String name;
    String descreption;
    String url;
    String poster;

    //defolte c'tor
    public myMovie()
    {

    }

    //c'tor
    public myMovie(String name, String descreption, String Url, String poster)
    {
        this.name = name;
        this.descreption = descreption;
        this.url=Url;
        this.poster = poster;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
