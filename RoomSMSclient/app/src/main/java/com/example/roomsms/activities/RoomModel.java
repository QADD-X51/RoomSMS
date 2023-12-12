package com.example.roomsms.activities;

public class RoomModel {
    private int roomId;
    private String roomName;
    private String roomOwnerName;

    public RoomModel()
    {
        this.roomId = 0;
        this.roomOwnerName = "None";
        this.roomName = "None";
    }

    public RoomModel(String name, String owner)
    {
        this.roomId = 0;
        this.roomOwnerName = owner;
        this.roomName = name;
    }

    public String getName(){
        return roomName;
    }

    public String getOwner(){
        return roomOwnerName;
    }

    public int getId() {
        return roomId;
    }
}
