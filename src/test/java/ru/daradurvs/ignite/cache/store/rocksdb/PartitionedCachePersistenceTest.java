package ru.daradurvs.ignite.cache.store.rocksdb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Vyacheslav Daradur
 * @since 23.10.2017
 */
public class PartitionedCachePersistenceTest {
    private static final String TEST_CACHE_NAME = "testCacheName";

    private Path tempPath;

    @Before
    public void init() {
        tempPath = Paths.get(System.getProperty("java.io.tmpdir"), (PartitionedCachePersistenceTest.class.getSimpleName() + "_" + System.currentTimeMillis()));
    }

    @After
    public void clear() {
        IgniteUtils.delete(tempPath.toFile());
    }

    @Test
    public void testPartitionedCachePersistence() throws Exception {
        final int entries = 10_000;
        final String prefix = "test";

        try (Ignite ignite = startIgniteCluster("0", "1", "2")) {

            assertEquals(3, ignite.cluster().nodes().size());

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            for (int i = 0; i < entries; i++) {
                cache.put(i, prefix + i); // write through
            }
        }
        finally {
            Ignition.stopAll(false);
        }

        try (Ignite ignite = startIgniteCluster("0", "1", "2")) {

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            assertEquals(3, ignite.cluster().nodes().size());

            for (int i = 0; i < entries; i++) {
                assertEquals(prefix + i, cache.get(i)); // read through
            }
        }
        finally {
            Ignition.stopAll(false);
        }
    }

    private Ignite startIgniteCluster(String... names) throws Exception {
        assert names.length > 1;

        Ignite ignite = Ignition.start(getIgniteConfiguration(names[0]));

        for (int i = 1; i < names.length; i++) {
            Ignition.start(getIgniteConfiguration(names[i]));
        }

        return ignite;
    }

    private IgniteConfiguration getIgniteConfiguration(String instanceName) throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName(instanceName);

        cfg.setCacheConfiguration(getCacheConfiguration(cfg));

        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(
            new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("127.0.0.1:47500..47509"))
        ));

        return cfg;
    }

    private CacheConfiguration<Integer, String> getCacheConfiguration(IgniteConfiguration cfg) throws Exception {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheCfg.setBackups(0);
        cacheCfg.setName(TEST_CACHE_NAME);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);
        cacheCfg.setAffinity(new TestAffinityFunction());

        Path path = Paths.get(tempPath.toString(), cfg.getIgniteInstanceName());

        assert tempPath.toFile().exists() || tempPath.toFile().mkdir();

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>(path.toString(), TEST_CACHE_NAME, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }
}
