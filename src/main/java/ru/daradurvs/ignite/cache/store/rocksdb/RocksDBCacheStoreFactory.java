package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.Serializable;
import java.util.Arrays;
import javax.cache.configuration.Factory;
import org.apache.ignite.IgniteException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import static org.apache.ignite.lifecycle.LifecycleEventType.AFTER_NODE_STOP;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class RocksDBCacheStoreFactory<K, V> implements Factory<RocksDBCacheStore<K, V>>, Serializable {
    private static final long serialVersionUID = 0L;

    private String pathToDB;
    private String cacheName;

    public RocksDBCacheStoreFactory(String pathToDB, String cacheName, IgniteConfiguration cfg) {
        this.pathToDB = pathToDB;
        this.cacheName = cacheName;

        LifecycleBean[] beans = cfg.getLifecycleBeans();

        if (beans == null) {
            cfg.setLifecycleBeans(new DestructorLifecycleBean());
        }
        else {
            boolean found = false;

            for (LifecycleBean bean : beans) {
                if (bean instanceof DestructorLifecycleBean) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                final int n = beans.length;
                beans = Arrays.copyOf(beans, n + 1);
                beans[n] = new DestructorLifecycleBean();
                cfg.setLifecycleBeans(beans);
            }
        }
    }

    /** {@inheritDoc} */
    @Override public RocksDBCacheStore<K, V> create() {
        try {
            RocksDBWrapper rocksDBWrapper = DBManager.db(pathToDB);
            ColumnFamilyHandle handle = RocksDBWrapper.initColumnFamilyHandle(cacheName);

            return new RocksDBCacheStore<>(rocksDBWrapper.db(), handle);
        }
        catch (RocksDBException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class DestructorLifecycleBean implements LifecycleBean {
        @Override public void onLifecycleEvent(LifecycleEventType evt) throws IgniteException {
            try {
                if (evt == AFTER_NODE_STOP)
                    DBManager.closeAll();
            }
            catch (RocksDBException e) {
                throw new IgniteException("Couldn't close RocksDB instances connection.", e);
            }
        }
    }
}
