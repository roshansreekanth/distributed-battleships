package models.stores;

import java.util.List;

public interface DispatchDao
{
    public List<Dispatch> getAllDispatches();
    public void addDispatch(Dispatch dispatch);
}