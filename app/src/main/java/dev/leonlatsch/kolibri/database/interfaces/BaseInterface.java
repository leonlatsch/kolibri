package dev.leonlatsch.kolibri.database.interfaces;

import dev.leonlatsch.kolibri.database.DatabaseMapper;

/**
 * Base class for database interfaces
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public abstract class BaseInterface {

    /**
     * Used to map convert dto's and models
     */
    private DatabaseMapper databaseMapper = DatabaseMapper.getInstance();

    protected DatabaseMapper getDatabaseMapper() {
        return databaseMapper;
    }
}
