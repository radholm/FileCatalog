package filecatalog.client.startup;

import filecatalog.client.view.UserInterface;
import filecatalog.common.FileCatalogServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {

    public static void main(String args[]){
        try{
            FileCatalogServer server = (FileCatalogServer) Naming.lookup(FileCatalogServer.FILE_CATALOG_NAME_IN_REGISTRY);
            new UserInterface(server).start();
            System.out.println("Client Started.");
        }
        catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
