package models;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Observable;

import models.ships.*;

@SuppressWarnings("deprecation")

public class Kinsale extends Observable implements Serializable
{
    private String sentryLocationName;

    ObjectOutputStream writer;
    ObjectInputStream reader;

    Socket sock;

    boolean serverFree;
    public Kinsale(String sentryLocation) 
    {
        this.sentryLocationName = sentryLocation;
        goSockets();
    }

    public void goSockets()
    {
        try
        {
            sock = new Socket("127.0.0.1", 5000);
            writer = new ObjectOutputStream(sock.getOutputStream());
            reader = new ObjectInputStream(sock.getInputStream());
            System.out.println("Networking Established");
            serverFree = true;
            Thread readerPollThread = new Thread(new IncomingPollReader());
            readerPollThread.start();

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void sendSockets(ShipFactory shipFactoryObject)
    {
        try
        {
            if(serverFree)
            {

            
                Ship incomingShip = shipFactoryObject.create();
                incomingShip.setDeparturePort(sentryLocationName);
                writer.writeObject(incomingShip);
                writer.flush();
                serverFree = false;
            }
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public class IncomingPollReader implements Runnable
    {
        public void run()
        {
            Object message;
            try
            {
                while((message = reader.readObject()) != null)
                {
                    message = (String) message;
                    System.out.println(message);
                    serverFree = true;
                }
            }

            catch(Exception e)
            {
                System.out.println(e);
            }
        }
    }
}
