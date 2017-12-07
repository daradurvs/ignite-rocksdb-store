package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.rocksdb.Options;
import org.rocksdb.ReadOptions;
import org.rocksdb.WriteOptions;

public class RocksDBConfiguration implements Serializable {
    private String pathToDB;
    private String cacheName;

    private RocksDBOptionsExternalizableWrapper dbOptions;
    private WriteOptionsExternalizableWrapper writeOptions;
    private ReadOptionsExternalizableWrapper readOptions;

    public RocksDBConfiguration() {
    }

    public RocksDBConfiguration(@NotNull String pathToDB, @NotNull String cacheName) {
        this(pathToDB, cacheName, new Options().setCreateIfMissing(true));
    }

    public RocksDBConfiguration(@NotNull String pathToDB, @NotNull String cacheName, @NotNull Options options) {
        this(pathToDB, cacheName, options, new WriteOptions(), new ReadOptions());
    }

    public RocksDBConfiguration(@NotNull String pathToDB, @NotNull String cacheName,
        @NotNull Options dbOptions, @NotNull WriteOptions writeOptions, @NotNull ReadOptions readOptions) {
        this.pathToDB = pathToDB;
        this.cacheName = cacheName;
        this.dbOptions = new RocksDBOptionsExternalizableWrapper(dbOptions);
        this.writeOptions = new WriteOptionsExternalizableWrapper(writeOptions);
        this.readOptions = new ReadOptionsExternalizableWrapper(readOptions);
    }

    public void setPathToDB(String pathToDB) {
        this.pathToDB = pathToDB;
    }

    public String getPathToDB() {
        return pathToDB;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setDbOptions(RocksDBOptionsExternalizableWrapper dbOptions) {
        this.dbOptions = dbOptions;
    }

    public Options getDbOptions() {
        return dbOptions.getOptions();
    }

    public void setWriteOptions(WriteOptionsExternalizableWrapper writeOptions) {
        this.writeOptions = writeOptions;
    }

    public WriteOptions getWriteOptions() {
        return writeOptions.getWriteOptions();
    }

    public void setReadOptions(ReadOptionsExternalizableWrapper readOptions) {
        this.readOptions = readOptions;
    }

    public ReadOptions getReadOptions() {
        return readOptions.getReadOptions();
    }
}
