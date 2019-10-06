package de.leonlatsch.olivia.database.interfaces;

import com.activeandroid.Model;

import java.util.ArrayList;
import java.util.List;

import de.leonlatsch.olivia.database.EntityChangedListener;

/**
 * Base Class for a Database Interface
 * The cached model needs to be
 *
 * @param <T> extends Model
 */
public abstract class CacheInterface<T extends Model> {

    /**
     * Cached model to be synced with database a every transaction
     */
    private T model = null;

    /**
     * {@link List} of {@link EntityChangedListener}s that get notified when a model has changed
     */
    private List<EntityChangedListener> listeners = new ArrayList<>();

    /**
     *  Adds a {@link EntityChangedListener} that gets notified when the model hat changed
     *
     * @param listener the listener to add
     */
    public void addEntityChangedListener(EntityChangedListener listener) {
        listeners.add(listener);
    }

    /**
     *  Notify all listeners with the same model
     * @param model
     */
    void notifyListeners(T model) {
        for (EntityChangedListener listener : listeners) {
            listener.entityChanged(model);
        }
    }

    public void save(T model) {
        if (model != null) {
            if (getModel() != null) {
                delete(getModel());
            }
            model.save();
            setModel(model);
            notifyListeners(model);
        }
    }

    public void delete(T model) {
        model.delete();
        setModel(null);
        notifyListeners(null);
    }

    T getModel() {
        return model;
    }

    void setModel(T model) {
        this.model = model;
    }
}
