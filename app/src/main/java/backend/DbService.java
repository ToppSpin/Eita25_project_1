package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import models.User;
import models.Doctor;
import models.Government;
import models.Nurse;
import models.Patient;
import models.Record;
import models.Result;

public class DbService {
    
    private static DbService singelton = null;


    List<String> divisions = new ArrayList<>();
    Set<Record> records;
    HashMap<String, User> Users;
    HashMap<String, String> treats;
    int id = 0;
    

    private DbService() {
        records = new HashSet<>();
        Users = new HashMap<>();
        treats = new HashMap<>();
        addDoctor("Alice", "Radeology");
        addNurse("Bob", "Radeology");
        addPatient("Charlie");
        addDoctor("David", "Kids");
        addGov("Eva");
        divisions.add("Radeology");
        divisions.add("Kids");
        treats.put("Alice", "Charlie");
        treats.put("David", "Charlie");
    }

    public static DbService getInstance() {
        if (singelton == null) {
            return new DbService();
        }
        return singelton;
    }

    public synchronized Optional<User> getAccount(String name) {
        return Optional.ofNullable(Users.get(name));
    }

    public Result Create(User user, String name, String nurse) {
        String result;
        if (!user.hasCreateAccess()) {
            result = "Access Denied";

        } else if (Users
                    .values()
                    .stream()
                    .filter(u -> u.getClass().equals(Nurse.class))
                    .noneMatch(u -> u.name.equalsIgnoreCase(nurse))) {
            result = "Nurse name not present in database";

        } else if (Users
                    .values()
                    .stream()
                    .filter(u -> u.getClass().equals(Patient.class))
                    .noneMatch(u -> u.name.equalsIgnoreCase(name))) {
            result = "Patient name was not present in database";

        } else if (records
                    .stream()
                    .anyMatch(rec -> rec.name.equalsIgnoreCase(name) && rec.division.equalsIgnoreCase(((Doctor)user).division))) {
            result = "Patient already has a record in your division";

        } else if (treats
                    .entrySet()
                    .stream()
                    .noneMatch(e -> e.getKey().equalsIgnoreCase(user.name) && 
                                    e.getValue().equalsIgnoreCase(name))
                ) {
            result = "Docter not treating this patient";
        } 
        else {
            records.add(new Record(name, Users.get(name).id, user.id, Users.get(nurse).id, ((Doctor)user).division));
            return new Result(true, "New record created");

        }
        return new Result(false, result);
    }

    public Result Delete(User user, String name, String division) {
        String result;
        if (!user.hasDeleteAccessTo()) {
            result = "Access denied";
        } else if (!records.removeIf(rec -> rec.name.equalsIgnoreCase(name) && rec.division.equalsIgnoreCase(division))) {
            result = "No such record";
        } else {
            return new Result(true, "Record has been deleted");
        }
        return new Result(false, result);
    }

    public Result Read(User user, String name, String division) {
        String result;
        List<Record> recordList = records.stream().filter(rec -> rec.name.equalsIgnoreCase(name) && rec.division.equalsIgnoreCase(division)).toList();
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
        List<Record> recordList = records
            .stream()
            .filter(rec -> rec.name.equalsIgnoreCase(name) && rec.division.equalsIgnoreCase(division))
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

        records
            .stream()
            .filter(rec -> user.hasReadAccessTo(rec))
            .forEach(rec -> System.out.println(rec));

        return records
            .stream()
            .filter(rec -> user.hasReadAccessTo(rec))
            .toList();
    }

    public List<User> getPatients(User user) {
        return Users.values().stream().filter(u -> u.getClass().equals(Patient.class)).toList();
    }

    private void addDoctor(String name, String division) {
        Users.put(name, new Doctor(getId(), name, division));
    }

    private void addNurse(String name, String division) {
        Users.put(name, new Nurse(getId(), name, division));
    }

    private void addPatient(String name) {
        Users.put(name, new Patient(getId(), name));
    }

    private void addGov(String name) {
        Users.put(name, new Government(getId(), name));
    }

    public int getId() {
        return id++;
    }
}
