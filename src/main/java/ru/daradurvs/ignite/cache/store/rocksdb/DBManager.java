package ru.daradurvs.ignite.cache.store.rocksdb;

import java.io.IOException;
import java.util.Arrays;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

/**
 * @author Vyacheslav Daradur
 * @since 25.10.2017
 */
public class DBManager implements AutoCloseable {
    private static RocksDB db;

    public static RocksDB db() throws RocksDBException {
        if (db == null)
            db = RocksDB.open("c:/TEMP/rocksdb");

        return db;
    }
//
//    public static ColumnFamilyHandle getColumnDescriptor() throws IOException, RocksDBException {
//        final String name = "handlerName";
//
//        ColumnFamilyHandle handle = null;
//
//        byte[] arr = new JavaSerializer().serialize("handlerName");
//
//        return new ColumnFamilyDescriptor();
//    }

    @Override public void close() throws Exception {
        if (db != null)
            db.close();
    }
}
