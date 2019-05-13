/*
 * Copyright 2019 Verapi Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.verapi.abyss.cassandra.impl;

import com.verapi.abyss.cassandra.CassandraClient;
import com.verapi.abyss.cassandra.CassandraClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CassandraClientFactoryImpl implements CassandraClientFactory {
    private static Logger logger = LoggerFactory.getLogger(CassandraClientFactoryImpl.class);

    private static CassandraClientFactoryImpl instance = null;
    private Map<String, CassandraClient> cassandraClientMap = null;

    public static CassandraClientFactoryImpl getInstance() {
        if (instance == null) {
            logger.trace("constructing new CassandraClientFactoryImpl");
            instance = new CassandraClientFactoryImpl();
        } else
            logger.trace("using existing CassandraClientFactoryImpl");

        return instance;
    }

    private CassandraClientFactoryImpl() {
        logger.trace("initializing CassandraClientFactoryImpl");
        cassandraClientMap = new HashMap<>();
    }

    public Boolean checkIfExistsCassandraClient(String context) {
        return cassandraClientMap.containsKey(context);
    }

    public CassandraClient getCassandraClient(String context) {
        logger.trace("Cassandra client count:{}", cassandraClientMap.size());
        return cassandraClientMap.get(context);
    }

    public CassandraClient putCassandraClient(String context, CassandraClient cassandraClient) {
        if (cassandraClient == null)
            return null;
        return cassandraClientMap.put(context, cassandraClient);
    }
}
