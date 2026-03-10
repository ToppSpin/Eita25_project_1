package models;

import java.util.Map;

public class Government extends User{

    public Government(int id, String name) {
        super(id, name);
    }

    @Override
    public boolean hasReadAccessTo(Record record) {
        return true;
    }

    @Override
    public boolean hasWriteAccessTo(Record record) {
        return false;
    }

    @Override
    public boolean hasDeleteAccess() {
        return true;
    }

    @Override
    public boolean hasCreateAccess(Map<String, String> treats, String patient) {
        return false;
    }
}
