package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.RocksDBException;

/**
 * Manages RocksDB instances which were opened in current JVM.
 */
public class RocksDBHolder {
    /** Key - Ignite instance name; Value - mapped RocksDB instances. */
    private static final Map<String, List<RocksDBWrapper>> ROCKS_DB_INSTANCES = new ConcurrentHashMap<>();

    /**
     * Returns wrapped RocksDB instance with given path if exists, otherwise create new one.
     *
     * @param igniteInstanceName Ignite instance name.
     * @param pathToDB Path to database.
     * @return Wrapper around RocksDB instance.
     * @throws RocksDBException In case of an error.
     */
    public static RocksDBWrapper db(@NotNull String igniteInstanceName,
        @NotNull String pathToDB) throws RocksDBException {
        List<RocksDBWrapper> dbInstances = ROCKS_DB_INSTANCES.get(igniteInstanceName);

        Path path = Paths.get(pathToDB);

        if (dbInstances != null) {
            for (RocksDBWrapper instance : dbInstances)
                if (instance.getPath().equals(path))
                    return instance;
        }
        else {
            dbInstances = new ArrayList<>(1);
        }

        RocksDBWrapper db = new RocksDBWrapper(pathToDB);
        dbInstances.add(db);

        ROCKS_DB_INSTANCES.put(igniteInstanceName, dbInstances);

        return db;
    }

    /**
     * Closes all RocksDB instances mapped to given Ignite instance name.
     *
     * @param igniteInstanceName Ignite instance name.
     * @throws RocksDBException In case of an error.
     */
    public static synchronized void close(@NotNull String igniteInstanceName) throws RocksDBException {
        List<RocksDBWrapper> dbInstances = ROCKS_DB_INSTANCES.get(igniteInstanceName);

        if (dbInstances != null)
            for (RocksDBWrapper wrapper : dbInstances)
                wrapper.close();

        ROCKS_DB_INSTANCES.remove(igniteInstanceName);
    }

    /**
     * Closes the connections to all previously opened RockDB instances in current JVM.
     *
     * @throws RocksDBException In case of an error.
     */
    public static void closeAll() throws RocksDBException {
        for (Map.Entry<String, List<RocksDBWrapper>> entry : ROCKS_DB_INSTANCES.entrySet()) {
            for (RocksDBWrapper dbInstances : entry.getValue()) {
                dbInstances.close();
            }

            ROCKS_DB_INSTANCES.clear();
        }
    }
}