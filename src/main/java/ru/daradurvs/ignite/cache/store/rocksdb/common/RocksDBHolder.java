package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.RocksDBException;

/**
 * Manages RocksDB instances which were opened in current JVM.
 */
public class RocksDBHolder {
    /** Key - Ignite instance id; Value - mapped RocksDB instances. */
    private static final Map<UUID, List<RocksDBWrapper>> ROCKS_DB_INSTANCES = new ConcurrentHashMap<>();

    /**
     * Returns wrapped RocksDB instance with given path if exists, otherwise create new one.
     *
     * @param nodeId Ignite instance name.
     * @param pathToDB Path to database.
     * @return Wrapper around RocksDB instance.
     * @throws RocksDBException In case of an error.
     */
    public static RocksDBWrapper db(@NotNull UUID nodeId,
        @NotNull String pathToDB) throws RocksDBException {
        List<RocksDBWrapper> dbInstances = ROCKS_DB_INSTANCES.get(nodeId);

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

        ROCKS_DB_INSTANCES.put(nodeId, dbInstances);

        return db;
    }

    /**
     * Closes all RocksDB instances mapped to given Ignite instance name.
     *
     * @param nodeId Ignite instance name.
     * @throws RocksDBException In case of an error.
     */
    public static synchronized void close(@NotNull UUID nodeId) throws RocksDBException {
        List<RocksDBWrapper> dbInstances = ROCKS_DB_INSTANCES.get(nodeId);

        if (dbInstances != null)
            for (RocksDBWrapper wrapper : dbInstances)
                wrapper.close();

        ROCKS_DB_INSTANCES.remove(nodeId);
    }

    /**
     * Closes the connections to all previously opened RockDB instances in current JVM.
     *
     * @throws RocksDBException In case of an error.
     */
    public static void closeAll() throws RocksDBException {
        for (Map.Entry<UUID, List<RocksDBWrapper>> entry : ROCKS_DB_INSTANCES.entrySet()) {
            for (RocksDBWrapper dbInstances : entry.getValue()) {
                dbInstances.close();
            }

            ROCKS_DB_INSTANCES.clear();
        }
    }
}