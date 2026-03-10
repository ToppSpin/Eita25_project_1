package models;

import java.util.Map;

public class Nurse extends User{

    public String division;

    public Nurse(int id, String name, String division) {
        super(id, name);
        this.division = division;
    }

    @Override
    public boolean hasReadAccessTo(Record record) {
        if (record.division.equalsIgnoreCase(division) || record.nurse_id == id) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasWriteAccessTo(Record record) {
        if (record.nurse_id == id) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasDeleteAccess() {
        return false;
    }

    @Override
    public boolean hasCreateAccess(Map<String, String> treats, String patient) {
        return false;
    }
}
