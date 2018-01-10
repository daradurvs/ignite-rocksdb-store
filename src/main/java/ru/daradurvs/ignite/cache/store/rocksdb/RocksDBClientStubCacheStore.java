package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.Collection;
import java.util.Map;
import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.internal.processors.cache.store.CacheLocalStore;
import org.apache.ignite.lang.IgniteBiInClosure;

/** Stub of {@link RocksDBCacheStore} for Ignite client nodes. */
@CacheLocalStore
public class RocksDBClientStubCacheStore<K, V> extends RocksDBCacheStore<K, V> {
    /** */
    public RocksDBClientStubCacheStore() {
        super();
    }

    /** Stub, shouldn't be called. */
    @Override public void loadCache(IgniteBiInClosure<K, V> clo, Object... args) {
        throwIllegalStateException();
    }

    /** Stub, shouldn't be called. */
    @Override public V load(K key) throws CacheLoaderException {
        throwIllegalStateException();

        return null;
    }

    /** Stub, shouldn't be called. */
    @Override public Map<K, V> loadAll(Iterable<? extends K> keys) {
        throwIllegalStateException();

        return null;
    }

    /** Stub, shouldn't be called. */
    @Override public void write(Cache.Entry<? extends K, ? extends V> entry) throws CacheWriterException {
        throwIllegalStateException();
    }

    /** Stub, shouldn't be called. */
    @Override public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> entries) {
        throwIllegalStateException();
    }

    /** Stub, shouldn't be called. */
    @Override public void delete(Object key) throws CacheWriterException {
        throwIllegalStateException();
    }

    /** Stub, shouldn't be called. */
    @Override public void deleteAll(Collection<?> keys) {
        throwIllegalStateException();
    }

    /** */
    private void throwIllegalStateException() throws IllegalStateException {
        throw new IllegalStateException("Method shouldn't be called from client node.");
    }
}
