package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.util.Arrays;
import java.util.Collection;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

/**
 * Tests Ignite partitioned cache with enabled RocksDB persistence.
 */
@RunWith(Parameterized.class)
public class CacheRocksDBPersistenceTest extends RocksDBPersistenceAbstractTest {
    @Parameterized.Parameter
    public int nodesCount;

    @Parameterized.Parameters(name = "Nodes count = {0}")
    public static Collection<Integer> counts() {
        return Arrays.asList(1, 2, 3, 4, 5);
    }

    /** {@inheritDoc} */
    @Override protected int nodesCount() {
        return nodesCount;
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

        try {
            Ignite ignite = startIgniteCluster(nodesCount());

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
}
