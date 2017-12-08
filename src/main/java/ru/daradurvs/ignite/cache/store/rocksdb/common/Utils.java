package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.util.UUID;
import org.apache.ignite.Ignite;

public class Utils {
    public static String getConsistentId(Ignite ignite) {
        return String.valueOf(ignite.cluster().localNode().consistentId());
    }
}
