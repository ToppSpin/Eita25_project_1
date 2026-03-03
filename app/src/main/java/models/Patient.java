package models;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Patient extends User {

    List<Doctor> doctors;
    Map<String, Record> records;

    public Patient(int id, String name) {
        super(id, name);
        doctors = new ArrayList<>();
        records = new HashMap<>();
    }

    public void addRecord(Record rec, String division) {
        records.put(division, rec);
    }

    @Override
    public boolean hasReadAccessTo(Record record) {
        if (record.patient_id == id) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasWriteAccessTo(Record record) {
        return false;
    }

    @Override
    public boolean hasDeleteAccessTo() {
        return false;
    }

    @Override
    public boolean hasCreateAccess() {
        return false;
    }
}
