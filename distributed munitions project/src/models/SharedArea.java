package models;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.bombs.*;
import models.ships.*;
import models.stores.*;

@SuppressWarnings("deprecation")

public class SharedArea extends UnicastRemoteObject implements Runnable, Serializable, RemoteInterface
{
    BombFactory bombFactory;
    String signalLocation;
    Ship arrivingShip;

    DispatchDao dispatches;

    LinkedList<Bomb> jobQueue = new LinkedList<>(); // Implements a queue so rapid orders from quick button presses can be handled effectively
    static int shipCount = 0;

    Thread makeBombProducerThread;  // The Producer Thread responsible for maing the bomb
    Thread storeDetailsConsumerThread; // The Consumer Thread used for storing the data

    static List<CallbackListener> list = new ArrayList<CallbackListener>();
    ArrayList clientPollStreams;
    
    // SOCKETS IMPLEMENTATION
    public void goSockets()
    {
        clientPollStreams = new ArrayList();
        try
        {
            ServerSocket serverSock = new ServerSocket(5000);
            System.out.println("Starting Sockets Service");
            while(true)
            {
                Socket clientSocket = serverSock.accept();
                
                ObjectOutputStream pollWriter = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println("Adding Sockets Listener");
                clientPollStreams.add(pollWriter); 
                Thread t = new Thread(new ClientSocketHandler(clientSocket));
                t.start();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void pollEveryone(String message)
    {
        Iterator it = clientPollStreams.iterator();
        while(it.hasNext())
        {
            try
            {
                ObjectOutputStream writer = (ObjectOutputStream) it.next();
                writer.writeObject(message);
                writer.flush();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }

    public class ClientSocketHandler implements Runnable
    {
        ObjectInputStream reader;
        Socket sock;
        
        public ClientSocketHandler(Socket clientSocket)
        {
            try
            {
                sock = clientSocket;
                reader = new ObjectInputStream(sock.getInputStream());
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    
        public void run()
        {
            try
            {
                Ship shipObject;
                SharedArea sharedAreaInstance = new SharedArea();

                while((shipObject = (Ship) reader.readObject()) != null)
                {
                    sharedAreaInstance.signalLocation = shipObject.getDeparturePort();
                    pollEveryone("Socket polled! " + shipObject.getType() + " Ship has been dealt with. The server is now free to deal with another ship");
                    update(sharedAreaInstance, shipObject);
                    Thread produceAndConsume = new Thread(sharedAreaInstance);
                    produceAndConsume.start();
                }
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        
    }
    //  SOCKETS IMPLEMNTATION

    //RMI IMPLEMENTATION
    @Override
    public void goRMI(Ship incomingShip) throws RemoteException 
    {
        Thread rmiThread = new Thread(new ClientRMIHandler(incomingShip));
        rmiThread.start();
    }

    public class ClientRMIHandler implements Runnable
    {
        Ship incomingShip;

        
        public ClientRMIHandler(Ship incomingShip)
        {
            this.incomingShip = incomingShip;
        }
    
        public void run()
        {
            try
            {
                SharedArea sharedAreaInstance = new SharedArea();
                
                update(sharedAreaInstance, incomingShip);
                Thread produceAndConsume = new Thread(sharedAreaInstance);
                produceAndConsume.start();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }

    // Callback
    @Override
    public Bomb getBomb() throws RemoteException
    {
        try
        {
            return bombFactory.create();   
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public void addEventListener(CallbackListener eventListener) throws RemoteException
    {

        System.out.println("Adding RMI Listener");
        list.add(eventListener);

    }

    @Override
    public void removeEventListener(CallbackListener eventListener) throws RemoteException 
    {
        list.remove(eventListener);
    }

    // Callback

    public void notifyListeners()
    {
        for(CallbackListener listener : list)
        {
            try
            {
                listener.bombMade(bombFactory.create());
            }
            catch(RemoteException e)
            {
                System.out.println(e);
                list.remove(listener);
            }
        }
    }

    // RMI Implementation

    public static void main(String[] args)
    {
        try
        {
            RemoteInterface service = new SharedArea();
            System.out.println("Starting RMI Service");
            Naming.rebind("RemoteServer", service);
            new SharedArea().goSockets();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    public SharedArea() throws RemoteException
    {
        dispatches = DispatchDaoImpl.getInstance(); // Singleton instance of the store implemented using DAO Pattern
    }

    public void update(SharedArea sharedAreaInstance, Ship shipObject)
    {
        sharedAreaInstance.signalLocation = shipObject.getDeparturePort();
            
        switch (shipObject.getType()) //Uses shipFactory.create() to 'spot' ships that have arrived
        {
            case "Aircraft Carrier":
                sharedAreaInstance.arrivingShip = new AircraftCarrierShipFactory().create();
                sharedAreaInstance.bombFactory = new TorpedoBombFactory();
                break;
            case "Destroyer":
                sharedAreaInstance.arrivingShip = new DestroyerShipFactory().create(); 
                sharedAreaInstance.bombFactory = new ArmorPiercingBombFactory();
                break;
            case "Sailing":
                sharedAreaInstance.arrivingShip = new SailingShipFactory().create();
                sharedAreaInstance.bombFactory = new BlastBombFactory();
                break;
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
            shipCount++;
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
        notifyListeners();
        Dispatch dispatchObject = new Dispatch(this.signalLocation, this.arrivingShip, bomb);
        System.out.println(dispatchObject);
        dispatches.addDispatch(dispatchObject); // DAO Implementation, prevents dispatches from being accessed directly
        this.bombFactory = null; // Prevents the same bomb for one ship from being created multiple times
        notifyAll();

        if(shipCount == 10) // Maximum of 10 ships from either direction
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
        if(shipCount <= 10)
        {
            storeDetailsConsumerThread.start();

            makeBombProducerThread.start();
        }
        else
        {
            System.out.println("Cannot make more bombs, fleet limit reached");
        }
    }
}
