package ru.daradurvs.ignite.cache.store.rocksdb.benchmarks;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.yardstick.cache.IgnitePutBenchmark;

/** {@inheritDoc} */
public class IgniteTransactionalPutBenchmark extends IgnitePutBenchmark {
    /** {@inheritDoc} */
    @Override protected IgniteCache<Integer, Object> cache() {
        if (!ignite().active())
            ignite().active(true);

        return ignite().cache("transactional");
    }
}
