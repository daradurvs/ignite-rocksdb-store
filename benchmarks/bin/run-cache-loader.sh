#!/bin/bash

SCRIPT_DIR=$(cd $(dirname "$0"); pwd)

export JAVA_HOME="/gridgain/ssd/vdaradur/jdk1.8.0_152"
export IGNITE_HOME="/gridgain/ssd/vdaradur/codloader_v1/apache-ignite-fabric-2.3.0-bin"
export JVM_OPTS="-server -Xms10g -Xmx30g -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ScavengeBeforeFullGC -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/gridgain/ssd/vdaradur/codloader_v1/heapdump"

CACHE_LOADER_CONFIG="/gridgain/ssd/vdaradur/codloader_v1/config/ignite-rocksdb-syncwrite-cfg.xml"
IGNITE_CONFIG_BEAN_NAME="ignite.cfg"
LOADING_CACHE_NAME="=com.sbt.cdm.api.model.published.instance.PublishedProductRegister"
CP="${IGNITE_HOME}/libs/*:${IGNITE_HOME}/libs/ignite-spring/*:${IGNITE_HOME}/libs/user_libs/*"

${JAVA_HOME}/bin/java ${JVM_OPTS} -DCACHE_LOADER_CONFIG=${CACHE_LOADER_CONFIG} -DIGNITE_CONFIG_BEAN_NAME=${IGNITE_CONFIG_BEAN_NAME} -DLOADING_CACHE_NAME=${LOADING_CACHE_NAME} -cp "${CP}" ru.daradurvs.ignite.cache.store.rocksdb.benchmarks.DummyCacheLoader 
