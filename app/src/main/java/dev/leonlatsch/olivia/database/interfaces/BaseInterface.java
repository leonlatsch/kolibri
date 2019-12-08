package dev.leonlatsch.olivia.database.interfaces;

import dev.leonlatsch.olivia.database.DatabaseMapper;

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
