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
        final int entries = 100_000;
        final String cacheName = "testCacheName";
        final String prefix = "test";

        IgniteConfiguration cfg = getIgniteConfiguration("0");
        cfg.setCacheConfiguration(getCacheConfiguration(cacheName, cfg));

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheName);

            for (int i = 0; i < entries; i++) {
                cache.put(i, prefix + i); // write through
            }
        }

        cfg = getIgniteConfiguration("1");
        cfg.setCacheConfiguration(getCacheConfiguration(cacheName, cfg));

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheName);

            for (int i = 0; i < entries; i++) {
                assertEquals(prefix + i, cache.get(i)); // read through
            }
        }
    }

    private IgniteConfiguration getIgniteConfiguration(String instanceName) {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName(instanceName);

        return cfg;
    }

    private CacheConfiguration<Integer, String> getCacheConfiguration(String cacheName, IgniteConfiguration cfg)
        throws RocksDBException, IOException {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setName(cacheName);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);
        cacheCfg.setAffinity(new TestAffinityFunction());

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>(tempPath.toString(), cacheName, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }
}
