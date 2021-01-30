package models;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Observable;

import models.bombs.Bomb;
import models.ships.*;

@SuppressWarnings("deprecation")

public class Youghal extends UnicastRemoteObject implements CallbackListener {
    private String sentryLocationName;
    RemoteInterface service;

    public Youghal() throws RemoteException
    {

    }

    public void makeConnection(String sentryLocation) {
        this.sentryLocationName = sentryLocation;
        try 
        {
            service = (RemoteInterface) Naming.lookup("rmi://127.0.0.1/RemoteServer");
            Youghal youghalListener = new Youghal();
            service.addEventListener(youghalListener);
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        }
    }

    public void goRMI(ShipFactory shipFactoryObject) {
        Ship incomingShip = shipFactoryObject.create();
        incomingShip.setDeparturePort(sentryLocationName);
        try {
            service.goRMI(incomingShip);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void bombMade(Bomb bomb) throws RemoteException {
        System.out.println("Callback Event! Bomb made " + bomb.getType());

    }

}
