package controllers;

import models.ships.*;
import models.SharedArea;

import java.rmi.RemoteException;

import models.*;

@SuppressWarnings("deprecation")

public class SentryController {
    Kinsale kinsale;
    Youghal youghal;

    public SentryController() throws RemoteException
    {
        kinsale = new Kinsale("Kinsale");
        youghal = new Youghal();
        youghal.makeConnection("Youghal");
    }

    public void makeShipController(String sentryLocationName, ShipFactory shipFactoryObject) // Function makes the Thread fire off when the button is clicked
    {
        if(sentryLocationName.equals("Kinsale"))
        {
            
            kinsale.sendSockets(shipFactoryObject); // Queue is used to prevent threads from interrupting and overwriting each other
            // Thread thread  = new Thread(kinsale);
            // thread.start();     
        }

        if(sentryLocationName.equals("Youghal"))
        {
            youghal.goRMI(shipFactoryObject);
        }
    }
}

