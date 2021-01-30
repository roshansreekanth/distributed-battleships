package models;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import models.*;
import models.bombs.*;
import models.ships.*;
import models.stores.*;

@SuppressWarnings("deprecation")

public class SharedArea implements Observer, Runnable, Serializable 
{
    BombFactory bombFactory;
    Sentry signalLocation;
    Ship arrivingShip;

    DispatchDao dispatches;

    LinkedList<Bomb> jobQueue = new LinkedList<>(); // Implements a queue so rapid orders from quick button presses can be handled effectively

    Thread makeBombProducerThread;  // The Producer Thread responsible for maing the bomb
    Thread storeDetailsConsumerThread; // The Consumer Thread used for storing the data


    public SharedArea() 
    {
        dispatches = DispatchDaoImpl.getInstance(); // Singleton instance of the store implemented using DAO Pattern
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg instanceof Ship) {
            switch (((Ship) arg).getType()) //Uses shipFactory.create() to 'spot' ships that have arrived
            {
                case "Aircraft Carrier":
                    this.arrivingShip = new AircraftCarrierShipFactory().create();
                    this.bombFactory = new TorpedoBombFactory();
                    break;
                case "Destroyer":
                    this.arrivingShip = new DestroyerShipFactory().create(); 
                    this.bombFactory = new ArmorPiercingBombFactory();
                    break;
                case "Sailing":
                    this.arrivingShip = new SailingShipFactory().create();
                    this.bombFactory = new BlastBombFactory();
                    break;
            }
        }
        if (o instanceof Sentry) 
        {
            this.signalLocation = (Sentry) o;
        }

    }

    /* 
    Producer/Consumer - Producer to make the bomb and Consumer to store the details
    */

    public synchronized void produce()
    {
        /* If the job queue is empty, the Producer can add a 
        new job and make a bomb. On the other hand, if the job queue
        is full, the consumer is busy so the producer waits until
        the queue is empty again*/

        while(jobQueue.size() != 0)
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.out.println(e);
            }
        }
        
        if(this.bombFactory != null)
        {
            Bomb bomb = bombFactory.create();
            jobQueue.add(bomb); // Fills the queue
            notifyAll();
        }
    }

    public synchronized void consume()
    {
        /* If the job queue is full, the Consumer can 
        take a job and store the details of the bomb. 
        However, if the job queue is full, the Consumer waits 
        for the Producer to make a bomb before it can store the details*/

        while(jobQueue.size() == 0)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e)
            {
                System.out.println(e);
            }
        }

        Bomb bomb = jobQueue.removeFirst(); // Empties the queue
        
        Dispatch dispatchObject = new Dispatch(this.signalLocation, this.arrivingShip, bomb);
        System.out.println(dispatchObject);
        dispatches.addDispatch(dispatchObject); // DAO Implementation, prevents dispatches from being accessed directly
                
        this.bombFactory = null; // Prevents the same bomb for one ship from being created multiple times
        notifyAll();

        if(Sentry.getShipCount() == 10) // Maximum of 10 ships from either direction
        {
            System.out.println("Fleet is full!");

            //Serializing the data to a file
            String fileName = "dispatch-log.txt";
            try
            {
                FileOutputStream file = new FileOutputStream(fileName);
                ObjectOutputStream out = new ObjectOutputStream(file);
                System.out.println(dispatches.getAllDispatches());
                out.writeObject(dispatches.getAllDispatches());
                out.close();
                file.close();

                System.out.println("The logs have been serialized");

                makeBombProducerThread.stop();
                storeDetailsConsumerThread.stop();
            }

            catch(IOException e)
            {
                System.out.println(e);
            }
        }
    }

    @Override
    public void run() 
    {
        makeBombProducerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(true)
                {
                    produce(); // Producer produces
                }
            }
        });

        storeDetailsConsumerThread = new Thread(new Runnable()
        {
           public void run()
           {
               while(true)
               {
                    consume(); // Consumer consumes
               }
           } 
        });

        makeBombProducerThread.start();
        storeDetailsConsumerThread.start();
    }
}
