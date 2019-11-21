package dev.leonlatsch.olivia.database.interfaces;

import dev.leonlatsch.olivia.database.DatabaseMapper;

/**
 * BaseInterface
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
