package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.After;
import org.junit.Before;
import ru.daradurvs.ignite.cache.store.rocksdb.RocksDBCacheStoreFactory;
import ru.daradurvs.ignite.cache.store.rocksdb.options.RocksDBConfiguration;

/**
 * Provides useful methods for testing Ignite cache with enabled RocksDB persistence.
 */
public abstract class RocksDBPersistenceAbstractTest {
    protected static final String TEST_CACHE_NAME = "testCacheName";

    protected Path tempPath;

    @Before
    public void init() {
        tempPath = Paths.get(System.getProperty("java.io.tmpdir"), (this.getClass().getSimpleName() + "_" + System.currentTimeMillis()));

        X.println("[RocksDB] temp path: " + tempPath);
    }

    @After
    public void clear() {
        U.delete(tempPath.toFile());

        Ignition.stopAll(true);
    }

    /**
     * Starts number Ignite nodes.
     *
     * @param count Nodes number.
     * @return First started node.
     * @throws Exception In case of an error.
     */
    protected Ignite startIgniteCluster(int count) throws Exception {
        assert count > 0;

        Ignite node = startIgniteCluster("0");

        for (int i = 1; i < count; i++) {
            startIgniteCluster(Integer.toString(i));
        }

        return node;
    }

    /**
     * Starts Ignite node with given name.
     *
     * @param name Ignite instance name.
     * @return Started ignite node.
     * @throws Exception In case of an error.
     */
    protected Ignite startIgniteCluster(String name) throws Exception {
        IgniteConfiguration cfg = igniteConfiguration(name);

        return Ignition.start(cfg);
    }

    /**
     * @param instanceName Ignite instance name.
     * @return Ignite configuration.
     * @throws Exception In case of an error.
     */
    protected IgniteConfiguration igniteConfiguration(String instanceName) throws Exception {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName(instanceName);
        cfg.setConsistentId(instanceName);

        cfg.setCacheConfiguration(getCacheConfiguration(cfg));

        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(
            new TcpDiscoveryVmIpFinder().setAddresses(Collections.singleton("127.0.0.1:47500..47509"))
        ));

        return cfg;
    }

    /**
     * @param cfg Ignite configuration.
     * @return Cache configuration.
     */
    protected CacheConfiguration<Integer, String> getCacheConfiguration(IgniteConfiguration cfg) {
        CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setCacheMode(cacheMode());
        cacheCfg.setWriteSynchronizationMode(cacheWriteSynchronizationMode());
        cacheCfg.setBackups(backups());
        cacheCfg.setName(TEST_CACHE_NAME);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);
        cacheCfg.setAffinity(affinityFunction());
        cacheCfg.setRebalanceMode(rebalanceMode());
        cacheCfg.setRebalanceDelay(rebalanceDelay());

        // Emulating separate machines which store data in independent stores.
        Path path = Paths.get(tempPath.toString(), cfg.getIgniteInstanceName());

        assert tempPath.toFile().exists() || tempPath.toFile().mkdir();

        RocksDBConfiguration rocksCfg = new RocksDBConfiguration(path.toString(), TEST_CACHE_NAME);

        RocksDBCacheStoreFactory<Integer, String> factory = new RocksDBCacheStoreFactory<>(rocksCfg, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }

    /**
     * @return Cache mode.
     */
    protected CacheMode cacheMode() {
        return CacheMode.REPLICATED;
    }

    /**
     * @return Cache rebalance mode.
     */
    protected CacheRebalanceMode rebalanceMode() {
        return CacheRebalanceMode.ASYNC;
    }

    /**
     * @return Rebalance delay.
     */
    protected long rebalanceDelay() {
        return 10_000;
    }

    /**
     * @return Cache write synchronization mode.
     */
    protected CacheWriteSynchronizationMode cacheWriteSynchronizationMode() {
        return CacheWriteSynchronizationMode.PRIMARY_SYNC;
    }

    /**
     * @return Test affinity function.
     */
    protected AffinityFunction affinityFunction() {
        return new TestAffinityFunction(partitions(), backups());
    }

    /**
     * @return Number of nodes.
     */
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

    /**
     * @return Rocks database configuration.
     */
    protected RocksDBConfiguration rocksDBConfiguration() {
        return new RocksDBConfiguration(tempPath.toString(), TEST_CACHE_NAME);
    }

    /**
     * @param path Path to DB.
     * @param cacheName Cache name.
     * @return Rocks database configuration.
     */
    protected RocksDBConfiguration rocksDBConfiguration(String path, String cacheName) {
        return new RocksDBConfiguration(path, cacheName);
    }
}
