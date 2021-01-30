package controller;

import models.ships.*;
import models.SharedArea;
import models.*;

@SuppressWarnings("deprecation")

public class Controller 
{   
    SharedArea blarney;
    Sentry kinsale;
    Sentry youghal;

    public Controller()
    {
        blarney = new SharedArea();
        kinsale = new Sentry("Kinsale");
        youghal = new Sentry("Youghal");

        Thread blarneyThread = new Thread(blarney);
        blarneyThread.start(); // Blarney thread starts and waits for a notification from the sentries

        kinsale.addObserver(blarney);
        youghal.addObserver(blarney);
    }

    public void makeShipController(String sentryLocationName, ShipFactory shipFactoryObject) // Function makes the Thread fire off when the button is clicked
    {
        if(sentryLocationName.equals("Kinsale"))
        {
            kinsale.addToQueue(shipFactoryObject); // Queue is used to prevent threads from interrupting and overwriting each other
            Thread thread  = new Thread(kinsale);
            thread.start();     
        }

        if(sentryLocationName.equals("Youghal"))
        {
            youghal.addToQueue(shipFactoryObject);
            Thread thread  = new Thread(youghal);
            thread.start();
        }
    }
}

