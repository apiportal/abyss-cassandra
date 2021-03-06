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

package com.verapi.abyss.cassandra;

import com.verapi.abyss.cassandra.impl.CassandraClientImpl;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CassandraClient {
    static Logger logger = LoggerFactory.getLogger(CassandraClient.class);

    /**
     * Create a shared singleton {@link com.verapi.abyss.cassandra.CassandraClient} per each context
     *
     * @param context context name associated with shared single {@link com.verapi.abyss.cassandra.CassandraClient}
     * @param vertx   {@link io.vertx.reactivex.core.Vertx} instance
     * @return the {@link com.verapi.abyss.cassandra.CassandraClient} instance
     */
    static CassandraClient createShared(String context, Vertx vertx) {
        if (vertx == null)
            return null;
        if (CassandraClientFactory.getInstance().checkIfExistsCassandraClient(context))
            return CassandraClientFactory.getInstance().getCassandraClient(context);
        else {
            return CassandraClientFactory.getInstance().putCassandraClient(context, new CassandraClientImpl(vertx));
        }
    }

    /**
     * Create a shared singleton {@link com.verapi.abyss.cassandra.CassandraClient} per each context
     *
     * @param context        context name associated with shared single {@link com.verapi.abyss.cassandra.CassandraClient}
     * @param routingContext {@link io.vertx.reactivex.ext.web.RoutingContext} instance
     * @return the {@link com.verapi.abyss.cassandra.CassandraClient} instance
     */
    static CassandraClient createShared(String context, RoutingContext routingContext) {
        if (routingContext == null) {
            logger.error("routing context is null.!");
            return null;
        }
        if (CassandraClientFactory.getInstance().checkIfExistsCassandraClient(context))
            return CassandraClientFactory.getInstance().getCassandraClient(context);
        else {
            return CassandraClientFactory.getInstance().putCassandraClient(context, new CassandraClientImpl(routingContext));
        }
    }

    CassandraClient setCassandraClientOptionsWithAbyssDefaultParameters();

    CassandraClient init();

    void executeStatement(String queryStatement, Object... bindVariables);

}
