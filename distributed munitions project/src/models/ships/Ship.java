package models.ships;

import java.io.Serializable;

public abstract class Ship implements Serializable
{
    private String type;
    private String departurePort;

    public String getType()
    {
        return this.type;
    }

    public String getDeparturePort()
    {
        return this.departurePort;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setDeparturePort(String departurePort)
    {
        this.departurePort = departurePort;
    }
}
