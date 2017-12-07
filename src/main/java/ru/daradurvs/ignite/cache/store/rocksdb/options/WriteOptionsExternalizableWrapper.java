package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.rocksdb.WriteOptions;

public class WriteOptionsExternalizableWrapper implements Externalizable {
    private transient WriteOptions writeOptions;

    /**
     * For Externalizable interface support.
     */
    public WriteOptionsExternalizableWrapper() {
    }

    public WriteOptionsExternalizableWrapper(WriteOptions writeOptions) {
        this.writeOptions = writeOptions;
    }

    public void setWriteOptions(WriteOptions writeOptions) {
        this.writeOptions = writeOptions;
    }

    public WriteOptions getWriteOptions() {
        return writeOptions;
    }

    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(writeOptions.disableWAL());
        out.writeBoolean(writeOptions.ignoreMissingColumnFamilies());
        out.writeBoolean(writeOptions.noSlowdown());
        out.writeBoolean(writeOptions.sync());
    }

    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        writeOptions = new WriteOptions();

        writeOptions.setDisableWAL(in.readBoolean());
        writeOptions.setIgnoreMissingColumnFamilies(in.readBoolean());
        writeOptions.setNoSlowdown(in.readBoolean());
        writeOptions.setSync(in.readBoolean());
    }
}
