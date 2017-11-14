package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.AffinityFunctionContext;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.IgniteNodeAttributes;

/**
 * Test affinity function.
 */
public class TestAffinityFunction implements AffinityFunction {
    private int partitions;
    private int backups;

    /**
     * @param partitions Number of partitions for one cache.
     * @param backups Number of backup nodes for one partition.
     */
    public TestAffinityFunction(int partitions, int backups) {
        this.partitions = partitions;
        this.backups = backups;
    }

    /** {@inheritDoc} */
    @Override public int partitions() {
        return partitions;
    }

    /** {@inheritDoc} */
    @Override public int partition(Object key) {
        return ((Number)key).intValue() % partitions();
    }

    /** {@inheritDoc} */
    @Override public List<List<ClusterNode>> assignPartitions(AffinityFunctionContext affCtx) {
        List<ClusterNode> sortedNodes = sortNodes(affCtx.currentTopologySnapshot());

        List<List<ClusterNode>> result = new ArrayList<>(partitions());

        for (int i = 0; i < partitions; i++) {
            List<ClusterNode> nodes = new ArrayList<>();

            ClusterNode primaryNode = mapPartitionToNode(i, sortedNodes);
            nodes.add(primaryNode);

            for (int j = 1; j <= backups; j++) {
                ClusterNode backupNode = mapPartitionToNode(i + 1, sortedNodes);
                nodes.add(backupNode);
            }

            result.add(nodes);
        }

        return result;
    }

    /**
     * @param partition Partition number.
     * @param sortedNodes Nodes sorted by name.
     * @return Mapped node.
     */
    private ClusterNode mapPartitionToNode(Integer partition, List<ClusterNode> sortedNodes) {
        if (partition < sortedNodes.size())
            return sortedNodes.get(partition);

        return sortedNodes.get(partition % sortedNodes.size());
    }

    /**
     * @param clusterNodes Nodes for sorting.
     * @return New list which contains sorted nodes by node name.
     */
    private List<ClusterNode> sortNodes(List<ClusterNode> clusterNodes) {
        List<ClusterNode> nodes = new ArrayList<>(clusterNodes);

        nodes.sort(Comparator.comparing(o -> o.<String>attribute(IgniteNodeAttributes.ATTR_IGNITE_INSTANCE_NAME)));

        return nodes;
    }

    /** {@inheritDoc} */
    @Override public void removeNode(UUID nodeId) {
        // No-op.
    }

    /** {@inheritDoc} */
    @Override public void reset() {
        // No-op.
    }
}
