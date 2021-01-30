package models.bombs;

public class ArmorPiercingBombFactory implements BombFactory
{
    public Bomb create()
    {
        return new ArmorPiercingBomb();
    }
}