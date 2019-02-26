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

import java.time.Instant;
import java.util.UUID;

public class MessagePlatformApiLog {
    public final UUID id;
    public final String httpMethod;
    public final String httpPath;
    public final String httpSession;
    public final String apiName;
    public final String remoteAddress;
    public final String apiPayload;
    public final Instant timestamp;
    public final String username;

    public MessagePlatformApiLog(UUID id, String httpMethod, String httpPath, String httpSession, String apiName, String remoteAddress, String apiPayload, Instant timestamp, String username) {
        this.id = id;
        this.httpMethod = httpMethod;
        this.httpPath = httpPath;
        this.httpSession = httpSession;
        this.apiName = apiName;
        this.remoteAddress = remoteAddress;
        this.apiPayload = apiPayload;
        this.timestamp = timestamp;
        this.username = username;
    }

    public MessagePlatformApiLog() {
        this.id = null;
        this.httpMethod = null;
        this.httpPath = null;
        this.httpSession = null;
        this.apiName = null;
        this.remoteAddress = null;
        this.apiPayload = null;
        this.timestamp = null;
        this.username = null;
    }

    @Override
    public String toString() {
        return "MessageConsumerPlatformApiLog{" +
                "id=" + id +
                ", httpMethod='" + httpMethod + '\'' +
                ", httpPath='" + httpPath + '\'' +
                ", httpSession='" + httpSession + '\'' +
                ", apiName='" + apiName + '\'' +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", apiPayload='" + apiPayload + '\'' +
                ", timestamp=" + timestamp +
                ", username='" + username + '\'' +
                '}';
    }
}
