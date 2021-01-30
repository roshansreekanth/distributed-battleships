package models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;

import models.ships.*;

@SuppressWarnings("deprecation")

public class Sentry extends Observable implements Runnable, Serializable
{
    private String sentryLocationName;
    private static int shipCount;
    private LinkedList<ShipFactory> workQueue;

    public Sentry(String sentryLocation) 
    {
        workQueue = new LinkedList<>();
        this.sentryLocationName = sentryLocation;
    }

    /*Using 'synchronized' makes sure that a thread cannot 
    take a job from the queue if it is empty, and helps prevent 
    concurrency issues as only one thread is allowed to hold the lock 
    for the function*/

    public synchronized void shipDetected()
    {        
        setChanged();
        shipCount++;
        /*Sends the first 'job' from the queue to the shared area. 
        Helps prevent issues where multiple threads might try to call 
        shipDetected() at the same time and overwriting the values of the newly made ships*/
        notifyObservers(workQueue.removeFirst().create());
        clearChanged();
    }

    public static int getShipCount()
    {
        return shipCount;
    }

    public void addToQueue(ShipFactory shipFactoryObject)
    {
        workQueue.add(shipFactoryObject);
    }

    public String getLocationName()
    {
        return this.sentryLocationName;
    }

    @Override
    public void run() {
        int maxWaitMilliSeconds = 3000;
        int minWaitMilliSeconds = 1000;
    
        try 
        {
            long time = (long)(minWaitMilliSeconds + (int)(Math.random() * ((maxWaitMilliSeconds - minWaitMilliSeconds) + 1)));
            System.out.println("Waiting for " + time + " milliseconds");
            Thread.sleep(time);
            shipDetected();
        }

        catch (InterruptedException e) 
        {
            System.out.println(e);
        }
    
    }
}
