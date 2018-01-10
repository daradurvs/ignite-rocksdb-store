package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
    private static final int ENTRIES_NUMBER = 10_000;
    private static final String TEST_PREFIX = "test";

    @Parameterized.Parameter
    public int nodesCount;

    @Parameterized.Parameters(name = "Nodes count = {0}")
    public static Collection<Integer> counts() {
        return Arrays.asList(1, 2, 3);
    }

    /** {@inheritDoc} */
    @Override protected int nodesCount() {
        return nodesCount;
    }

    @Test
    public void testPutIntoCachePersistence() throws Exception {
        try {
            Ignite ignite = startIgniteCluster(nodesCount());

            assertEquals(nodesCount(), ignite.cluster().nodes().size());

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            for (int i = 0; i < ENTRIES_NUMBER; i++) {
                cache.put(i, TEST_PREFIX + i); // write through
            }
        }
        finally {
            Ignition.stopAll(false);
        }

        validateData();
    }

    @Test
    public void testPutAllIntoCachePersistence() throws Exception {
        try {
            Ignite ignite = startIgniteCluster(nodesCount());

            assertEquals(nodesCount(), ignite.cluster().nodes().size());

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(TEST_CACHE_NAME);

            for (int i = 0, j = 100; i < ENTRIES_NUMBER; ) {
                Map<Integer, String> data = new HashMap<>();

                for (int k = i + j; i < k && i < ENTRIES_NUMBER; i++) {
                    data.put(i, TEST_PREFIX + i);
                }

                cache.putAll(data); // write through
            }
        }
        finally {
            Ignition.stopAll(false);
        }

        validateData();
    }

    /**
     * @throws Exception In case of an error.
     */
    protected void validateData() throws Exception {
        try {
            Ignite ignite = startIgniteCluster(nodesCount());

            final IgniteCache<Object, Object> cache = ignite.getOrCreateCache(TEST_CACHE_NAME).withSkipStore();

            assertEquals(nodesCount(), ignite.cluster().nodes().size());

            cache.loadCache(null);

            for (int i = 0; i < ENTRIES_NUMBER; i++) {
                assertEquals(TEST_PREFIX + i, cache.get(i)); // read through disabled
            }
        }
        finally {
            Ignition.stopAll(false);
        }
    }


    @Test
    public void testPutIntoCachePersistenceFromClientNode() throws Exception {
        try {
            startIgniteCluster(nodesCount());

            Ignite client = startIgniteCluster(igniteConfiguration("client", true));

            assertEquals(nodesCount() + 1, client.cluster().nodes().size());

            IgniteCache<Integer, String> cache = client.getOrCreateCache(TEST_CACHE_NAME);

            for (int i = 0; i < ENTRIES_NUMBER; i++) {
                cache.put(i, TEST_PREFIX + i); // write through
            }
        }
        finally {
            Ignition.stopAll(false);
        }

        try {
            startIgniteCluster(nodesCount());

            Ignite client = startIgniteCluster(igniteConfiguration("client", true));

            assertEquals(nodesCount() + 1, client.cluster().nodes().size());

            IgniteCache<Integer, String> cache = client.getOrCreateCache(TEST_CACHE_NAME);

            cache.loadCache(null);

            for (int i = 0; i < ENTRIES_NUMBER; i++) {
                assertEquals(TEST_PREFIX + i, cache.get(i)); // read through disabled
            }
        }
        finally {
            Ignition.stopAll(false);
        }
    }
}
