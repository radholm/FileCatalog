package filecatalog.server.controller;

import filecatalog.server.model.User;
import filecatalog.server.model.MetaFile;
import filecatalog.server.model.ExceptionHandler;
import filecatalog.server.model.UserHandler;
import filecatalog.server.model.FileHandler;
import filecatalog.common.Credentials;
import filecatalog.common.FileCatalogClient;
import filecatalog.common.FileCatalogServer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

public class Controller extends UnicastRemoteObject implements FileCatalogServer{
    private final UserHandler userManager;
    private final FileHandler fileHandler;


    public Controller() throws RemoteException{
        super();
        userManager = new UserHandler();
        fileHandler = new FileHandler();
    }

    @Override
    public long login(Credentials credentials, FileCatalogClient client) throws RemoteException {
        long key = userManager.login(credentials);
        User user = userManager.getUser(key);
        if(user != null){
            fileHandler.addNotifyUser(user.getUserID(),client);
        }
        return key;
    }

    @Override
    public void logout(long key) throws RemoteException, ExceptionHandler {
        userManager.logout(key);
    }

    @Override
    public void register(Credentials credentials) throws ExceptionHandler {
        userManager.register(credentials);

    }

    @Override
    public void unregister(String username) throws ExceptionHandler {
        userManager.unregister(username);
    }

    @Override
    public void uploadFile(long key, String fileName, byte[] data, boolean publicFile, boolean publicWrite, boolean publicRead) throws IOException, ExceptionHandler {
        User user = userManager.getUser(key);
        if(user != null){
            fileHandler.uploadFile(user,fileName, data, publicFile, publicWrite, publicRead);
        }
        else{
            throw new ExceptionHandler("User is not logged in.");
        }
    }

    @Override
    public byte[] downloadFile(long key, String filename) throws IOException, ExceptionHandler {
        User user = userManager.getUser(key);
        if(user == null){
            throw new ExceptionHandler("User is not logged in.");
        }
        return fileHandler.openFile(user, filename);
    }

    @Override
    public String whoami(long key) throws RemoteException {
        return userManager.whoami(key);
    }

    @Override
    public void deleteFile(long key, String filename) throws IOException, ExceptionHandler {
        User user = userManager.getUser(key);
        if(user != null){
            fileHandler.deleteFile(user,filename);
        }
        else{
            throw new ExceptionHandler("User is not logged in.");
        }
    }

    @Override
    public void modifyFile(long key, String filename, boolean publicFile, boolean publicWrite, boolean publicRead) throws RemoteException, ExceptionHandler, FileNotFoundException {
        User user = userManager.getUser(key);
        if(user != null){
            fileHandler.modifyFile(user,filename,publicFile,publicWrite,publicRead);
        }
        else{
            throw new ExceptionHandler("User is not logged in.");
        }
    }

    @Override
    public Collection<MetaFile> listFiles(long key) throws RemoteException, ExceptionHandler {
        User user = userManager.getUser(key);
        if(user != null){
            return fileHandler.listFiles(user.getUserID());
        }
        else{
            throw new ExceptionHandler("User is not logged in.");
        }
    }

    @Override
    public void notifyFile(long key, String filename, boolean notify) throws RemoteException, ExceptionHandler, FileNotFoundException {
        User user = userManager.getUser(key);
        if(user != null){
            fileHandler.notifyFile(user,filename,notify);
        }
        else{
            throw new ExceptionHandler("User is not logged in.");
        }
    }
}
