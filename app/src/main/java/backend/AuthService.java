package backend;

import java.util.Optional;


import models.*;

public class AuthService {

    DbService db;
    
    public AuthService(String name) {
        db = DbService.getInstance();   
    }

    public static Optional<User> CNtoUser(String CN) {
        return DbService.getInstance().getAccount(CN);
    }
}