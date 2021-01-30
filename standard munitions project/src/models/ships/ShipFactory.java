package models.ships;

import java.io.Serializable;

public interface ShipFactory extends Serializable
{
    public Ship create();
}
