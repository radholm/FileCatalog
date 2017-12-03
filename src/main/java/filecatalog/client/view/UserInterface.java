package filecatalog.client.view;

import filecatalog.common.ThreadSafeOutput;
import filecatalog.common.FileCatalogClient;
import filecatalog.common.FileChangeDTO;
import filecatalog.common.FileCatalogServer;
import filecatalog.common.Credentials;
import filecatalog.server.model.MetaFile;
import filecatalog.server.model.ExceptionHandler;
import filecatalog.client.model.FileManager;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Scanner;

public class UserInterface implements Runnable{
    FileCatalogServer fileServer;

    private boolean running = false;
    private final Scanner input = new Scanner(System.in);
    private long key = -1;
    private static final String PROMPT = "> ";
    private final ThreadSafeOutput consoleOut = new ThreadSafeOutput();
    private final FileManager fileManager = FileManager.getInstance();

    private ServerMessages serverOutput;

    public UserInterface(FileCatalogServer fileServer) throws RemoteException {
        this.fileServer = fileServer;
        this.serverOutput = new ServerMessages();
    }

    public void start(){
        if(running){
            return;
        }
        running = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        consoleOut.println("Type \"help\" for a list of commands.");
        while(running){
            try{
                consoleOut.print(PROMPT);
                String in = input.nextLine();
                if(in.equals("")){
                    consoleOut.println("");
                    continue;
                }
                CmdLine cmd = new CmdLine(in);
                String filename;
                switch(cmd.getCmd()){
                    case QUIT:
                        running = false;
                        serverOutput = null;
                        break;
                    case LOGIN:
                        if(cmd.getArgs().length >= 2){
                            Credentials c = new Credentials(cmd.getArgs()[0],cmd.getArgs()[1]);
                            long result = fileServer.login(c,serverOutput);
                            if(result == -1){
                                consoleOut.println("Incorrect credentials!");
                            }
                            else{
                                consoleOut.println("Successfully logged in!");
                                key = result;
                            }
                        }
                        else{
                            consoleOut.println("Not enough arguments. Provide both username and password.");
                        }
                        break;
                    case LOGOUT:
                        fileServer.logout(key);
                        consoleOut.println("Successfully logged out!");
                        break;
                    case REGISTER:
                        if(cmd.getArgs().length >= 2){
                            fileServer.register(new Credentials(cmd.getArgs()[0],cmd.getArgs()[1]));
                            consoleOut.println("Successfully registered account!");
                        }
                        else{
                            consoleOut.println("Not enough arguments. Provide both username and password.");
                        }
                        break;
                    case UNREGISTER:
                        if(cmd.getArgs().length >= 1){
                            fileServer.unregister(cmd.getArgs()[0]);
                            consoleOut.println("Successfully unregistered account!");
                        }
                        else{
                            consoleOut.println("Not enough arguments. Provide a username to unregister.");
                        }
                        break;
                    case WHOAMI:
                        String name = fileServer.whoami(key);
                        if(name != null){
                            consoleOut.println("I am " + name);
                        }
                        else{
                            consoleOut.println("A girl is no one.");
                        }
                        break;
                    case DELETE:
                        if(cmd.getArgs().length >= 1){
                            fileServer.deleteFile(key,cmd.getArgs()[0]);
                            consoleOut.println("Successfully deleted file!");
                        }
                        else{
                            consoleOut.println("Not enough arguments. Provide a filename to delete.");
                        }
                        break;
                    case LIST:
                        formatFilesOutput(fileServer.listFiles(key));
                        break;
                    case UPLOAD:
                        upload(cmd);
                        break;
                    case DOWNLOAD:
                        if(cmd.getArgs().length >= 1){
                            filename = cmd.getArgs()[0];
                                fileManager.writeFile(filename,fileServer.downloadFile(key, filename));
                                consoleOut.println("Successfully downloaded file!");
                        }
                        else{
                            consoleOut.println("Not enough arguments. Provide a valid filename to download.");
                        }
                        break;
                    case NOTIFY:
                        if(cmd.getArgs().length >= 2){
                            fileServer.notifyFile(key,cmd.getArgs()[0],Boolean.parseBoolean(cmd.getArgs()[1]));
                            consoleOut.println("Notify registered for file: " + cmd.getArgs()[0]);
                        }
                        else{
                            consoleOut.println("Not enough arguments. Notify <filename> <True/False>.");
                        }
                        break;
                    case MODIFY:
                        if(cmd.getArgs().length >= 4){
                            fileServer.modifyFile(key,cmd.getArgs()[0],Boolean.parseBoolean(cmd.getArgs()[1]),Boolean.parseBoolean(cmd.getArgs()[2]),Boolean.parseBoolean(cmd.getArgs()[3]));
                            consoleOut.println("Modification successful!");
                        }
                        else{
                            consoleOut.println("Not enough arguments. Modify <filename> <isPublicFile> <publicWrite> <publicRead>.");
                        }
                        break;
                    case HELP:
                        printMenu();
                        break;
                    case UNKNOWN:
                        consoleOut.println("Unknown command. Type help for a list of commands.");
                        break;
                }
            } catch (ExceptionHandler e) {
                consoleOut.println("This action is not permitted.");
            } catch(FileNotFoundException e) {
                consoleOut.println("Could not find file.");
            } catch(IOException e) {
                consoleOut.println("Operation failed.");
                consoleOut.println(e.getMessage());
            }
        }
    }

