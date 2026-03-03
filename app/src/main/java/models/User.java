package models;

public abstract class User {

    public int id;
    public String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract boolean hasReadAccessTo(Record record);

    public abstract boolean hasWriteAccessTo(Record record);

    public abstract boolean hasDeleteAccessTo();

    public abstract boolean hasCreateAccess();

    public String toString() {
        return name;
    }
}