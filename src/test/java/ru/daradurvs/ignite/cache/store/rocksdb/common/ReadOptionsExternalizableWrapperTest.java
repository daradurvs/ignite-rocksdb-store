package ru.daradurvs.ignite.cache.store.rocksdb.common;

import org.junit.Test;
import org.rocksdb.ReadOptions;
import org.rocksdb.ReadTier;

public class ReadOptionsExternalizableWrapperTest extends ExternalizableAbstractTest {
    @Test
    public void testSerialization() throws Exception {
        ReadOptions sut = getReadOptions(0, ReadTier.READ_ALL_TIER, true, true, true, true, true, true, true, true, true);

        checkSerialization(sut);
    }

    @Test
    public void testSerialization2() throws Exception {
        ReadOptions sut = getReadOptions(1, ReadTier.PERSISTED_TIER, false, false, false, false, false, false, false, false, false);

        checkSerialization(sut);
    }

    @Test
    public void testSerialization3() throws Exception {
        ReadOptions sut = getReadOptions(2, ReadTier.BLOCK_CACHE_TIER, true, false, true, false, true, false, true, false, true);

        checkSerialization(sut);
    }

    @Test
    public void testSerialization4() throws Exception {
        ReadOptions sut = getReadOptions(3, ReadTier.PERSISTED_TIER, false, true, false, true, false, true, false, true, false);

        checkSerialization(sut);
    }

    private void checkSerialization(ReadOptions readOptions) throws Exception {
        ReadOptionsExternalizableWrapper sut = new ReadOptionsExternalizableWrapper(readOptions);

        ReadOptionsExternalizableWrapper obj = serializeDeserialize(sut);

        checkEqualityByGetters(sut.getReadOptions(), obj.getReadOptions());
    }

    private ReadOptions getReadOptions(long readaheadSize, ReadTier readTier, boolean... b) {
        ReadOptions readOptions = new ReadOptions();

        readOptions.setReadaheadSize(readaheadSize);
        readOptions.setReadTier(readTier);
        readOptions.setBackgroundPurgeOnIteratorCleanup(b[0]);
        readOptions.setFillCache(b[1]);
        readOptions.setIgnoreRangeDeletions(b[2]);
        readOptions.setManaged(b[3]);
        readOptions.setPinData(b[4]);
        readOptions.setPrefixSameAsStart(b[5]);
        readOptions.setTailing(b[6]);
        readOptions.setTotalOrderSeek(b[7]);
        readOptions.setVerifyChecksums(b[8]);

        return readOptions;
    }
}
