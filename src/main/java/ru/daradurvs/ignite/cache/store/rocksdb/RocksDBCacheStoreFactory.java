package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.Serializable;
import java.util.Arrays;
import javax.cache.configuration.Factory;
import org.apache.ignite.IgniteException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import static org.apache.ignite.lifecycle.LifecycleEventType.AFTER_NODE_STOP;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class RocksDBCacheStoreFactory<K, V> implements Factory<RocksDBCacheStore<K, V>>, Serializable {
    private String cacheName;

    static {
        RocksDB.loadLibrary();
    }

    public RocksDBCacheStoreFactory(String cacheName, IgniteConfiguration cfg) {
        this.cacheName = cacheName;

        LifecycleBean[] beans = cfg.getLifecycleBeans();
        if (beans != null) {
            final int n = beans.length;
            beans = Arrays.copyOf(beans, n + 1);
            beans[n] = new DestructorLifecycleBean();
            cfg.setLifecycleBeans(beans);
        }
        else {
            cfg.setLifecycleBeans(new DestructorLifecycleBean());
        }
    }

    /** {@inheritDoc} */
    @Override public RocksDBCacheStore<K, V> create() {
        try {
            RocksDB db = DBManager.db();
            ColumnFamilyHandle handle = DBManager.initColumnFamilyHandle(cacheName);

            return new RocksDBCacheStore<>(db, handle);
        }
        catch (RocksDBException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class DestructorLifecycleBean implements LifecycleBean {
        @Override public void onLifecycleEvent(LifecycleEventType evt) throws IgniteException {
            try {
                if (evt == AFTER_NODE_STOP)
                    new DBManager().close();
            }
            catch (RocksDBException e) {
                throw new IgniteException("Couldn't close RocksDB instances connection.", e);
            }
        }
    }
}
