package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.IOException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.rocksdb.RocksDBException;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class Main {
    public static void main(String[] args) throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();

        final String cacheName = "testCacheName";

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(getCacheConfiguration(cacheName, ignite.configuration()));

            for (int i = 0; i < 100_000; i++) {
                cache.put(i, "test" + i); // put with persistence
            }
        }

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(getCacheConfiguration(cacheName, ignite.configuration()));

            for (int i = 0; i < 100_000; i++) {
                System.out.println(cache.get(i)); // get from persistence
            }
        }
    }

    private static CacheConfiguration<Integer, String> getCacheConfiguration(String cacheName, IgniteConfiguration cfg)
        throws RocksDBException, IOException {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setName(cacheName);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>("c:/TEMP/rocksdb3", cacheName, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }
}
