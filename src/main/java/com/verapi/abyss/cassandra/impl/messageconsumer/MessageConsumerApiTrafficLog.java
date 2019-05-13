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
                            UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.API_ID))
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.APP_ID))
                            , messageApiTrafficLog.getString(ApiTraffic.ACCEPT_ENCODING)
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.API).encode()
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.APP).encode()
                            , messageApiTrafficLog.getInteger(ApiTraffic.CONTENT_LENGTH)
                            , messageApiTrafficLog.getString(ApiTraffic.CONTENT_TYPE)
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.CONTRACT).encode()
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.CONTRACT_ID))
                            , messageApiTrafficLog.getJsonArray(ApiTraffic.COOKIES).encode()
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.CORRELATION_ID))
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.COUNTRY).encode()
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.COUNTRY_ID))
                            , messageApiTrafficLog.getString(ApiTraffic.HTTP_METHOD)
                            , messageApiTrafficLog.getString(ApiTraffic.HTTP_SESSION)
                            , messageApiTrafficLog.getString(ApiTraffic.HTTP_VERSION)
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.ID))
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.LICENSE).encode()
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.LICENSE_ID))
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.ORGANIZATION).encode()
                            , UUID.fromString(messageApiTrafficLog.getString(ApiTraffic.ORGANIZATION_ID))
                            , messageApiTrafficLog.getString(ApiTraffic.REFERER)
                            , messageApiTrafficLog.getString(ApiTraffic.REMOTE_CLIENT)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_BODY)
                            , messageApiTrafficLog.getLong(ApiTraffic.REQUEST_BYTES_READ).intValue()
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.REQUEST_HEADERS).encode()
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_HOST)
                            , messageApiTrafficLog.getBoolean(ApiTraffic.REQUEST_IS_SSL)
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.REQUEST_PARAMS).encode()
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_PATH)
                            , messageApiTrafficLog.getInteger(ApiTraffic.REQUEST_PORT)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_QUERY)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_SCHEME)
                            , messageApiTrafficLog.getString(ApiTraffic.REQUEST_URI)
                            , messageApiTrafficLog.getString(ApiTraffic.RESPONSE_BODY)
                            , messageApiTrafficLog.getLong(ApiTraffic.RESPONSE_BYTES_WRITTEN).intValue()
                            , messageApiTrafficLog.getJsonObject(ApiTraffic.RESPONSE_HEADERS).encode()
                            , messageApiTrafficLog.getInteger(ApiTraffic.RESPONSE_STATUS_CODE)
                            , messageApiTrafficLog.getString(ApiTraffic.ROUTE)
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
