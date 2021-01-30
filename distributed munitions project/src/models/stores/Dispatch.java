package models.stores;

import java.io.Serializable;

import models.bombs.*;
import models.*;
import models.ships.*;
public class Dispatch implements Serializable
{
    String location;
    Ship ship;
    Bomb bomb;

    public Dispatch(String location, Ship ship, Bomb bomb)
    {
        this.location = location;
        this.ship = ship;
        this.bomb = bomb;
    }

    public String getSentry()
    {
        return this.location;
    }

    public Ship getShip()
    {
        return this.ship;
    }

    public Bomb getBomb()
    {
        return this.bomb;
    }

    public void setSentry(String location)
    {
        this.location = location;
    }

    public void setShip(Ship ship)
    {
        this.ship = ship;
    }

    public void setBomb(Bomb bomb)
    {
        this.bomb = bomb;
    }

    @Override
    public String toString()
    {
        return "Ship Type: " + ship.getType() + ", Location Spotted: " + location + ", Bomb Dispatched: " + bomb.getType();
    }
}
