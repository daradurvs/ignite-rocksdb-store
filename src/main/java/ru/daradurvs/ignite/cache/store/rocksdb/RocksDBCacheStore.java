package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.internal.processors.cache.store.CacheLocalStore;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;
import ru.daradurvs.ignite.cache.store.rocksdb.serializer.JavaSerializer;
import ru.daradurvs.ignite.cache.store.rocksdb.serializer.Serialiazer;

/** {@inheritDoc} */
@CacheLocalStore
public class RocksDBCacheStore<K, V> extends CacheStoreAdapter<K, V> {
    private final RocksDB db;
    private final ColumnFamilyHandle handle;
    private final WriteOptions writeOptions;
    private final ReadOptions readOptions;
    private final Serialiazer serializer;

    protected RocksDBCacheStore() {
        this.db = null;
        this.handle = null;
        this.writeOptions = null;
        this.readOptions = null;
        this.serializer = null;
    }

    public RocksDBCacheStore(@NotNull RocksDB db, @NotNull ColumnFamilyHandle handle) {
        this(db, handle, new JavaSerializer());
    }

    public RocksDBCacheStore(@NotNull RocksDB db, @NotNull ColumnFamilyHandle handle,
        @NotNull Serialiazer serialiazer) {
        this(db, handle, new WriteOptions(), new ReadOptions(), serialiazer);
    }

    public RocksDBCacheStore(@NotNull RocksDB db, @NotNull ColumnFamilyHandle handle,
        @NotNull WriteOptions writeOptions, @NotNull ReadOptions readOptions, @NotNull Serialiazer serializer) {
        this.db = db;
        this.handle = handle;
        this.writeOptions = writeOptions;
        this.readOptions = readOptions;
        this.serializer = serializer;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public void loadCache(IgniteBiInClosure<K, V> clo, Object... args) {
        RocksIterator iter = db.newIterator(handle, readOptions);

        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            clo.apply(
                (K)serializer.deserialize(iter.key()),
                (V)serializer.deserialize(iter.value())
            );
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public Map<K, V> loadAll(Iterable<? extends K> keys) {
        assert keys != null;

        try {
            List<byte[]> preparedKeys = new ArrayList<>();
            List<ColumnFamilyHandle> handles = new ArrayList<>();

            for (K key : keys) {
                preparedKeys.add(serializer.serialize(key));

                handles.add(handle);
            }

            Map<byte[], byte[]> loaded = db.multiGet(
                readOptions,
                handles,
                preparedKeys
            );

            Map<Object, Object> result = new HashMap<>();

            for (Map.Entry<byte[], byte[]> entry : loaded.entrySet()) {
                result.put(
                    serializer.deserialize(entry.getKey()),
                    serializer.deserialize(entry.getValue())
                );
            }

            return (Map<K, V>)result;
        }
        catch (RocksDBException e) {
            throw new CacheLoaderException("Couldn't execute multiGet operation.", e);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public V load(K key) throws CacheLoaderException {
        try {
            byte[] arr = db.get(handle, readOptions, serializer.serialize(key));

            if (arr == null)
                return null;

            return (V)serializer.deserialize(arr);
        }
        catch (RocksDBException e) {
            throw new CacheLoaderException("Couldn't load a value for the key: " + key, e);
        }
    }

    /** {@inheritDoc} */
    @Override public void write(Cache.Entry<? extends K, ? extends V> entry) throws CacheWriterException {
        try {
            db.put(handle,
                writeOptions,
                serializer.serialize(entry.getKey()),
                serializer.serialize(entry.getValue())
            );
        }
        catch (RocksDBException e) {
            throw new CacheWriterException("Couldn't put entry, [key: " + entry.getKey() + "; value: " + entry.getValue() + "]", e);
        }
    }

    /** {@inheritDoc} */
    @Override public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> entries) {
        try {
            WriteBatch batch = new WriteBatch();

            for (Cache.Entry<? extends K, ? extends V> entry : entries) {
                batch.put(handle,
                    serializer.serialize(entry.getKey()),
                    serializer.serialize(entry.getValue())
                );
            }

            db.write(writeOptions, batch);
        }
        catch (RocksDBException e) {
            throw new CacheWriterException("Couldn't execute batch writing operation. [Entries number: " + entries.size() + "]", e);
        }
    }

    /** {@inheritDoc} */
    @Override public void delete(Object key) throws CacheWriterException {
        try {
            db.delete(handle, writeOptions, serializer.serialize(key));
        }
        catch (RocksDBException e) {
            throw new CacheWriterException("Couldn't delete a value for the key: " + key, e);
        }
    }

    /** {@inheritDoc} */
    @Override public void deleteAll(Collection<?> keys) {
        try {
            WriteBatch batch = new WriteBatch();

            for (Object key : keys) {
                batch.remove(
                    handle,
                    serializer.serialize(key)
                );
            }

            db.write(writeOptions, batch);
        }
        catch (RocksDBException e) {
            throw new CacheWriterException("Couldn't execute batch deleting operation. [Keys number: " + keys.size() + "]", e);
        }
    }
}
