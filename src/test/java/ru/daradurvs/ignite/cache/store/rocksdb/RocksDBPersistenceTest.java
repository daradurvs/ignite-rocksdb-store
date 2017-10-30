package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.IgniteUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rocksdb.RocksDBException;

import static org.junit.Assert.assertEquals;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class RocksDBPersistenceTest {
    private Path tempPath;

    @Before
    public void init() {
        tempPath = Paths.get(System.getProperty("java.io.tmpdir"), (RocksDBPersistenceTest.class.getSimpleName() + "_" + System.currentTimeMillis()));
    }

    @After
    public void clear() {
        IgniteUtils.delete(tempPath.toFile());
    }

    @Test
    public void testRocksDBPersistence() throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();

        final String cacheName = "testCacheName";
        final String prefix = "test";

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(getCacheConfiguration(cacheName, ignite.configuration()));

            for (int i = 0; i < 100_000; i++) {
                cache.put(i, prefix + i); // put with persistence
            }
        }

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(getCacheConfiguration(cacheName, ignite.configuration()));

            for (int i = 0; i < 100_000; i++) {
                assertEquals(prefix + i, cache.get(i)); // get from persistence
            }
        }
    }

    private CacheConfiguration<Integer, String> getCacheConfiguration(String cacheName, IgniteConfiguration cfg)
        throws RocksDBException, IOException {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setName(cacheName);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>(tempPath.toString(), cacheName, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }
}