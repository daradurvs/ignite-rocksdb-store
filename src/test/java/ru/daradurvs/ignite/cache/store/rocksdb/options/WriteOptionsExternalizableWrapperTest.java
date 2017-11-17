package ru.daradurvs.ignite.cache.store.rocksdb.options;

import org.junit.Test;
import org.rocksdb.WriteOptions;

public class WriteOptionsExternalizableWrapperTest extends ExternalizableAbstractTest {
    @Test
    public void testSerialization() throws Exception {
        WriteOptions writeOptions = getWriteOptions(true, true, true, true);

        checkSerialization(writeOptions);
    }

    @Test
    public void testSerialization2() throws Exception {
        WriteOptions writeOptions = getWriteOptions(false, false, false, false);

        checkSerialization(writeOptions);
    }

    @Test
    public void testSerialization3() throws Exception {
        WriteOptions writeOptions = getWriteOptions(false, true, false, true);

        checkSerialization(writeOptions);
    }

    @Test
    public void testSerialization4() throws Exception {
        WriteOptions writeOptions = getWriteOptions(true, false, true, false);

        checkSerialization(writeOptions);
    }

    private void checkSerialization(WriteOptions writeOptions) throws Exception {
        WriteOptionsExternalizableWrapper sut = new WriteOptionsExternalizableWrapper(writeOptions);

        WriteOptionsExternalizableWrapper obj = serializeDeserialize(sut);

        checkEqualityByGetters(sut.getWriteOptions(), obj.getWriteOptions());
    }

    private WriteOptions getWriteOptions(boolean b1, boolean b2, boolean b3, boolean b4) {
        WriteOptions writeOptions = new WriteOptions();

        writeOptions.setSync(b1);
        writeOptions.setNoSlowdown(b2);
        writeOptions.setIgnoreMissingColumnFamilies(b3);
        writeOptions.setDisableWAL(b4);

        return writeOptions;
    }
}
