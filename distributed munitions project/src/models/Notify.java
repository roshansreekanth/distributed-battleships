package models;

import java.rmi.RemoteException;

public interface Notify extends Remote 
{
    public void joinMessage(String name) throws RemoteException;
    public void attackMessage(String name, String message) throws RemoteException;
    public void bombMessage(String name) throws RemoteException;
    
}
