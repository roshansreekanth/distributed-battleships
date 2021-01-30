package models.ships;

public class DestroyerShipFactory implements ShipFactory
{
    public Ship create()
    {
        return new DestroyerShip();
    }
}