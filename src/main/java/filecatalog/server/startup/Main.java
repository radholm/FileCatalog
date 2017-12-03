package filecatalog.server.startup;

import filecatalog.server.controller.Controller;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String args[]){
        try{
            System.out.println("Starting Server...");
            new Main().startRegistry();
            System.out.println("Rebinding Server...");
            Naming.rebind(Controller.FILE_CATALOG_NAME_IN_REGISTRY, new Controller());
            System.out.println("Server Started.");
        }
        catch(RemoteException | MalformedURLException e) {
            System.out.println("Could not start file catalog server.");
        }
    }

    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}
