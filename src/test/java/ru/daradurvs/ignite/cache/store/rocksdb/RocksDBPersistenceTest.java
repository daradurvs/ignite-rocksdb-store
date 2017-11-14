package ru.daradurvs.ignite.cache.store.rocksdb;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests enabled RocksDB persistence.
 */
public class RocksDBPersistenceTest extends RocksDBPersistenceAbstractTest {
    @Test
    public void testSingleNodePersistence() throws Exception {
        final int entries = 100_000;
        final String cacheName = "testCacheName";
        final String prefix = "test";

        try (Ignite ignite = startIgniteCluster("0")) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheName);

            for (int i = 0; i < entries; i++) {
                cache.put(i, prefix + i); // write through
            }
        }

        try (Ignite ignite = startIgniteCluster("0")) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheName);

            for (int i = 0; i < entries; i++) {
                assertEquals(prefix + i, cache.get(i)); // read through
            }
        }
    }

    /** {@inheritDoc} */
    @Override protected CacheConfiguration<Integer, String> getCacheConfiguration(IgniteConfiguration cfg) {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setName(TEST_CACHE_NAME);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>(tempPath.toString(), TEST_CACHE_NAME, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }
}
