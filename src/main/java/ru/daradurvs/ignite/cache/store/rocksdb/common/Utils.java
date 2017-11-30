package ru.daradurvs.ignite.cache.store.rocksdb.common;

import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.internal.IgniteKernal;

public class Utils {
    public static UUID getNodeId(Ignite ignite) {
        return ((IgniteKernal)ignite).getLocalNodeId();
    }
}
