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

package com.verapi.abyss.cassandra.impl.verticle;

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import com.verapi.abyss.cassandra.common.Cqls;
import com.verapi.abyss.cassandra.impl.messagecodec.MessagePlatformApiLog;
import com.verapi.abyss.cassandra.impl.messagecodec.MessagePlatformApiLogCodec;
import com.verapi.abyss.cassandra.impl.messageconsumer.MessageConsumerApiTrafficLog;
import com.verapi.abyss.cassandra.impl.messageconsumer.MessageConsumerPlatformApiLog;
import com.verapi.abyss.common.Config;
import com.verapi.abyss.common.Constants;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.cassandra.CassandraClient;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CassandraLoggerVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(CassandraLoggerVerticle.class);

    private static List<String> queryList = new ArrayList<>();
    private static Map<String, PreparedStatement> preparedStatementMap = new HashMap<>();
    private CassandraClient cassandraClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        queryList.add(Cqls.CQL_INSERT_PLATFORM_API_LOG);
        queryList.add(Cqls.CQL_INSERT_TRAFFIC_LOG);
        // Register codec for custom message
        //vertx.getDelegate().eventBus().registerDefaultCodec(MessageConsumerPlatformApiLog.class, new MessagePlatformApiLogCodec());
        vertx.eventBus().registerCodec(new MessagePlatformApiLogCodec());

        String[] cassandraContactPoints = Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_CONTACT_POINTS).split(",");
        CassandraClientOptions cassandraClientOptions = new CassandraClientOptions();

        for (String contactPoint : cassandraContactPoints) {
            cassandraClientOptions.addContactPoint(contactPoint);
            logger.info("Cassandra contact point[{}] added", contactPoint);
        }
        cassandraClientOptions.setPort(Config.getInstance().getConfigJsonObject().getInteger(Constants.CASSANDRA_PORT));

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions
                .setConnectionsPerHost(HostDistance.LOCAL, 4, 10)
                .setConnectionsPerHost(HostDistance.REMOTE, 2, 4);

        cassandraClientOptions.dataStaxClusterBuilder()
                .withCredentials(Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_DBUSER_NAME)
                        , Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_DBUSER_PASSWORD))
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withoutJMXReporting()
                .withoutMetrics()
                .withCompression(ProtocolOptions.Compression.LZ4)
                //.withPoolingOptions(poolingOptions)
                .getConfiguration().getCodecRegistry().register(InstantCodec.instance);

        cassandraClient = CassandraClient.createShared(this.vertx, cassandraClientOptions);
        cassandraClient.connect(Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_KEYSPACE), conn -> {
            if (conn.succeeded()) {
                logger.info("Cassandra client connected");
                queryList.forEach(query -> {
                    preparedStatementMap.put(query, null);
                    cassandraClient.prepare(query, prepareResult -> {
                        if (prepareResult.succeeded()) {
                            logger.info("Cassandra client prepared statement for query {}", query);
                            preparedStatementMap.put(query, prepareResult.result());
                        } else {
                            logger.error("Cassandra client is unable to prepare statement for query {}, error: {}", query, prepareResult.cause().getLocalizedMessage());
                        }
                    });
                });
            } else {
                logger.error("Cassandra client is unable to connect, error: {}", conn.cause().getLocalizedMessage());
            }
        });

        vertx.eventBus().<MessagePlatformApiLog>consumer(Constants.EVENTBUS_ADDRESS_PLATFORM_API_LOG)
                .handler(new MessageConsumerPlatformApiLog().logWriter(preparedStatementMap, cassandraClient));

        vertx.eventBus().<JsonObject>consumer(Constants.EVENTBUS_ADDRESS_API_TRAFFIC_LOG)
                .handler(new MessageConsumerApiTrafficLog().logWriter(preparedStatementMap, cassandraClient));

        super.start(startFuture);
    }

}
