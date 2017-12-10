package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.ignite.internal.util.typedef.X;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.FlushOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * Wrapper around RocksDB instance, provides some useful methods to work with.
 */
public class RocksDBWrapper implements AutoCloseable {
    /** Key - Column family name; Value - Column family handle. */
    private final Map<BytesArrayWrapper, ColumnFamilyHandle> COLUMN_FAMILY_HANDLES = new ConcurrentHashMap<>();

    private Path path;
    private RocksDB db;

    static {
        RocksDB.loadLibrary();
    }

    /**
     * Initialize wrapper around RocksDB database with given path.
     *
     * @param path Path to database.
     * @throws RocksDBException In case of an error.
     */
    RocksDBWrapper(String path, DBOptions dbOptions) throws RocksDBException {
        this.path = Paths.get(path);
        this.db = open(path, dbOptions);
    }

    /**
     * Opens connection to RocksDB database with given path if exists, otherwise create new database.
     *
     * @param pathToDB Path to database.
     * @param dbOptions Database options.
     * @return RocksDB instance.
     * @throws RocksDBException In case of an error.
     */
    protected RocksDB open(String pathToDB, DBOptions dbOptions) throws RocksDBException {
        List<byte[]> columnFamilyNames = getColumnFamilyNames(pathToDB);

        if (columnFamilyNames.isEmpty()) {
            columnFamilyNames.add(RocksDB.DEFAULT_COLUMN_FAMILY);
        }

        List<ColumnFamilyDescriptor> descriptors = new ArrayList<>();

        for (byte[] name : columnFamilyNames) {
            descriptors.add(new ColumnFamilyDescriptor(name));
        }

        List<ColumnFamilyHandle> handles = new ArrayList<>(descriptors.size());

        RocksDB db = RocksDB.open(dbOptions, pathToDB, descriptors, handles);

        X.println("RocksDB[" + path + "] connection has been initialized.");

        assert descriptors.size() == handles.size();

        for (int i = 0; i < descriptors.size(); i++) {
            byte[] name = descriptors.get(i).columnFamilyName();

            COLUMN_FAMILY_HANDLES.put(new BytesArrayWrapper(name), handles.get(i));
        }

        return db;
    }

    /**
     * Returns names of all available column families for a rocksdb database identified by path
     *
     * @param pathToDB Path to database.
     * @return List containing the column family names
     * @throws RocksDBException In case of an error.
     */
    protected List<byte[]> getColumnFamilyNames(String pathToDB) throws RocksDBException {
        Options options = new Options();
        options.setCreateIfMissing(true);

        List<byte[]> names = RocksDB.listColumnFamilies(options, pathToDB);

        if (names.isEmpty())
            names = new ArrayList<>(); // To avoid UnsupportedOperationException

        return names;
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
     * Returns path to database of wrapped RocksDB instance.
     *
     * @return Path to database.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Close connection to db.
     *
     * @throws RocksDBException In case of an error.
     */
    @Override public void close() throws RocksDBException {
        if (db != null) {
            db.flush(new FlushOptions().setWaitForFlush(true));
            db.close();

            X.println("Connection to RocksDB[" + path + "] connection has been closed.");
        }

        COLUMN_FAMILY_HANDLES.clear();
    }

    /**
     * Wrapper around bytes array, which intended using it as key in maps. Provides methods 'equal' and 'hashCode'.
     */
    private static class BytesArrayWrapper {
        byte[] arr;

        /**
         * @param arr Bytes array.
         */
        private BytesArrayWrapper(byte[] arr) {
            this.arr = arr;
        }

        /** {@inheritDoc} */
        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            BytesArrayWrapper wrapper = (BytesArrayWrapper)o;

            return Arrays.equals(arr, wrapper.arr);
        }

        /** {@inheritDoc} */
        @Override public int hashCode() {
            return Arrays.hashCode(arr);
        }
    }
}
