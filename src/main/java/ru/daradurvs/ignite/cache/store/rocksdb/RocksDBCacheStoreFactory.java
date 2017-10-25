package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.Serializable;
import javax.cache.configuration.Factory;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class RocksDBCacheStoreFactory<K, V> implements Factory<RocksDBCacheStore<K, V>>, Serializable {
    static {
        RocksDB.loadLibrary();
    }

    @Override public RocksDBCacheStore<K, V> create() {
        try {
            RocksDB db = DBManager.db();

//            return new RocksDBCacheStore<>(db, db.createColumnFamily(getColumnDescriptor()));
            return new RocksDBCacheStore<>(db, null);
        }
        catch (RocksDBException e) {
            throw new IllegalStateException(e);
        }
    }

}
