package ru.daradurvs.ignite.cache.store.rocksdb;

import org.apache.ignite.cache.CacheMode;
import ru.daradurvs.ignite.cache.store.rocksdb.common.CacheRocksDBPersistenceTest;

/**
 * Tests Ignite partitioned cache with enabled RocksDB persistence.
 */
public class PartitionedCacheRocksDBPersistenceTest extends CacheRocksDBPersistenceTest {
    /** {@inheritDoc} */
    @Override protected CacheMode cacheMode() {
        return CacheMode.PARTITIONED;
    }
}
