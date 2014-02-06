package ch.hgdev.toposuite.dao.collections;

import java.util.ArrayList;
import java.util.List;

import ch.hgdev.toposuite.dao.interfaces.DAO;
import ch.hgdev.toposuite.dao.interfaces.DAOMapper;


/**
 * 
 * @author HGdev
 * @param <E>
 */
public class DAOMapperArrayList<E> extends ArrayList<E> implements DAOMapper {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2385665453541274357L;
    
    /**
     * List of observers.
     */
    private List<DAO> daoList;
    
    /**
     * FIXME update comment
     * Control whether methods that change the list automatically call notifyObservers().
     * If set to false, caller must manually call
     * {@link DAOMapperArrayList#notifyObservers()} to have the changes reflected in 
     * the observers.
     * 
     * The default value is set to true.
     */
    private boolean notifyOnChange;
    
    /**
     * Construct a new {@link DAOMapperArrayList}.
     */
    public DAOMapperArrayList() {
        super();
        this.daoList = new ArrayList<DAO>();
        this.notifyOnChange = true;
    }

    @Override
    public boolean add(E obj) {
        boolean status = super.add(obj);
        if (status && this.notifyOnChange) {
            this.notifyCreation(obj);
        }
        return status;
    }

    @Override
    public void add(int index, E obj) {
        super.add(index, obj);
        if (this.notifyOnChange) {
            this.notifyCreation(obj);
        }
    }

    @Override
    public E remove(int index) {
        E obj = super.remove(index);
        if (this.notifyOnChange) {
            this.notifyDeletion(obj);
        }
        return obj;
    }

    @Override
    public boolean remove(Object obj) {
        boolean status = super.remove(obj);
        if (status && notifyOnChange) {
            this.notifyDeletion(obj);
        }
        return status;
    }

    /**
     * Getter for notifyOnChange flag.
     * @return the notifyOnChange
     */
    public boolean isNotifyOnChange() {
        return this.notifyOnChange;
    }

    /**
     * Setter for notifyOnChange flag.
     * @param notifyOnChange
     *            the notifyOnChange to set
     */
    public void setNotifyOnChange(boolean _notifyOnChange) {
        this.notifyOnChange = _notifyOnChange;
    }
    
    @Override
    public void registerDAO(DAO dao) {
        this.daoList.add(dao);
    }

    @Override
    public void removeDAO(DAO dao) {
        this.daoList.remove(dao);
    }

    @Override
    public void notifyCreation(Object obj) {
        for (DAO dao : daoList) {
            dao.create(obj);
        }
    }

    @Override
    public void notifyDeletion(Object obj) {
        for (DAO dao : daoList) {
            dao.delete(obj);
        }
    }
    
    @Override
    public void notifyUpdate(Object obj) {
        // actually not used
    }
}