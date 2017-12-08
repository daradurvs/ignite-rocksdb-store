package ru.daradurvs.ignite.cache.store.rocksdb.benchmarks;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.yardstick.cache.IgnitePutBenchmark;

/** {@inheritDoc} */
public class IgniteTransactionalPutBenchmark extends IgnitePutBenchmark {
    /** {@inheritDoc} */
    @Override protected IgniteCache<Integer, Object> cache() {
        if (!hasPrepared)
            prepare();

        return ignite().cache("transactional");
    }

    private boolean hasPrepared = false;

    /** Logic shouldn't be moved to 'setUp' method. */
    private void prepare() {
        if (!ignite().active())
            ignite().active(true);

        hasPrepared = true;
    }
}
