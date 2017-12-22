package ru.daradurvs.ignite.cache.store.rocksdb.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.rocksdb.AccessHint;
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

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(dbOptions.adviseRandomOnOpen());
        out.writeBoolean(dbOptions.allow2pc());
        out.writeBoolean(dbOptions.allowConcurrentMemtableWrite());
        out.writeBoolean(dbOptions.allowFAllocate());
        out.writeBoolean(dbOptions.allowMmapReads());
        out.writeBoolean(dbOptions.allowMmapWrites());
        out.writeBoolean(dbOptions.avoidFlushDuringRecovery());
        out.writeBoolean(dbOptions.avoidFlushDuringShutdown());
        out.writeByte(dbOptions.accessHintOnCompactionStart().getValue());
        out.writeInt(dbOptions.baseBackgroundCompactions());
        out.writeLong(dbOptions.bytesPerSync());
        out.writeBoolean(dbOptions.createIfMissing());
        out.writeBoolean(dbOptions.createMissingColumnFamilies());
        out.writeLong(dbOptions.compactionReadaheadSize());
        out.writeBytes(dbOptions.dbLogDir());
        out.writeLong(dbOptions.dbWriteBufferSize());
        out.writeLong(dbOptions.delayedWriteRate());
        out.writeLong(dbOptions.deleteObsoleteFilesPeriodMicros());
        out.writeBoolean(dbOptions.dumpMallocStats());
        out.writeBoolean(dbOptions.enableThreadTracking());
        out.writeBoolean(dbOptions.enableWriteThreadAdaptiveYield());
        out.writeBoolean(dbOptions.errorIfExists());
        out.writeBoolean(dbOptions.failIfOptionsFileError());
        out.writeBoolean(dbOptions.isFdCloseOnExec());
        out.writeLong(dbOptions.keepLogFileNum());
        out.writeLong(dbOptions.logFileTimeToRoll());
        out.writeLong(dbOptions.manifestPreallocationSize());

        out.writeInt(dbOptions.maxBackgroundCompactions());
        out.writeInt(dbOptions.maxBackgroundFlushes());
        out.writeInt(dbOptions.maxFileOpeningThreads());

        out.writeBoolean(dbOptions.useFsync());
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        dbOptions = new DBOptions();

        dbOptions.setAdviseRandomOnOpen(in.readBoolean());
        dbOptions.setAllow2pc(in.readBoolean());
        dbOptions.setAllowConcurrentMemtableWrite(in.readBoolean());
        dbOptions.setAllowFAllocate(in.readBoolean());
        dbOptions.setAllowMmapReads(in.readBoolean());
        dbOptions.setAllowMmapWrites(in.readBoolean());
        dbOptions.setAvoidFlushDuringRecovery(in.readBoolean());
        dbOptions.setAvoidFlushDuringShutdown(in.readBoolean());
        dbOptions.setAccessHintOnCompactionStart(AccessHint.getAccessHint(in.readByte()));
        dbOptions.setBaseBackgroundCompactions(in.readInt());
        dbOptions.setBytesPerSync(in.readLong());
        dbOptions.setCreateIfMissing(in.readBoolean());
        dbOptions.setCreateMissingColumnFamilies(in.readBoolean());
        dbOptions.setCompactionReadaheadSize(in.readLong());
        dbOptions.setDbLogDir(in.readLine());
        dbOptions.setDbWriteBufferSize(in.readLong());
        dbOptions.setDelayedWriteRate(in.readLong());
        dbOptions.setDeleteObsoleteFilesPeriodMicros(in.readLong());
        dbOptions.setDumpMallocStats(in.readBoolean());
        dbOptions.setEnableThreadTracking(in.readBoolean());
        dbOptions.setEnableWriteThreadAdaptiveYield(in.readBoolean());
        dbOptions.setErrorIfExists(in.readBoolean());
        dbOptions.setFailIfOptionsFileError(in.readBoolean());
        dbOptions.setIsFdCloseOnExec(in.readBoolean());
        dbOptions.setKeepLogFileNum(in.readLong());
        dbOptions.setLogFileTimeToRoll(in.readLong());
        dbOptions.setManifestPreallocationSize(in.readLong());

        dbOptions.setMaxBackgroundCompactions(in.readInt());
        dbOptions.setMaxBackgroundFlushes(in.readInt());
        dbOptions.setMaxFileOpeningThreads(in.readInt());

        dbOptions.setUseFsync(in.readBoolean());
    }

    public void setDbOptions(DBOptions dbOptions) {
        this.dbOptions = dbOptions;
    }

    public DBOptions getDbOptions() {
        return dbOptions;
    }
}
