package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * Wrapper around RocksDB instance, provides some useful methods to work with.
 */
public class RocksDBWrapper implements AutoCloseable {
    private final Map<BytesArrayWrapper, ColumnFamilyHandle> COLUMN_FAMILY_HANDLES = new ConcurrentHashMap<>();

    private RocksDB db;

    static {
        RocksDB.loadLibrary();
    }

    /**
     * Initialize wrapper around RocksDB database with given path.
     *
     * @param pathToDB Path to database.
     * @throws RocksDBException In case of an error.
     */
    RocksDBWrapper(String pathToDB) throws RocksDBException {
        db = open(pathToDB);
    }

    /**
     * Opens connection to RocksDB database with given path if exists, otherwise create new database.
     *
     * @param pathToDB Path to database.
     * @return RocksDB instance.
     * @throws RocksDBException In case of an error.
     */
    protected RocksDB open(String pathToDB) throws RocksDBException {
        Options options = new Options().setCreateIfMissing(true);

        List<byte[]> columnFamilyNames = RocksDB.listColumnFamilies(options, pathToDB);

        if (columnFamilyNames.isEmpty()) {
            RocksDB db = RocksDB.open(options, pathToDB);

            COLUMN_FAMILY_HANDLES.put(new BytesArrayWrapper(RocksDB.DEFAULT_COLUMN_FAMILY), db.getDefaultColumnFamily());

            return db;
        }

        List<ColumnFamilyDescriptor> descriptors = new ArrayList<>();

        for (byte[] name : columnFamilyNames) {
            descriptors.add(new ColumnFamilyDescriptor(name));
        }

        List<ColumnFamilyHandle> handles = new ArrayList<>();

        RocksDB db = RocksDB.open(pathToDB, descriptors, handles);

        assert descriptors.size() == handles.size();

        for (int i = 0; i < descriptors.size(); i++) {
            byte[] name = descriptors.get(i).columnFamilyName();

            COLUMN_FAMILY_HANDLES.put(new BytesArrayWrapper(name), handles.get(i));
        }

        return db;
    }

    /**
     * Returns {@link ColumnFamilyHandle} which mapped to given Ignite cache name if exists, otherwise create new column
     * family which will be used as an isolated namespace for keys of Ignite cache with given name.
     *
     * @param cacheName Ignite cache name.
     * @return ColumnFamilyHandle mapped to given Ignite cache name.
     * @throws RocksDBException In case of an error.
     */
    public ColumnFamilyHandle handle(String cacheName) throws RocksDBException {
        byte[] name = cacheName.getBytes();

        BytesArrayWrapper key = new BytesArrayWrapper(name);

        ColumnFamilyHandle handle = COLUMN_FAMILY_HANDLES.get(key);

        if (handle == null) {
            handle = db.createColumnFamily(new ColumnFamilyDescriptor(name));

            COLUMN_FAMILY_HANDLES.put(key, handle);
        }

        return handle;
    }

    /**
     * Returns {@link RocksDB} instance.
     *
     * @return RocksDB instance.
     */
    public RocksDB db() {
        return db;
    }

    /**
     * Close connection to db.
     *
     * @throws RocksDBException In case of an error.
     */
    @Override public void close() throws RocksDBException {
        if (db != null) {
            db.close();
        }

        COLUMN_FAMILY_HANDLES.clear();
    }

    private static class BytesArrayWrapper {
        byte[] arr;

        private BytesArrayWrapper(byte[] arr) {
            this.arr = arr;
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            BytesArrayWrapper wrapper = (BytesArrayWrapper)o;

            return Arrays.equals(arr, wrapper.arr);
        }

        @Override public int hashCode() {
            return Arrays.hashCode(arr);
        }
    }
}
