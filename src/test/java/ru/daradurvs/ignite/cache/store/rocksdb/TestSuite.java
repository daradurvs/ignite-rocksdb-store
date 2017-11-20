package ru.daradurvs.ignite.cache.store.rocksdb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    PartitionedCacheRocksDBPersistenceTest.class,
    ReplicatedCacheRocksDBPersistenceTest.class,
    RocksDBPersistenceTest.class})
public class TestSuite {
}
