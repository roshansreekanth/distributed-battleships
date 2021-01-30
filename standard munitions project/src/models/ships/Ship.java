package models.ships;

import java.io.Serializable;

public abstract class Ship implements Serializable
{
    private String type;

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
