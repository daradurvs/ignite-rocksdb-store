package ru.daradurvs.ignite.cache.store.rocksdb.options;

import org.junit.Test;
import org.rocksdb.AccessHint;
import org.rocksdb.DBOptions;

public class DBOptionsExternalizableWrapperTest extends ExternalizableAbstractTest {
    @Test
    public void testSerialization() throws Exception {
        DBOptions sut = getDBOptions(
            new String[] {""},
            new byte[] {0},
            new int[] {1, 2, 3, 4},
            new long[] {1, 2, 3, 4, 5, 6, 7},
            false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
        );

        checkSerialization(sut);
    }

    @Test
    public void testSerialization2() throws Exception {
        DBOptions sut = getDBOptions(
            new String[] {"1"},
            new byte[] {1},
            new int[] {2, 3, 1, 0},
            new long[] {6, 5, 4, 3, 2, 1, 0},
            true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
        );

        checkSerialization(sut);
    }

    @Test
    public void testSerialization3() throws Exception {
        DBOptions sut = getDBOptions(
            new String[] {"2"},
            new byte[] {2},
            new int[] {5, 6, 7, 8},
            new long[] {16, 15, 14, 13, 12, 11, 10},
            false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true
        );

        checkSerialization(sut);
    }

    @Test
    public void testSerialization4() throws Exception {
        DBOptions sut = getDBOptions(
            new String[] {"3"},
            new byte[] {3},
            new int[] {8, 7, 6, 5},
            new long[] {61, 51, 41, 31, 21, 11, 20},
            true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false
        );

        checkSerialization(sut);
    }

    private void checkSerialization(DBOptions dbOptions) throws Exception {
        DBOptionsExternalizableWrapper sut = new DBOptionsExternalizableWrapper(dbOptions);

        DBOptionsExternalizableWrapper obj = serializeDeserialize(sut);

        checkEqualityByGetters(sut.getDbOptions(), obj.getDbOptions());
    }

    private DBOptions getDBOptions(String[] s, byte[] bytes, int[] i, long[] lo, boolean... b) {
        DBOptions dbOptions = new DBOptions();

        dbOptions.setAdviseRandomOnOpen(b[0]);
        dbOptions.setAllow2pc(b[1]);
        dbOptions.setAllowConcurrentMemtableWrite(b[2]);
        dbOptions.setAllowFAllocate(b[3]);
        dbOptions.setAllowMmapReads(b[4]);
        dbOptions.setAllowMmapWrites(b[5]);
        dbOptions.setAvoidFlushDuringRecovery(b[6]);
        dbOptions.setAvoidFlushDuringShutdown(b[7]);
        dbOptions.setAccessHintOnCompactionStart(AccessHint.getAccessHint(bytes[0]));
        dbOptions.setBaseBackgroundCompactions(i[0]);
        dbOptions.setBytesPerSync(lo[0]);
        dbOptions.setCreateIfMissing(b[8]);
        dbOptions.setCreateMissingColumnFamilies(b[9]);
        dbOptions.setCompactionReadaheadSize(lo[1]);
        dbOptions.setDbLogDir(s[0]);
        dbOptions.setDbWriteBufferSize(lo[2]);
        dbOptions.setDelayedWriteRate(lo[3]);
        dbOptions.setDeleteObsoleteFilesPeriodMicros(lo[4]);
        dbOptions.setDumpMallocStats(b[9]);
        dbOptions.setEnableThreadTracking(b[10]);
        dbOptions.setEnableWriteThreadAdaptiveYield(b[11]);
        dbOptions.setErrorIfExists(b[12]);
        dbOptions.setFailIfOptionsFileError(b[13]);
        dbOptions.setIsFdCloseOnExec(b[14]);
        dbOptions.setKeepLogFileNum(lo[4]);
        dbOptions.setLogFileTimeToRoll(lo[5]);
        dbOptions.setManifestPreallocationSize(lo[6]);

        dbOptions.setMaxBackgroundCompactions(i[1]);
        dbOptions.setMaxBackgroundFlushes(i[2]);
        dbOptions.setMaxFileOpeningThreads(i[3]);

        dbOptions.setUseFsync(b[15]);

        return dbOptions;
    }
}
