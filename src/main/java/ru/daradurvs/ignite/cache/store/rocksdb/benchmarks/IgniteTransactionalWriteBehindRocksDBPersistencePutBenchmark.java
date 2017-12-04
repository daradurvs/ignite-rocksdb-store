package ru.daradurvs.ignite.cache.store.rocksdb.benchmarks;

import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

/** {@inheritDoc} */
public class IgniteTransactionalWriteBehindRocksDBPersistencePutBenchmark extends IgniteTransactionalRocksDBPersistencePutBenchmark {
    /** {@inheritDoc} */
    @Override protected CacheConfiguration<Integer, Object> getCacheConfiguration(IgniteConfiguration cfg) {
        CacheConfiguration<Integer, Object> cacheCfg = super.getCacheConfiguration(cfg);
        cacheCfg.setWriteBehindEnabled(true);

        return cacheCfg;
    }
}
