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
import com.verapi.abyss.cassandra.impl.messagecodec.MessagePlatformApiLog;
import com.verapi.abyss.cassandra.impl.messagecodec.MessagePlatformApiLogCodec;
import com.verapi.abyss.common.Config;
import com.verapi.abyss.common.Constants;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.cassandra.CassandraClient;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class MessageConsumerPlatformApiLog {
    private static Logger logger = LoggerFactory.getLogger(MessageConsumerPlatformApiLog.class);

    public Handler<Message<com.verapi.abyss.cassandra.impl.messagecodec.MessagePlatformApiLog>> logWriter(Map<String, PreparedStatement> preparedStatementMap, CassandraClient cassandraClient) {
        return message -> {
            MessagePlatformApiLog messagePlatformApiLog = message.body();
            logger.trace("logWriter() received message: {}", message);
            if (preparedStatementMap.containsKey(Cqls.CQL_INSERT_PLATFORM_API_LOG)) {
                PreparedStatement preparedStatement = preparedStatementMap.get(Cqls.CQL_INSERT_PLATFORM_API_LOG);
                if (preparedStatement != null) {
                    cassandraClient.execute(preparedStatement.bind(messagePlatformApiLog.id
                            , messagePlatformApiLog.httpMethod
                            , messagePlatformApiLog.httpPath
                            , messagePlatformApiLog.httpSession
                            , messagePlatformApiLog.apiName
                            , messagePlatformApiLog.remoteAddress
                            , messagePlatformApiLog.apiPayload
                            , messagePlatformApiLog.timestamp
                            , messagePlatformApiLog.username), event -> {
                        String replyMsg;
                        if (event.succeeded()) {
                            logger.trace("successfully executed query into Cassandra database, using values: {}", messagePlatformApiLog);
                            DeliveryOptions deliveryOptions = new DeliveryOptions()
                                    .setCodecName(new MessagePlatformApiLogCodec().name())
                                    .setSendTimeout(Config.getInstance().getConfigJsonObject().getInteger(Constants.EVENTBUS_ADDRESS_PLATFORM_API_LOG_SEND_TIMEOUT));
//                            message.reply(messagePlatformApiLog, deliveryOptions);
                            message.reply(new MessagePlatformApiLog(), deliveryOptions);
                        } else {
                            replyMsg = "unable to execute query into Cassandra database! \n query:" + Cqls.CQL_INSERT_PLATFORM_API_LOG +
                                    " \n error:{}" + event.cause().getLocalizedMessage();
                            logger.error(replyMsg);
                            message.fail(-1, replyMsg);
                        }
                    });
                } else {
                    logger.warn("query statement not prepared yet, statement :{}", Cqls.CQL_INSERT_PLATFORM_API_LOG);
                }
            } else {
                logger.warn("specified CQL not defined, CQL: {}", Cqls.CQL_INSERT_PLATFORM_API_LOG);
            }
        };
    }
}
