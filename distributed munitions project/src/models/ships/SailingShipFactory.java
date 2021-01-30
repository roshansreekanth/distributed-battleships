package models.ships;

public class SailingShipFactory implements ShipFactory 
{
    public Ship create()
    {
        return new SailingShip();
    }
}
