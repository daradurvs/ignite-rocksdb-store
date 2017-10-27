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
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
public class DBManager implements AutoCloseable {
    private static final String PATH_TO_DATABASE = "c:/TEMP/rocksdb";
    private static final Map<BytesArrayWrapper, ColumnFamilyHandle> COLUMN_FAMILY_HANDLES = new ConcurrentHashMap<>();

    private static RocksDB db;

    public static RocksDB db() throws RocksDBException {
        if (db == null)
            db = open();

        return db;
    }

    protected static RocksDB open() throws RocksDBException {
        Options options = new Options().setCreateIfMissing(true);

        List<byte[]> columnFamilyNames = RocksDB.listColumnFamilies(options, PATH_TO_DATABASE);

        List<ColumnFamilyDescriptor> descriptors = new ArrayList<>();

        for (byte[] name : columnFamilyNames) {
            descriptors.add(new ColumnFamilyDescriptor(name));
        }

        List<ColumnFamilyHandle> handles = new ArrayList<>();

        RocksDB db = RocksDB.open(PATH_TO_DATABASE, descriptors, handles);

        assert descriptors.size() == handles.size();

        for (int i = 0; i < descriptors.size(); i++) {
            byte[] name = descriptors.get(i).columnFamilyName();

            COLUMN_FAMILY_HANDLES.put(new BytesArrayWrapper(name), handles.get(i));
        }

        return db;
    }

    public static ColumnFamilyHandle initColumnFamilyHandle(String cacheName) throws RocksDBException {
        byte[] name = cacheName.getBytes();

        BytesArrayWrapper key = new BytesArrayWrapper(name);

        ColumnFamilyHandle handle = COLUMN_FAMILY_HANDLES.get(key);

        if (handle == null) {
            handle = db().createColumnFamily(new ColumnFamilyDescriptor(name));

            COLUMN_FAMILY_HANDLES.put(key, handle);
        }

        return handle;
    }

    @Override public void close() throws RocksDBException {
        if (db != null) {
            db.close();
            COLUMN_FAMILY_HANDLES.clear();
            db = null;
        }
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
