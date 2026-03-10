package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import models.Doctor;
import models.Government;
import models.Nurse;
import models.Patient;
import models.Record;
import models.Result;
import models.User;

public class DbService {

    private static DbService singleton = null;

    List<String> divisions = new ArrayList<>();
    Set<Record> records;
    HashMap<String, User> users;
    HashMap<String, String> treats;
    int id = 0;

    private DbService() {
        records = new HashSet<>();
        users = new HashMap<>();
        treats = new HashMap<>();

        divisions.add("Radiology");
        divisions.add("Surgery");

        addDoctor("Alice", "Radiology");
        addNurse("Bob", "Radiology");
        addPatient("Charlie");
        addDoctor("David", "Surgery");
        addGov("Eva");

        treats.put("Alice", "Charlie");
        treats.put("David", "Charlie");
    }


    public static DbService getInstance() {
        if (singleton == null) {
            return new DbService();
        }
        return singleton;
    }


    public synchronized Optional<User> getAccount(String name) {
        return Optional.ofNullable(users.get(name));
    }


    public Result Create(User user, String patient, String nurse) {
        String result;

        if (!user.hasCreateAccess(treats, patient)) {
            result = "Access Denied";
        }
        else if (
            users.values()
                .stream()
                .noneMatch(u -> u.getClass().equals(Nurse.class) && 
                                u.name.equalsIgnoreCase(nurse))
        ) {
            result = "Nurse not present in database";

        } 
        else if (
            users.values()
                .stream()
                .noneMatch(u -> u.getClass().equals(Patient.class) && 
                            u.name.equalsIgnoreCase(patient))
        ) {
            result = "Patient name was not present in database";

        } 
        else if (
            records
                .stream()
                .anyMatch(rec ->
                            rec.name.equalsIgnoreCase(patient) && 
                            rec.division.equalsIgnoreCase(((Doctor) user).division))
        ) {
            result = "Patient already has a record in your division";

        }  else {
            records.add(
                new Record(
                    patient,
                    users.get(patient).id,
                    user.id,
                    users.get(nurse).id,
                    ((Doctor) user).division
                )
            );
            return new Result(true, "New record created");
        }

        return new Result(false, result);
    }


    public Result Delete(User user, String name, String division) {
        if (!user.hasDeleteAccess()) {
            return new Result(false, "Access denied");
        } 
        
        if (!records.removeIf(rec ->
                                rec.name.equalsIgnoreCase(name) &&
                                rec.division.equalsIgnoreCase(division))) 
        {
            return new Result(true, "Record has been deleted");
        }
        return new Result(false, "no such record");
    }


    public Result Read(User user, String name, String division) {
        Optional<Record> record = records.stream()
            .filter(rec -> 
                        rec.name.equalsIgnoreCase(name) && 
                        rec.division.equalsIgnoreCase(division))
            .findFirst();

        if (record.isPresent() && user.hasReadAccessTo(record.get())) {
            return new Result(true, record.get().Data());
        }
        return new Result(false, "Access denied or no record here matches search");
    }


    public Result Write(User user, String name, String division, String text) {
        Optional<Record> record = records.stream()
            .filter(
                rec ->
                    rec.name.equalsIgnoreCase(name) && 
                    rec.division.equalsIgnoreCase(division)
            )
            .findFirst();

        if (record.isPresent() && user.hasWriteAccessTo(record.get())) {
            record.get().addEntry(text);
            return new Result(true, "Info has been written to patient record");
        } 
        return new Result(false, "Access denied or no record here matches search");
    }


    public List<Record> getAllRecords(User user) {
        records.stream()
            .filter(rec -> user.hasReadAccessTo(rec))
            .forEach(rec -> System.out.println(rec));

        return records.stream()
            .filter(rec -> user.hasReadAccessTo(rec))
            .toList();
    }


    public List<User> getPatients(User user) {
        return users.values()
            .stream()
            .filter(u -> u.getClass().equals(Patient.class))
            .toList();
    }

    private void addDoctor(String name, String division) {
        users.put(name, new Doctor(getId(), name, division));
    }

    private void addNurse(String name, String division) {
        users.put(name, new Nurse(getId(), name, division));
    }

    private void addPatient(String name) {
        users.put(name, new Patient(getId(), name));
    }

    private void addGov(String name) {
        users.put(name, new Government(getId(), name));
    }

    public int getId() {
        return id++;
    }
}