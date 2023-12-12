package com.example.roomsms.activities;

public class RoomModel {
    private int roomId;
    private String roomName;
    private String roomOwnerName;

    public RoomModel()
    {
        this.roomId = 0;
        this.roomOwnerName = "";
        this.roomName = "";
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

    public void setName(String roomName) {
        this.roomName = roomName;
    }

    public void setOwner(String roomOwnerName) {
        this.roomOwnerName = roomOwnerName;
    }

    public void setId(int roomId) {
        this.roomId = roomId;
    }
}
