package models;

import java.rmi.Remote;
import java.rmi.RemoteException;

import models.bombs.Bomb;

public interface CallbackListener extends Remote
{
    public void bombMade(Bomb bomb) throws RemoteException;
}
