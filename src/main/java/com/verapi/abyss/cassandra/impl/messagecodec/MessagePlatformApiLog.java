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
