package com.verapi.abyss.cassandra.impl;

import com.verapi.abyss.cassandra.CassandraClient;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraClientImpl implements CassandraClient {

    private static Logger logger = LoggerFactory.getLogger(CassandraClientImpl.class);

    private static CassandraClientImpl instance = null;

    /**
     * returns a singleton CassandraService instance
     *
     * @param routingContext Vertx instance is associated with this routing context, it is required for {@link io.vertx.cassandra.CassandraClient#createShared(Vertx, CassandraClientOptions)}
     */
    public static CassandraClientImpl getInstance(RoutingContext routingContext) {
        if ((instance == null) && (routingContext != null))
            instance = new CassandraClientImpl(routingContext);
        return instance;
    }

    /**
     * Construct new singleton CassandraService instance
     *
     * @param routingContext Vertx instance is associated with this routing context, it is required for {@link io.vertx.cassandra.CassandraClient#createShared(Vertx, CassandraClientOptions)}
     */


    private CassandraClientImpl(RoutingContext routingContext) {
        logger.info("initializing Cassandra Service");
    }

}
