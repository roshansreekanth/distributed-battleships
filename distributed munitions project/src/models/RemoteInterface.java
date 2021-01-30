package models;

import java.rmi.*;

import models.bombs.Bomb;
import models.ships.Ship;

public interface RemoteInterface extends Remote
{
    public void goRMI(Ship shipObject) throws RemoteException;
    public void addEventListener(CallbackListener eventListener) throws RemoteException;
    public void removeEventListener(CallbackListener eventListener) throws RemoteException;
    public Bomb getBomb() throws RemoteException;
}