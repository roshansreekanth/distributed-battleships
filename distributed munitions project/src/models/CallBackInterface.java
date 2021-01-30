package models;

import java.rmi.RemoteException;
import models.bombs.Bomb;

public interface CallBackInterface 
{
    public void addEventListener(RemoteInterface eventListener) throws RemoteException;
    public void removeEventListener(RemoteInterface eventListener) throws RemoteException;
    public Bomb getBomb() throws RemoteException;
}
