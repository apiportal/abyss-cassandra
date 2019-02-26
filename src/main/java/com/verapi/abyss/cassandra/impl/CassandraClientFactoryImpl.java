/*
 *
 *  *  Copyright (C) Verapi Yazilim Teknolojileri A.S. - All Rights Reserved
 *  *
 *  *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  *  Proprietary and confidential
 *  *
 *  *  Written by Halil Özkan <halil.ozkan@verapi.com>, 2 2019
 *
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
