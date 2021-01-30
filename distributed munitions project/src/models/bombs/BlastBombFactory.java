package models.bombs;

public class BlastBombFactory implements BombFactory
{
    public Bomb create()
    {
        return new BlastBomb();
    }    
}
