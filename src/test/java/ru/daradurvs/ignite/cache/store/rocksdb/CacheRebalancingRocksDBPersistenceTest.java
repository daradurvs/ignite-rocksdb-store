package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.Arrays;
import java.util.Collection;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.daradurvs.ignite.cache.store.rocksdb.common.RocksDBPersistenceAbstractTest;

import static org.junit.Assert.assertEquals;

/**
 * Tests rebalance of cache with enabled RocksDB persistence.
 */
@RunWith(Parameterized.class)
public class CacheRebalancingRocksDBPersistenceTest extends RocksDBPersistenceAbstractTest {
    @Parameterized.Parameter
    public int nodesCount;

    @Parameterized.Parameters(name = "Nodes count = {0}")
    public static Collection<Integer> counts() {
        return Arrays.asList(3, 4, 5, 6);
    }

    /** {@inheritDoc} */
    @Override protected int nodesCount() {
        return nodesCount;
    }

    @Ignore
    @Test
    public void testPartitionedCachePersistence() throws Exception {
        final int entries = 10_000;
        final String prefix = "test";

        final int stopIndex = nodesCount() - 1;

        try {
            Ignite ignite = startIgniteCluster(nodesCount());

            assertEquals(nodesCount(), ignite.cluster().nodes().size());

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            for (int i = 0; i < entries; i++) {
                cache.put(i, prefix + i); // write through
            }

            for (int i = stopIndex; i < nodesCount(); i++)
                Ignition.stop(Integer.toString(i), true);

            cache.rebalance().get(); // waiting for complete of rebalancing
        }
        finally {
            Ignition.stopAll(false);
        }

        try {
            Ignite ignite = startIgniteCluster(stopIndex);

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            assertEquals(stopIndex, ignite.cluster().nodes().size());

            for (int i = 0; i < entries; i++) {
                assertEquals(prefix + i, cache.get(i)); // read through
            }
        }
        finally {
            Ignition.stopAll(false);
        }
    }

    /** {@inheritDoc} */
    @Override protected CacheMode cacheMode() {
        return CacheMode.PARTITIONED;
    }

    /** {@inheritDoc} */
    @Override protected CacheRebalanceMode rebalanceMode() {
        return CacheRebalanceMode.SYNC;
    }

    /** {@inheritDoc} */
    @Override protected long rebalanceDelay() {
        return 0;
    }
}
