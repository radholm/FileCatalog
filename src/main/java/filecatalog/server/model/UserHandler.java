package filecatalog.server.model;

import filecatalog.common.Credentials;
import filecatalog.server.integration.FileServerDAO;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserHandler {
    private final Map<Long, User> usersLoggedIn = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, Long> userIDtoKey = Collections.synchronizedMap(new HashMap<>());
    private final Random idGenerator = new Random();
    private FileServerDAO fileServerDAO;


    public UserHandler(){
        fileServerDAO = FileServerDAO.getInstance();
    }

    public void register(Credentials credentials) throws ExceptionHandler {
        User user = fileServerDAO.findUserByName(credentials.getUsername());
        if(user == null){
            fileServerDAO.register(new User(credentials.getUsername(),credentials.getPassword()));
        }
        else{
            throw new ExceptionHandler("User " + credentials.getUsername() + " is already registered!");
        }
    }

    public void unregister(String username) throws ExceptionHandler {
        fileServerDAO.unregister(username);
    }

    public long login(Credentials credentials){
        User user =  fileServerDAO.findUserByName(credentials.getUsername());
        if(user != null){
            if(user.getPassword().equals(credentials.getPassword())){
                long id = idGenerator.nextLong();
                if(userIDtoKey.containsKey(user.getUserID())){
                    long tmp = userIDtoKey.get(user.getUserID());
                    usersLoggedIn.remove(tmp); //Remove old key
                    userIDtoKey.put(user.getUserID(), id);
                    usersLoggedIn.put(id, user);
                }
                else{
                    usersLoggedIn.put(id,user);
                    userIDtoKey.put(user.getUserID(),id);
                }
                return id;
            }
        }
        return -1;
    }

    public void logout(long key) throws ExceptionHandler {
        User user = usersLoggedIn.remove(key);
        if(user == null){
            throw new ExceptionHandler("User is not logged in.");
        }
        else{
            userIDtoKey.remove(user.getUserID());
        }
    }

    public String whoami(long key){
        User user = usersLoggedIn.get(key);
        if(user != null){
            return user.getUsername();
        }
        return null;
    }

    public User getUser(long key){
        return usersLoggedIn.get(key);
    }
}
