package models;

public class Nurse extends User{

    public String division;

    public Nurse(int id, String name, String division) {
        super(id, name);
        this.division = division;
    }

    @Override
    public boolean hasReadAccessTo(Record record) {
        if (record.division.equalsIgnoreCase(division)) {
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
    public boolean hasDeleteAccessTo() {
        return false;
    }

    @Override
    public boolean hasCreateAccess() {
        return false;
    }
}
