package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.rocksdb.Options;

// TODO: implement serialization properly
public class RocksDBOptionsExternalizableWrapper implements Externalizable {
    private transient Options options;

    /**
     * For Externalizable interface support.
     */
    public RocksDBOptionsExternalizableWrapper() {
    }

    public RocksDBOptionsExternalizableWrapper(Options options) {
        this.options = options;
    }

    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(options.createIfMissing());
        out.writeBoolean(options.useFsync());
    }

    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        options = new Options();

        options.setCreateIfMissing(in.readBoolean());
        options.setUseFsync(in.readBoolean());
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public Options getOptions() {
        return options;
    }
}
