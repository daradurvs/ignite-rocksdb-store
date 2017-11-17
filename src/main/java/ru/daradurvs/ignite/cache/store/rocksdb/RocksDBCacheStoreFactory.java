package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.Arrays;
import javax.cache.configuration.Factory;
import org.apache.ignite.IgniteException;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import ru.daradurvs.ignite.cache.store.rocksdb.options.RocksDBConfiguration;
import ru.daradurvs.ignite.cache.store.rocksdb.serializer.JavaSerializer;

import static org.apache.ignite.lifecycle.LifecycleEventType.AFTER_NODE_STOP;

/** {@inheritDoc} */
public class RocksDBCacheStoreFactory<K, V> implements Factory<RocksDBCacheStore<K, V>> {
    private static final long serialVersionUID = 0L;

    private RocksDBConfiguration dbCfg;

    public RocksDBCacheStoreFactory(RocksDBConfiguration dbCfg, IgniteConfiguration cfg) {
        this.dbCfg = dbCfg;

        registerDestructor(cfg);
    }

    private void registerDestructor(IgniteConfiguration cfg) {
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
            RocksDBWrapper dbWrapper = DBManager.db(dbCfg.getPathToDB());
            ColumnFamilyHandle handle = dbWrapper.handle(dbCfg.getCacheName());

            return new RocksDBCacheStore<>(dbWrapper.db(), handle, dbCfg.getWriteOptions(), dbCfg.getReadOptions(), new JavaSerializer());
        }
        catch (
            RocksDBException e)

        {
            throw new IllegalStateException("Couldn't initialize RocksDB instance.", e);
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
