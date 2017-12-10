package ru.daradurvs.ignite.cache.store.rocksdb.common;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.rocksdb.RocksDBException;

import static org.apache.ignite.lifecycle.LifecycleEventType.BEFORE_NODE_STOP;

/**
 * Responsible for closing connection to RocksDB instance after Ignite node stop.
 */
public class DestructorLifecycleBean implements LifecycleBean {
    /** Auto-inject Ignite instance. */
    @IgniteInstanceResource
    private Ignite ignite;

    /** {@inheritDoc} */
    @Override public void onLifecycleEvent(LifecycleEventType evt) throws IgniteException {
        try {
            if (evt == BEFORE_NODE_STOP)
                RocksDBHolder.close(Utils.getConsistentId(ignite));
        }
        catch (RocksDBException e) {
            throw new IgniteException("Couldn't close RocksDB instances connection.", e);
        }
    }
}
