package ru.daradurvs.ignite.cache.store.rocksdb.codloader;

import java.io.File;
import java.io.IOException;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteComponentType;
import org.apache.ignite.internal.util.spring.IgniteSpringHelper;
import org.apache.ignite.internal.util.typedef.X;
import ru.sbrf.gg.load.CacheConfigurationProvider;

public class RocksDBCacheProvider<K, V> implements CacheConfigurationProvider<K, V> {
    @SuppressWarnings("unchecked")
    @Override public CacheConfiguration<K, V> create(String s) {
        CacheConfiguration cacheCfg = null;

        try {
            IgniteSpringHelper spring = IgniteComponentType.SPRING.create(false);

            String beanName = System.getProperty("CACHE_CONFIG_BEAN_NAME");
            String cfgPath = System.getProperty("IGNITE_CONFIG_URL");

            cacheCfg = spring.loadBean(new File(cfgPath).toURI().toURL(), beanName);

            assert cacheCfg != null;

            cacheCfg.setName(s);
        }
        catch (IgniteCheckedException | IOException e) {
            X.printerrln(e.getMessage(), e);
        }

        return cacheCfg;
    }
}
