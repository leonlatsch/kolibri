package dev.leonlatsch.olivia.database;

import com.activeandroid.Model;

public interface EntityChangedListener<T extends Model> {

    /**
     * The saved user has changed
     * If the new entity is null, the user got deleted
     *
     * @param newEntity
     */
    void entityChanged(T newEntity);
}
