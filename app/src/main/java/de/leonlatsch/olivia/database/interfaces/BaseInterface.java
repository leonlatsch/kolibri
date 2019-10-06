package de.leonlatsch.olivia.database.interfaces;

import de.leonlatsch.olivia.database.DatabaseMapper;

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
