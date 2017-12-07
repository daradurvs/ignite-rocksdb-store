package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.rocksdb.ReadOptions;
import org.rocksdb.ReadTier;

public class ReadOptionsExternalizableWrapper implements Externalizable {
    private transient ReadOptions readOptions;

    /**
     * For Externalizable interface support.
     */
    public ReadOptionsExternalizableWrapper() {
    }

    public ReadOptionsExternalizableWrapper(ReadOptions readOptions) {
        this.readOptions = readOptions;
    }

    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(readOptions.readaheadSize());
        out.writeByte(readOptions.readTier().getValue());
        out.writeBoolean(readOptions.backgroundPurgeOnIteratorCleanup());
        out.writeBoolean(readOptions.fillCache());
        out.writeBoolean(readOptions.ignoreRangeDeletions());
        out.writeBoolean(readOptions.managed());
        out.writeBoolean(readOptions.pinData());
        out.writeBoolean(readOptions.prefixSameAsStart());
        out.writeBoolean(readOptions.tailing());
        out.writeBoolean(readOptions.totalOrderSeek());
        out.writeBoolean(readOptions.verifyChecksums());
    }

    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        readOptions = new ReadOptions();

        readOptions.setReadaheadSize(in.readLong());
        readOptions.setReadTier(ReadTier.getReadTier(in.readByte()));
        readOptions.setBackgroundPurgeOnIteratorCleanup(in.readBoolean());
        readOptions.setFillCache(in.readBoolean());
        readOptions.setIgnoreRangeDeletions(in.readBoolean());
        readOptions.setManaged(in.readBoolean());
        readOptions.setPinData(in.readBoolean());
        readOptions.setPrefixSameAsStart(in.readBoolean());
        readOptions.setTailing(in.readBoolean());
        readOptions.setTotalOrderSeek(in.readBoolean());
        readOptions.setVerifyChecksums(in.readBoolean());
    }

    public void setReadOptions(ReadOptions readOptions) {
        this.readOptions = readOptions;
    }

    public ReadOptions getReadOptions() {
        return readOptions;
    }
}
