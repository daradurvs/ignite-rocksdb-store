#!/usr/bin/env bash

export CACHE_CONFIG_PROVIDER="ru.daradurvs.ignite.cache.store.rocksdb.codloader.RocksDBCacheProvider.java"
export CACHE_CONFIG_BEAN_NAME="rocksdb-cache.cfg"
# export CACHE_CONFIG_BEAN_NAME="transactional-cache.cfg"
export IGNITE_CONFIG_URL="ignite-rocksdb-syncwrite-cfg.xml"

export JVM_OPTS="-server -Xms10g -Xmx30g -XX:+AlwaysPreTouch -XX:+UseG1GC -XX:+ScavengeBeforeFullGC
-XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/gridgain/ssd/vdaradur/heapdump"

TABLE_INDEX=6
COD_DATA_PATH="/path/to/cod_data.zip"

java -jar ignitecod-data-loader-0.0.1-SNAPSHOT.jar ${JVM_OPTS} -DBATCH_SIZE=5000 -DMAX_LINES=10001
-DFILE_NAME=EIP_DBAOSB_DEPOHISTPARAM_1.txt load-table --data-root ${COD_DATA_PATH} --local -ti
${TABLE_INDEX}