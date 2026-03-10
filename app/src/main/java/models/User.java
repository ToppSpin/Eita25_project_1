package models;

import java.util.Map;

public abstract class User {

    public int id;
    public String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract boolean hasReadAccessTo(Record record);

    public abstract boolean hasWriteAccessTo(Record record);

    public abstract boolean hasDeleteAccess();

    public abstract boolean hasCreateAccess(Map<String, String> treats, String patient);

    public String toString() {
        return name;
    }
}