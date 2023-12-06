package com.example.roomsms.activities;

public class RoomModel {
    private String owner;
    private String name;

    public RoomModel()
    {
        this.owner = "None";
        this.name = "None";
    }

    public RoomModel(String name, String owner)
    {
        this.owner = owner;
        this.name = name;
    }

    public String GetName(){
        return name;
    }

    public String GetOwner(){
        return owner;
    }
}
