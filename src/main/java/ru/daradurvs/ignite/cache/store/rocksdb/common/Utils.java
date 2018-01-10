package ru.daradurvs.ignite.cache.store.rocksdb.common;

import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.IgniteConfiguration;

public class Utils {
    public static String getConsistentId(Ignite ignite) {
        return String.valueOf(ignite.cluster().localNode().consistentId());
    }

    public static boolean isClientMode(IgniteConfiguration cfg) {
        return cfg.isClientMode() != null && cfg.isClientMode();
    }

    public static boolean isClientMode(Ignite ignite) {
        return ignite.cluster().localNode().isClient();
    }
}