    private void printMenu() {
        consoleOut.println("Help - Display this menu\n"
                            + "Quit - Exit program\n"
                            + "Register <username> <password> - Create a new user\n"
                            + "Unregister <username> - Delete user\n"
                            + "Login <username> <password>\n"
                            + "Logout\n"
                            + "List - List files in remote/server directory\n"
                            + "Upload (<filename> (<isPublicFile> <publicWrite> <publicRead>))\n"
                            + "Download <filename>\n"
                            + "Delete <filename>\n"
                            + "Modify <filename> <isPublicFile> <publicWrite> <publicRead> - Change permission of file\n"
                            + "Notify <filename> <True/False> - Request to be informed if file is modified\n");
    }

    private void upload(CmdLine cmd) throws ExceptionHandler, IOException {
        String filename = "";
        boolean publicFile = false;
        boolean publicWrite = false;
        boolean publicRead = false;

        switch(cmd.getArgs().length){
            case 0:
                consoleOut.print("Name of the file? ");
                filename = input.nextLine();
                consoleOut.print("Should it be a public file? True/False: ");
                publicFile = input.nextBoolean();
                consoleOut.print("Public Write access? True/False: ");
                publicWrite = input.nextBoolean();
                consoleOut.print("Public Read access? True/False: ");
                publicRead = input.nextBoolean();
                break;
            case 1:
                filename = cmd.getArgs()[0];
                consoleOut.print("Should it be a public file? True/False: ");
                publicFile = input.nextBoolean();
                consoleOut.print("Public Write access? True/False: ");
                publicWrite = input.nextBoolean();
                consoleOut.print("Public Read access? True/False: ");
                publicRead = input.nextBoolean();
                break;
            case 4:
                filename = cmd.getArgs()[0];
                publicFile = Boolean.parseBoolean(cmd.getArgs()[1]);
                publicWrite = Boolean.parseBoolean(cmd.getArgs()[2]);
                publicRead = Boolean.parseBoolean(cmd.getArgs()[3]);
                break;
        }
        if(!filename.equals("")) {
            try {
                byte[] data = fileManager.readFile(filename);
                fileServer.uploadFile(key,filename,data,publicFile,publicWrite,publicRead);
                consoleOut.println("File uploaded successfully!");
            } catch(FileNotFoundException e) {
                consoleOut.println("File not found.");
            }
        }
        else {
            consoleOut.println("Invalid filename");
        }
    }

    private void formatFilesOutput(Collection<MetaFile> files) {
        if(files.isEmpty()) {
            consoleOut.println("No files stored in remote/server directory.");
        }
        for(MetaFile file : files) {
            consoleOut.println("Name: " + file.getName() + " Size: " + file.getSize() + " bytes," + " Public: " + file.isPublicFile() + ", Write: " + file.isPublicWrite() + ", Read: " + file.isPublicRead()
                    + ", Notify of change: " + file.isNotify() + ", Owner: " + file.getOwner().getUsername());
        }
    }

    private class ServerMessages extends UnicastRemoteObject implements FileCatalogClient {
        public ServerMessages() throws RemoteException {}

        @Override
        public void handleMsg(FileChangeDTO message) throws RemoteException {
            consoleOut.println("File: " + message.getFilename() + " was modified by: " + message.getModifiedByUser() + ". Action taken was: " + message.getModifiedAction());
            consoleOut.print(PROMPT);
        }
    }
}
