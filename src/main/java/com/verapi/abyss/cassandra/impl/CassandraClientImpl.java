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

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.extras.codecs.jdk8.InstantCodec;
import com.verapi.abyss.cassandra.CassandraClient;
import com.verapi.abyss.common.Config;
import com.verapi.abyss.common.Constants;
import io.reactivex.Single;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CassandraClientImpl implements CassandraClient {

    private static Logger logger = LoggerFactory.getLogger(CassandraClientImpl.class);

    //    private static CassandraClientImpl instance = null;
    private RoutingContext routingContext;
    private Vertx vertx;
    private CassandraClientOptions cassandraClientOptions = new CassandraClientOptions();
    private String cassandraKeySpace;
    private io.vertx.reactivex.cassandra.CassandraClient cassandraClient;
    private CompletableFuture<io.vertx.reactivex.cassandra.CassandraClient> cassandraClientCompletableFuture = new CompletableFuture<>();
    //    private Map<String, CompletableFuture<PreparedStatement>> preparedStatementMap;
    private Map<String, PreparedStatement> preparedStatementMap;
    private Boolean initAlreadyRun = false;

    private Boolean setCassandraClientOptionsWithAbyssDefaultParametersAlreadyRun = false;

    private RoutingContext getRoutingContext() {
        return routingContext;
    }

    private void setRoutingContext(RoutingContext routingContext) {
        this.routingContext = routingContext;
        setVertx(routingContext.vertx());
    }

    private Vertx getVertx() {
        return vertx;
    }

    private void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    private io.vertx.reactivex.cassandra.CassandraClient getCassandraClient() {
        return cassandraClient;
    }

    private void setCassandraClient(io.vertx.reactivex.cassandra.CassandraClient cassandraClient) {
        this.cassandraClient = cassandraClient;
    }

    private String getCassandraKeySpace() {
        return cassandraKeySpace;
    }

    private void setCassandraKeySpace(String cassandraKeySpace) {
        this.cassandraKeySpace = cassandraKeySpace;
    }
    /*

     */
/**
 * use this method if a routing context instance exists
 *
 * @param routingContext routing context
 * @return a singleton {@link com.verapi.abyss.cassandra.CassandraClient}
 *//*

    public static CassandraClientImpl getInstance(RoutingContext routingContext) {
        if ((instance == null) && (routingContext != null))
            instance = new CassandraClientImpl(routingContext);
        return instance;
    }

    */
/**
 * use this method if a routing context instance does <b>not</b> exist yet
 *
 * @param vertx Vertx instance
 * @return a singleton {@link com.verapi.abyss.cassandra.CassandraClient}
 *//*

    public static CassandraClientImpl getInstance(Vertx vertx) {
        if ((instance == null) && (vertx != null))
            instance = new CassandraClientImpl(vertx);
        return instance;
    }
*/

    /**
     * Construct new singleton CassandraClient instance
     *
     * @param routingContext routing context
     */


    public CassandraClientImpl(RoutingContext routingContext) {
        logger.trace("initializing CassandraClientImpl");
//        createCassandraClientImpl(routingContext.vertx(), routingContext);
        setRoutingContext(routingContext);
//        instance = this;
    }

    /**
     * Construct new singleton CassandraClient instance
     *
     * @param vertx Vertx instance
     */


    public CassandraClientImpl(Vertx vertx) {
        logger.trace("initializing CassandraClientImpl");
//        createCassandraClientImpl(vertx, null);
        setVertx(vertx);
//        instance = this;
    }

    /**
     * Construct new singleton CassandraClient instance
     *
     * @param vertx          Vertx instance
     * @param routingContext routing context
     */


    private void createCassandraClientImpl2(Vertx vertx, RoutingContext routingContext) {
        logger.trace("initializing CassandraClientImpl");

        setRoutingContext(routingContext);
        setVertx(vertx);

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions
                .setConnectionsPerHost(HostDistance.LOCAL, 4, 10)
                .setConnectionsPerHost(HostDistance.REMOTE, 2, 4);

        cassandraClientOptions.dataStaxClusterBuilder()
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withoutJMXReporting()
                .withoutMetrics()
                .withCompression(ProtocolOptions.Compression.LZ4)
//                .withPoolingOptions(poolingOptions)
                .getConfiguration().getCodecRegistry().register(InstantCodec.instance);

        cassandraClient = io.vertx.reactivex.cassandra.CassandraClient.createShared(getVertx(), cassandraClientOptions);

        preparedStatementMap = new HashMap<>();

        cassandraClient.connect(getCassandraKeySpace(), event -> {
            if (event.succeeded()) {
                logger.info("Cassandra client connected");
                cassandraClientCompletableFuture.complete(cassandraClient);
            } else {
                logger.error("Cassandra client is unable to connect, error: {} \n stack trace:{}", event.cause().getLocalizedMessage(), event.cause().getStackTrace());
                cassandraClientCompletableFuture.completeExceptionally(event.cause());
            }
        });
    }

    public CassandraClient init() {
        logger.trace("init()");

        //init runs only once
        if (initAlreadyRun) {
            return this;
        }

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions
                .setConnectionsPerHost(HostDistance.LOCAL, 4, 10)
                .setConnectionsPerHost(HostDistance.REMOTE, 2, 4);

        cassandraClientOptions.dataStaxClusterBuilder()
                .withLoadBalancingPolicy(new RoundRobinPolicy())
                .withoutJMXReporting()
                .withoutMetrics()
                .withCompression(ProtocolOptions.Compression.LZ4)
                .withPoolingOptions(poolingOptions)
                .getConfiguration().getCodecRegistry().register(InstantCodec.instance);

        cassandraClient = io.vertx.reactivex.cassandra.CassandraClient.createShared(getVertx(), cassandraClientOptions);

//        cassandraClientCompletableFuture = new CompletableFuture<>();
        preparedStatementMap = new HashMap<>();

        cassandraClient.connect(getCassandraKeySpace(), event -> {
            if (event.succeeded()) {
                logger.info("Cassandra client connected");
                cassandraClientCompletableFuture.complete(cassandraClient);
            } else {
                logger.error("Cassandra client is unable to connect, error: {} \n stack trace:{}", event.cause().getLocalizedMessage(), event.cause().getStackTrace());
                cassandraClientCompletableFuture.completeExceptionally(event.cause());
            }
        });
        initAlreadyRun = true;

        return this;
    }

    public CassandraClient setContactPoints(String[] cassandraContactPoints) {

        for (String contactPoint : cassandraContactPoints) {
            this.cassandraClientOptions.addContactPoint(contactPoint);
            logger.trace("Cassandra contact point[{}] is set", contactPoint);
        }

        return this;
    }

    public CassandraClient setPort(int port) {

        this.cassandraClientOptions.setPort(port);
        logger.trace("Cassandra port[{}] is set", port);

        return this;
    }

    public CassandraClient setKeySpace(String keySpace) {
        setCassandraKeySpace(keySpace);
        logger.trace("Cassandra keyspace[{}] is set", keySpace);

        return this;
    }

    public CassandraClient setCredentials(String dbusername, String dbuserpassword) {

        this.cassandraClientOptions.dataStaxClusterBuilder().withCredentials(dbusername, dbuserpassword);
        logger.trace("Cassandra credentials with username[{}] is set", dbusername);

        return this;
    }

    public CassandraClient setCassandraClientOptionsWithAbyssDefaultParameters() {

        //init runs only once
        if (setCassandraClientOptionsWithAbyssDefaultParametersAlreadyRun) {
            return this;
        }

        setContactPoints(Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_CONTACT_POINTS).split(","));
        setPort(Config.getInstance().getConfigJsonObject().getInteger(Constants.CASSANDRA_PORT));
        setCredentials(Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_DBUSER_NAME)
                , Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_DBUSER_PASSWORD));
        setKeySpace(Config.getInstance().getConfigJsonObject().getString(Constants.CASSANDRA_KEYSPACE));

        setCassandraClientOptionsWithAbyssDefaultParametersAlreadyRun = true;

        return this;
    }

    public void executeStatement(String queryStatement, Object... bindVariables) {

        if (!cassandraClient.isConnected()) {
            logger.warn("Cassandra client not connected yet.!, exiting..");
            return;
        }

/*
            while (!cassandraClientCompletableFuture.isDone()) {
                logger.info("uyuyor");
                sleep(500);
            }
            cassandraClientCompletableFuture.get();
*/

/*
            if (!preparedStatementMap.containsKey(queryStatement)) {
                CompletableFuture<PreparedStatement> preparedStatementCompletableFuture = new CompletableFuture<>();
                preparedStatementMap.put(queryStatement, preparedStatementCompletableFuture);
                cassandraClient.prepare(queryStatement, prepareResult -> {
                    if (prepareResult.succeeded()) {
                        logger.trace("Cassandra client prepared statement");
                        preparedStatementCompletableFuture.complete(prepareResult.result());
                    } else {
                        logger.error("Cassandra client is unable to prepare statement{} \n error: {} \n stack trace:{}", queryStatement, prepareResult.cause().getLocalizedMessage(), prepareResult.cause().getStackTrace());
                        preparedStatementCompletableFuture.completeExceptionally(prepareResult.cause());
                    }
                });
            }

            PreparedStatement preparedStatement = preparedStatementMap.get(queryStatement).get();
*/
/*
        if (!preparedStatementMap.containsKey(queryStatement)) {
            preparedStatementMap.put(queryStatement, null);
            PreparedStatement preparedStatement = cassandraClient.rxPrepare(queryStatement).blockingGet();
            preparedStatementMap.put(queryStatement, preparedStatement);
*/
/*
            CompletableFuture<PreparedStatement> preparedStatementCompletableFuture = CompletableFuture
                    .supplyAsync(() -> {
                        cassandraClient.prepare(queryStatement, prepareResult -> {
                            if (prepareResult.succeeded()) {
                                logger.info("Cassandra client prepared statement");
                                preparedStatementMap.put(queryStatement, prepareResult.result());
                            } else {
                                logger.error("Cassandra client is unable to prepare statement{} \n error: {} \n stack trace:{}", queryStatement, prepareResult.cause().getLocalizedMessage(), prepareResult.cause().getStackTrace());
                                throw new RuntimeException(prepareResult.cause());
                            }
                        });
                        return null;
                    }, getVertx().nettyEventLoopGroup());
            logger.info("preparedStatementCompletableFuture: {}", preparedStatementCompletableFuture.toString());
            preparedStatementCompletableFuture.get();
            logger.info("preparedStatementCompletableFuture after get: {}", preparedStatementCompletableFuture.toString());
*/

        prepareStatement(queryStatement)
                .flatMap(preparedStatement -> {
                    preparedStatementMap.put(queryStatement, preparedStatement);
                    return cassandraClient.rxExecute(preparedStatement.bind(bindVariables));
                })
                .subscribe(o -> {
                            logger.info("successfully inserted into Cassandra database");
                        }
                        , throwable -> {
                            logger.error("unable to execute query into Cassandra database! \n query:{} \n error:{} \n stack trace: {}", queryStatement, throwable.getLocalizedMessage(), throwable.getStackTrace());
                        });

/*
        PreparedStatement preparedStatement = preparedStatementMap.get(queryStatement);

        if (preparedStatement != null)
            cassandraClient.rxExecute(preparedStatement.bind(bindVariables))
                    .doAfterTerminate(() -> cassandraClient.rxDisconnect())
                    .subscribe(o -> {
                                logger.info("successfully inserted into Cassandra database");
                            }
                            , throwable -> {
                                logger.error("unable to execute query into Cassandra database! \n query:{} \n error:{} \n stack trace: {}", queryStatement, throwable.getLocalizedMessage(), throwable.getStackTrace());
                            });
        else
            logger.error("unable to execute executeStatement(), cause: no prepared statement exists for the requested query:{}", queryStatement);
*/


    }


    private Single<PreparedStatement> prepareStatement(String queryStatement) {
        if (preparedStatementMap.containsKey(queryStatement))
            if (preparedStatementMap.get(queryStatement) != null)
                return Single.just(preparedStatementMap.get(queryStatement));
            else {
                return Single.error(new RuntimeException("query statement not prepared yet, statement : " + queryStatement));
            }
        else {
            preparedStatementMap.put(queryStatement, null);
            //return Single.just(cassandraClient.rxPrepare(queryStatement).blockingGet());
            getVertx().executeBlocking(event -> {
                preparedStatementMap.put(queryStatement, cassandraClient.rxPrepare(queryStatement).blockingGet());
                event.complete();
            }, res -> {
                logger.error("prepare statement blocking execution error occured, error: {}", res.cause().getLocalizedMessage());
            });
            return Single.just(preparedStatementMap.get(queryStatement));
        }
    }

}
