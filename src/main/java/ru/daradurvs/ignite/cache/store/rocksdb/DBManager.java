package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.rocksdb.RocksDBException;

/**
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
public class DBManager {
    private static final Map<String, RocksDBWrapper> DB_INSTANCES = new ConcurrentHashMap<>();

    public static RocksDBWrapper db(String pathToDB) throws RocksDBException {
        RocksDBWrapper db = DB_INSTANCES.get(pathToDB);

        if (db == null) {
            db = new RocksDBWrapper(pathToDB);

            DB_INSTANCES.put(pathToDB, db);
        }

        return db;
    }

    public static void closeAll() throws RocksDBException {
        for (Map.Entry<String, RocksDBWrapper> entry : DB_INSTANCES.entrySet()) {
            entry.getValue().close();
        }

        DB_INSTANCES.clear();
    }
}
