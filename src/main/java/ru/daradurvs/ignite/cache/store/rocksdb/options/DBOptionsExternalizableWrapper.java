package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.rocksdb.DBOptions;

// TODO: implement serialization properly
public class DBOptionsExternalizableWrapper implements Externalizable {
    private transient DBOptions dbOptions;

    /**
     * For Externalizable interface support.
     */
    public DBOptionsExternalizableWrapper() {
    }

    public DBOptionsExternalizableWrapper(DBOptions dbOptions) {
        this.dbOptions = dbOptions;
    }

    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(dbOptions.createIfMissing());
        out.writeBoolean(dbOptions.useFsync());
    }

    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        dbOptions = new DBOptions();

        dbOptions.setCreateIfMissing(in.readBoolean());
        dbOptions.setUseFsync(in.readBoolean());
    }

    public void setDbOptions(DBOptions dbOptions) {
        this.dbOptions = dbOptions;
    }

    public DBOptions getDbOptions() {
        return dbOptions;
    }
}
