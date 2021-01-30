package models.bombs;

public class TorpedoBombFactory implements BombFactory
{
    public Bomb create()
    {
        return new TorpedoBomb();
    }
}