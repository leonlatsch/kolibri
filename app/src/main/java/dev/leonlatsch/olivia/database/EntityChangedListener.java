package dev.leonlatsch.olivia.database;

import com.activeandroid.Model;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface EntityChangedListener<T extends Model> {

    /**
     * The saved user has changed
     * If the new entity is null, the user got deleted
     *
     * @param newEntity
     */
    void entityChanged(T newEntity);
}
