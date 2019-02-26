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

package com.verapi.abyss.cassandra.impl.messageconsumer;

import com.datastax.driver.core.PreparedStatement;
import com.verapi.abyss.cassandra.common.Cqls;
import com.verapi.abyss.common.Config;
import com.verapi.abyss.common.Constants;
import com.verapi.abyss.common.message.ApiTraffic;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.cassandra.CassandraClient;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;


public class MessageConsumerApiTrafficLog {
    private static Logger logger = LoggerFactory.getLogger(MessageConsumerApiTrafficLog.class);

    public Handler<Message<JsonObject>> logWriter(Map<String, PreparedStatement> preparedStatementMap, CassandraClient cassandraClient) {
        return message -> {
            JsonObject messageApiTrafficLog = message.body();
            logger.trace("logWriter() received message: {}", message);
            if (preparedStatementMap.containsKey(Cqls.CQL_INSERT_TRAFFIC_LOG)) {
                PreparedStatement preparedStatement = preparedStatementMap.get(Cqls.CQL_INSERT_TRAFFIC_LOG);
                if (preparedStatement != null) {
                    cassandraClient.execute(preparedStatement.bind(
                            UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.ID))
                            , messageApiTrafficLog.getString(ApiTraffic.ACCEPT_ENCODING)
                            , messageApiTrafficLog.getInteger(ApiTraffic.CONTENT_LENGTH)
                            , messageApiTrafficLog.getString(ApiTraffic.CONTENT_TYPE)
                            , messageApiTrafficLog.getString(ApiTraffic.COOKIES)
                            , messageApiTrafficLog.getString(ApiTraffic.HTTP_METHOD)
                            , messageApiTrafficLog.getString(ApiTraffic.HTTP_SESSION)
                            , messageApiTrafficLog.getString(ApiTraffic.HTTP_VERSION)
                            , messageApiTrafficLog.getString(ApiTraffic.REFERER)
                            , messageApiTrafficLog.getString(ApiTraffic.REMOTE_CLIENT)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_BODY)
                            , messageApiTrafficLog.getLong(ApiTraffic.REQUEST_BYTES_READ)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_HEADERS)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_HOST)
                            , messageApiTrafficLog.getBoolean(ApiTraffic.REQUEST_IS_SSL)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_PARAMS)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_PATH)
                            , messageApiTrafficLog.getInteger(ApiTraffic.REQUEST_PORT)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_QUERY)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_SCHEME)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_URI)
                            , messageApiTrafficLog.getString(ApiTraffic.RESPONSE_BODY)
                            , messageApiTrafficLog.getLong(ApiTraffic.RESPONSE_BYTES_WRITTEN)
                            , messageApiTrafficLog.getString(ApiTraffic.RESPONSE_HEADERS)
                            , messageApiTrafficLog.getInteger(ApiTraffic.RESPONSE_STATUS_CODE)
                            , messageApiTrafficLog.getInstant(ApiTraffic.TIMESTAMP)
                            , messageApiTrafficLog.getString(ApiTraffic.USER_AGENT)
                            , messageApiTrafficLog.getString(ApiTraffic.USERNAME)
                    ), event -> {
                        String replyMsg;
                        if (event.succeeded()) {
                            logger.trace("successfully executed query into Cassandra database, using values: {}", messageApiTrafficLog.encodePrettily());
                            DeliveryOptions deliveryOptions = new DeliveryOptions()
                                    .setSendTimeout(Config.getInstance().getConfigJsonObject().getInteger(Constants.EVENTBUS_ADDRESS_API_TRAFFIC_LOG_SEND_TIMEOUT));
                            message.reply(new JsonObject(), deliveryOptions);
                        } else {
                            replyMsg = "unable to execute query into Cassandra database! \n query:" + Cqls.CQL_INSERT_TRAFFIC_LOG +
                                    " \n error:{}" + event.cause().getLocalizedMessage();
                            logger.error(replyMsg);
                            message.fail(-1, replyMsg);
                        }
                    });
                } else {
                    logger.warn("query statement not prepared yet, statement :{}", Cqls.CQL_INSERT_TRAFFIC_LOG);
                }
            } else {
                logger.warn("specified CQL not defined, CQL: {}", Cqls.CQL_INSERT_TRAFFIC_LOG);
            }
        };
    }
}
