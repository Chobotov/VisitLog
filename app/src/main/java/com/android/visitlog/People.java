package com.android.visitlog;

public class People {
    private int Id;
    private String Name;

    public People(int Id, String Name){
        this.Id = Id;
        this.Name = Name;

    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }
}
