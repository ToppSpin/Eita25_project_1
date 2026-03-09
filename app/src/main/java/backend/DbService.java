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


    public Result Create(User user, String name, String nurse) {
        String result;

        if (!user.hasCreateAccess()) {
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
                            u.name.equalsIgnoreCase(name))
        ) {
            result = "Patient name was not present in database";

        } 
        else if (
            records
                .stream()
                .anyMatch(rec ->
                            rec.name.equalsIgnoreCase(name) && 
                            rec.division.equalsIgnoreCase(((Doctor) user).division))
        ) {
            result = "Patient already has a record in your division";

        } 
        else if (
            treats.entrySet()
                .stream()
                .noneMatch(e -> e.getKey().equalsIgnoreCase(user.name) && e.getValue().equalsIgnoreCase(name))
        ) {
            result = "Docter not treating this patient";

        } else {
            records.add(
                new Record(
                    name,
                    users.get(name).id,
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
        String result;

        if (!user.hasDeleteAccessTo()) {
            result = "Access denied";

        } else if (
            !records.removeIf(
                rec ->
                    rec.name.equalsIgnoreCase(name)
                        && rec.division.equalsIgnoreCase(division)
            )
        ) {
            result = "No such record";

        } else {
            return new Result(true, "Record has been deleted");
        }

        return new Result(false, result);
    }


    public Result Read(User user, String name, String division) {
        String result;

        List<Record> recordList = 
        records
            .stream()
            .filter(rec -> 
                        rec.name.equalsIgnoreCase(name) && 
                        rec.division.equalsIgnoreCase(division))
            .toList();

        if (recordList.isEmpty()) {
            result = "patient has no record here";

        } else if (!user.hasReadAccessTo(recordList.get(0))) {
            result = "Access denied";

        } else {
            return new Result(true, recordList.get(0).Data());
        }

        return new Result(false, result);
    }


    public Result Write(User user, String name, String division, String text) {
        String result;

        List<Record> recordList = records.stream()
            .filter(
                rec ->
                    rec.name.equalsIgnoreCase(name) && 
                    rec.division.equalsIgnoreCase(division)
            )
            .toList();

        if (recordList.isEmpty()) {
            result = "no record here matches search";

        } else if (!user.hasWriteAccessTo(recordList.get(0))) {
            result = "Access denied";

        } else {
            recordList.get(0).addEntry(text);
            return new Result(true, "Info has been written to patient record");
        }

        return new Result(false, result);
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