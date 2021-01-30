package models.stores;

import java.io.Serializable;

import models.bombs.*;
import models.*;
import models.ships.*;
public class Dispatch implements Serializable
{
    Sentry location;
    Ship ship;
    Bomb bomb;

    public Dispatch(Sentry location, Ship ship, Bomb bomb)
    {
        this.location = location;
        this.ship = ship;
        this.bomb = bomb;
    }

    public Sentry getSentry()
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

    public void setSentry(Sentry location)
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
        return "Ship Type: " + ship.getType() + ", Location Spotted: " + location.getLocationName() + ", Bomb Dispatched: " + bomb.getType();
    }
}
