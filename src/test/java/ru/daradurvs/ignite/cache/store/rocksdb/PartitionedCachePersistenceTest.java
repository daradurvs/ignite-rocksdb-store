package ru.daradurvs.ignite.cache.store.rocksdb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.AffinityFunction;
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

        try {
            Ignite ignite = startIgniteCluster(nodesCount());

            assertEquals(nodesCount(), ignite.cluster().nodes().size());

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            for (int i = 0; i < entries; i++) {
                cache.put(i, prefix + i); // write through
            }
        }
        finally {
            Ignition.stopAll(false);
        }

        try (Ignite ignite = startIgniteCluster(nodesCount())) {

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            assertEquals(nodesCount(), ignite.cluster().nodes().size());

            for (int i = 0; i < entries; i++) {
                assertEquals(prefix + i, cache.get(i)); // read through
            }
        }
        finally {
            Ignition.stopAll(false);
        }
    }

    protected Ignite startIgniteCluster(int count) throws Exception {
        assert count > 0;

        Ignite node = startIgniteCluster("0");

        for (int i = 1; i < count; i++) {
            startIgniteCluster(Integer.toString(i));
        }

        return node;
    }

    protected Ignite startIgniteCluster(String name) throws Exception {
        IgniteConfiguration cfg = getIgniteConfiguration(name);

        return Ignition.start(cfg);
    }

    protected IgniteConfiguration getIgniteConfiguration(String instanceName) throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName(instanceName);

        cfg.setCacheConfiguration(getCacheConfiguration(cfg));

        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(
            new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("127.0.0.1:47500..47509"))
        ));

        return cfg;
    }

    protected CacheConfiguration<Integer, String> getCacheConfiguration(IgniteConfiguration cfg) throws Exception {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheCfg.setBackups(backups());
        cacheCfg.setName(TEST_CACHE_NAME);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);
        cacheCfg.setAffinity(affinityFunction());
        cacheCfg.setRebalanceMode(CacheRebalanceMode.NONE);

        // Emulating separate machines which store data in independent stores.
        Path path = Paths.get(tempPath.toString(), cfg.getIgniteInstanceName());

        assert tempPath.toFile().exists() || tempPath.toFile().mkdir();

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>(path.toString(), TEST_CACHE_NAME, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }

    protected AffinityFunction affinityFunction() {
        return new TestAffinityFunction(partitions(), backups());
    }

    protected int nodesCount() {
        return 4;
    }

    /**
     * @return Number of partitions for one cache.
     */
    protected int partitions() {
        return nodesCount();
    }

    /**
     * @return Number of backup nodes for one partition.
     */
    protected int backups() {
        return 1;
    }
}
