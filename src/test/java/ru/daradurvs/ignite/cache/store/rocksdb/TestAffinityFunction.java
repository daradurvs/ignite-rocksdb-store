/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.daradurvs.ignite.cache.store.rocksdb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.ignite.cache.affinity.AffinityFunction;
import org.apache.ignite.cache.affinity.AffinityFunctionContext;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.IgniteNodeAttributes;

public class TestAffinityFunction implements AffinityFunction {
    private int partitions;

    public TestAffinityFunction() {
        this(2);
    }

    public TestAffinityFunction(int partitions) {
        this.partitions = partitions;
    }

    @Override public int partitions() {
        return partitions;
    }

    @Override public int partition(Object key) {
        return ((Number)key).intValue() % partitions();
    }

    @Override public List<List<ClusterNode>> assignPartitions(AffinityFunctionContext affCtx) {
        List<List<ClusterNode>> result = new ArrayList<>(partitions());

        for (int i = 0; i < partitions; i++) {
            ClusterNode node = findClusterNode(affCtx.currentTopologySnapshot(), String.valueOf(i));

            if (node == null) {
                node = affCtx.currentTopologySnapshot().get(0);
            }

            List<ClusterNode> nodes = new ArrayList<>();
            nodes.add(node);

            result.add(nodes);
        }

        return result;
    }

    private ClusterNode findClusterNode(List<ClusterNode> nodes, String name) {
        for (ClusterNode node : nodes) {
            if (name.equals(node.attribute(IgniteNodeAttributes.ATTR_IGNITE_INSTANCE_NAME)))
                return node;
        }

        return null;
    }

    @Override public void removeNode(UUID nodeId) {
        // No-op.
    }

    @Override public void reset() {
        // No-op.
    }
}
