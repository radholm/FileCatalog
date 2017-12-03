package filecatalog.common;

import filecatalog.server.model.MetaFile;
import filecatalog.server.model.ExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface FileCatalogServer extends Remote {
    String FILE_CATALOG_NAME_IN_REGISTRY = "filecatalog";

    long login(Credentials credentials, FileCatalogClient client) throws RemoteException, ExceptionHandler;

    void logout(long key) throws RemoteException, ExceptionHandler;

    void register(Credentials credentials) throws RemoteException, ExceptionHandler;

    void unregister(String username) throws RemoteException, ExceptionHandler;

    void uploadFile(long key, String fileName, byte[] data, boolean publicFile, boolean publicWrite, boolean publicRead) throws IOException, ExceptionHandler;

    byte[] downloadFile(long key, String filename) throws IOException, ExceptionHandler;

    String whoami(long key) throws RemoteException;

    void deleteFile(long key, String filename) throws IOException, ExceptionHandler;

    void modifyFile(long key, String filename, boolean publicFile, boolean publicWrite, boolean publicRead) throws RemoteException, ExceptionHandler, FileNotFoundException;

    Collection<MetaFile> listFiles(long key) throws RemoteException, ExceptionHandler;

    void notifyFile(long key, String filename, boolean notify) throws RemoteException, ExceptionHandler, FileNotFoundException;
}
