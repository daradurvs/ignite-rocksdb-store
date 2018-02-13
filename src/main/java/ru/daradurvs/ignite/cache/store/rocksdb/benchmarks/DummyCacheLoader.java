package ru.daradurvs.ignite.cache.store.rocksdb.benchmarks;

import java.io.File;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgniteComponentType;
import org.apache.ignite.internal.util.spring.IgniteSpringHelper;
import org.apache.ignite.internal.util.typedef.X;

public class DummyCacheLoader {
    public static void main(String[] args) throws Exception {
        IgniteSpringHelper spring = IgniteComponentType.SPRING.create(false);

        String cfgPath = System.getProperty("CACHE_LOADER_CONFIG");
        String beanName = System.getProperty("IGNITE_CONFIG_BEAN_NAME");
        String cacheName = System.getProperty("LOADING_CACHE_NAME");

        IgniteConfiguration cfg = spring.loadBean(new File(cfgPath).toURI().toURL(), beanName);
        cfg.setClientMode(true);

        try (Ignite ignite = Ignition.start(cfg)) {
            IgniteCache cache = ignite.cache(cacheName);


            long start = System.currentTimeMillis();
            cache.loadCache(null);
            long end = System.currentTimeMillis();

            X.printerrln("Cache warmed in " + (end - start) / 1000);
        }
    }
}
