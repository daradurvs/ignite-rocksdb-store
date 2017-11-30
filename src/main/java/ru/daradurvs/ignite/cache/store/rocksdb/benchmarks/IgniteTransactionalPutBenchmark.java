package ru.daradurvs.ignite.cache.store.rocksdb.benchmarks;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.yardstick.cache.IgnitePutBenchmark;

/** {@inheritDoc} */
public class IgniteTransactionalPutBenchmark extends IgnitePutBenchmark {
    /** {@inheritDoc} */
    @Override protected IgniteCache<Integer, Object> cache() {
        return ignite().cache("transactional");
    }
}
