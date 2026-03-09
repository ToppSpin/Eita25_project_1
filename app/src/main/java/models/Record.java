package models;

import java.util.ArrayList;
import java.util.List;

public class Record implements Comparable<Record> {
    public String name;
    public String division;

    int patient_id;
    int doctor_id;
    int nurse_id;

    List<String> data = new ArrayList<String>();

    public Record(String patientName, int patient, int doctorid, int nurseid, String division) {
        name = patientName;
        patient_id = patient;
        doctor_id = doctorid;
        nurse_id = nurseid;
        this.division = division;
    }

    @Override
    public int compareTo(Record other) {
        return this.name.compareTo(other.name);
    }

    public String Data() {
        return (String.join("\n", data));
    }

    @Override
    public String toString() {
        return name + " -- " + division;
    }

    public void addEntry(String text) {
        data.add(text);
    }
}
