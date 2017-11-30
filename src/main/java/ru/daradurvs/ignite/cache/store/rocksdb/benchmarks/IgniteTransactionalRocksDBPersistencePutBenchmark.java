package ru.daradurvs.ignite.cache.store.rocksdb.benchmarks;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.yardstick.cache.IgnitePutBenchmark;
import org.yardstickframework.BenchmarkConfiguration;
import ru.daradurvs.ignite.cache.store.rocksdb.RocksDBCacheStoreFactory;
import ru.daradurvs.ignite.cache.store.rocksdb.common.Utils;
import ru.daradurvs.ignite.cache.store.rocksdb.options.RocksDBConfiguration;

/** {@inheritDoc} */
public class IgniteTransactionalRocksDBPersistencePutBenchmark extends IgnitePutBenchmark {
    protected static final String BENCHMARK_CACHE_NAME = "transactional-rocks";

    protected Path tempPath;

    private boolean hasPrepared = false;

    /** {@inheritDoc} */
    @Override protected IgniteCache<Integer, Object> cache() {
        if (!hasPrepared)
            prepareCache();

        return ignite().cache(BENCHMARK_CACHE_NAME);
    }

    /** Following logic shouldn't be moved to setUp method. */
    private void prepareCache() {
        if (tempPath == null)
            tempPath = Paths.get(System.getProperty("java.io.tmpdir"), (getClass().getSimpleName() + "_" + System.currentTimeMillis()));

        CacheConfiguration<Integer, Object> cacheCfg = getCacheConfiguration(ignite().configuration());

        ignite().createCache(cacheCfg);

        hasPrepared = true;
    }

    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);
    }

    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        super.tearDown();

        U.delete(tempPath.toFile());
    }

    protected CacheConfiguration<Integer, Object> getCacheConfiguration(IgniteConfiguration cfg) {
        CacheConfiguration<Integer, Object> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
        cacheCfg.setBackups(1);
        cacheCfg.setName(BENCHMARK_CACHE_NAME);
        cacheCfg.setWriteThrough(true);
        cacheCfg.setReadThrough(true);
        cacheCfg.setRebalanceMode(CacheRebalanceMode.SYNC);
        cacheCfg.setRebalanceDelay(10_000);

        // Emulating separate machines which store data in independent stores.
        Path path = Paths.get(tempPath.toString(), Utils.getNodeId(ignite()).toString());

        if (!tempPath.toFile().exists())
            tempPath.toFile().mkdir();

        RocksDBConfiguration rocksCfg = new RocksDBConfiguration(path.toString(), BENCHMARK_CACHE_NAME);

        RocksDBCacheStoreFactory<Integer, Object> factory = new RocksDBCacheStoreFactory<>(rocksCfg, cfg);

        cacheCfg.setCacheStoreFactory(factory);

        return cacheCfg;
    }
}
