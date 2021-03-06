package ru.daradurvs.ignite.cache.store.rocksdb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.cache.configuration.Factory;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import ru.daradurvs.ignite.cache.store.rocksdb.common.DestructorLifecycleBean;
import ru.daradurvs.ignite.cache.store.rocksdb.common.RocksDBHolder;
import ru.daradurvs.ignite.cache.store.rocksdb.common.RocksDBWrapper;
import ru.daradurvs.ignite.cache.store.rocksdb.common.Utils;
import ru.daradurvs.ignite.cache.store.rocksdb.options.RocksDBConfiguration;
import ru.daradurvs.ignite.cache.store.rocksdb.serializer.JavaSerializer;

/** {@inheritDoc} */
public class RocksDBCacheStoreFactory<K, V> implements Factory<RocksDBCacheStore<K, V>> {
    private static final long serialVersionUID = 0L;

    /** Auto-inject Ignite instance. */
    @IgniteInstanceResource
    private transient Ignite ignite;

    private RocksDBConfiguration dbCfg;

    public RocksDBCacheStoreFactory(RocksDBConfiguration dbCfg, IgniteConfiguration cfg) {
        this.dbCfg = dbCfg;

        registerDestructor(cfg);
    }

    private void registerDestructor(IgniteConfiguration cfg) {
        if (Utils.isClientMode(cfg))
            return;

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
            if (Utils.isClientMode(ignite))
                return new RocksDBClientStubCacheStore<>();

            validate(dbCfg);

            String consistentNodeId = Utils.getConsistentId(ignite);

            Path path = Paths.get(dbCfg.getPathToDB(), dbCfg.getCacheName(), consistentNodeId);

            if (!path.toFile().exists())
                path.toFile().mkdirs();

            RocksDBWrapper dbWrapper = RocksDBHolder.db(consistentNodeId, path.toString(), dbCfg.getDbOptions());
            ColumnFamilyHandle handle = dbWrapper.handle(dbCfg.getCacheName());

            return new RocksDBCacheStore<>(dbWrapper.db(), handle, dbCfg.getWriteOptions(), dbCfg.getReadOptions(), new JavaSerializer());
        }
        catch (RocksDBException e) {
            throw new IllegalStateException("Couldn't initialize RocksDB instance.", e);
        }
    }

    /**
     * Validates given RocksDB configuration.
     *
     * @param cfg RocksDB configuration.
     */
    private void validate(RocksDBConfiguration cfg) {
        StringBuilder msgBldr = new StringBuilder();

        if (cfg.getPathToDB() == null)
            msgBldr.append("pathToDB; ");

        if (cfg.getCacheName() == null)
            msgBldr.append("cacheName; ");

        if (cfg.getDbOptions() == null)
            msgBldr.append("dbOptions; ");

        if (cfg.getWriteOptions() == null)
            msgBldr.append("writeOptions; ");

        if (cfg.getReadOptions() == null)
            msgBldr.append("readOptions; ");

        if (msgBldr.length() != 0) {
            throw new IllegalStateException(
                "Following fields should be defined explicitly in RocksDBConfiguration: [ " + msgBldr + "]");
        }
    }
}
