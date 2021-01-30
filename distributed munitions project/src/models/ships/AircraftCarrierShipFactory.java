package models.ships;

public class AircraftCarrierShipFactory implements ShipFactory
{
    public Ship create()
    {
        return new AircraftCarrierShip();
    }
}