package models;

import java.rmi.RemoteException;

import models.ships.Ship;

public interface PollingInterface 
{
    public void nextShip() throws RemoteException;
    public boolean doneYet() throws RemoteException;
    public Ship getShip() throws RemoteException;
}
