package ru.daradurvs.ignite.cache.store.rocksdb;

import org.apache.ignite.cache.CacheMode;
import ru.daradurvs.ignite.cache.store.rocksdb.common.CacheRocksDBPersistenceTest;

/**
 * Tests Ignite replicated cache with enabled RocksDB persistence.
 */
public class ReplicatedCacheRocksDBPersistenceTest extends CacheRocksDBPersistenceTest {
    /** {@inheritDoc} */
    @Override protected CacheMode cacheMode() {
        return CacheMode.REPLICATED;
    }
}
