package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final Map<byte[], ColumnFamilyHandle> COLUMN_FAMILY_HANDLES = new HashMap<>();

    public static void main(String[] args) throws Exception {
        try (Options options = new Options()) {
            options.setCreateIfMissing(true);

            List<byte[]> columnFamilyNames = RocksDB.listColumnFamilies(options, PATH_TO_DATABASE);

            List<ColumnFamilyDescriptor> descriptors = new ArrayList<>();

            for (byte[] name : columnFamilyNames) {
                descriptors.add(new ColumnFamilyDescriptor(name));
            }

            List<ColumnFamilyHandle> handles = new ArrayList<>();

            try (RocksDB db = RocksDB.open(PATH_TO_DATABASE, descriptors, handles)) {
                assert descriptors.size() == handles.size();

                for (int i = 0; i < descriptors.size(); i++) {
                    byte[] name = descriptors.get(i).columnFamilyName();

                    COLUMN_FAMILY_HANDLES.put(name, handles.get(i));
                }

                for (byte[] bytes : COLUMN_FAMILY_HANDLES.keySet()) {
                    System.out.println(new String(bytes));
                    System.out.println(COLUMN_FAMILY_HANDLES.get(bytes));
                }
            }
        }
    }



    private static RocksDB db;

    public static RocksDB db() throws RocksDBException {
        if (db == null)
            db = RocksDB.open("c:/TEMP/rocksdb");

        return db;
    }

    @Override public void close() throws Exception {
        if (db != null)
            db.close();
    }
}
