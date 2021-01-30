package models.stores;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DispatchDaoImpl implements DispatchDao, Serializable 
{
    private volatile static DispatchDaoImpl singletonObject;
    
    List<Dispatch> dispatches;

    private DispatchDaoImpl()
    {
        dispatches = new ArrayList<Dispatch>();
    }

    public static DispatchDaoImpl getInstance()
    {
        if(singletonObject == null)
        {
            synchronized(DispatchDaoImpl.class)
            {
                if(singletonObject == null) //If another thread happens to reach this part
                {
                    singletonObject = new DispatchDaoImpl();
                }
            }
        }
        return singletonObject;
    }

    @Override
    public List<Dispatch> getAllDispatches() 
    {
        return dispatches;
    }

    @Override
    public void addDispatch(Dispatch dispatch) 
    {
        dispatches.add(dispatch);
    }
}
