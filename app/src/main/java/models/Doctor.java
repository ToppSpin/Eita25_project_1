package models;

import java.util.Map;

public class Doctor extends User {

    public String division;

    public Doctor(int id, String name, String division) {
        super(id, name);
        this.division = division;
    }

    @Override
    public boolean hasReadAccessTo(Record record) {
        if (record.division.equalsIgnoreCase(division) || record.doctor_id == id) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasWriteAccessTo(Record record) {
        if (record.doctor_id == id) {
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
        return treats.values().stream().anyMatch(name -> name.equalsIgnoreCase(patient));
    }
}
