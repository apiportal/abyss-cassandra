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

package com.verapi.abyss.cassandra.impl.messagecodec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MessagePlatformApiLogCodec implements MessageCodec<MessagePlatformApiLog, MessagePlatformApiLog> {
    private static Logger logger = LoggerFactory.getLogger(MessagePlatformApiLogCodec.class);

    @Override
    public void encodeToWire(Buffer buffer, MessagePlatformApiLog messagePlatformApiLog) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonToStr;
        try {

            // Encode object to string
            jsonToStr = mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).writeValueAsString(messagePlatformApiLog);

            // Length of JSON: is NOT characters count
            int length = jsonToStr.getBytes().length;

            // Write data into given buffer
            buffer.appendInt(length);
            buffer.appendString(jsonToStr);

        } catch (JsonProcessingException e) {
            logger.error("Json processing exception, error:{}", e.getLocalizedMessage());
        }
    }

    @Override
    public MessagePlatformApiLog decodeFromWire(int pos, Buffer buffer) {

        //message starting from this *position* of buffer
        int _pos = pos;

        // Length of JSON
        int length = buffer.getInt(_pos);

        // Get JSON string by it`s length
        // Jump 4 because getInt() == 4 bytes
        String jsonStr = buffer.getString(_pos += 4, _pos += length);
        JsonObject contentJson = new JsonObject(jsonStr);

        ObjectMapper mapper = new ObjectMapper();
        MessagePlatformApiLog messagePlatformApiLog = null;
        try {
            messagePlatformApiLog = mapper.readValue(contentJson.encode(), MessagePlatformApiLog.class);
        } catch (IOException e) {
            logger.error("Json to object processing exception, error:{}", e.getLocalizedMessage());
        }

        return messagePlatformApiLog;
    }

    @Override
    public MessagePlatformApiLog transform(MessagePlatformApiLog messagePlatformApiLog) {
        return messagePlatformApiLog;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
