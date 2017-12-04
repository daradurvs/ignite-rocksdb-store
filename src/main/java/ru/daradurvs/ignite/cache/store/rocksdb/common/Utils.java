package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.util.UUID;
import org.apache.ignite.Ignite;

public class Utils {
    public static UUID getNodeId(Ignite ignite) {
        return ignite.cluster().localNode().id();
    }
}
