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

import com.verapi.abyss.cassandra.impl.CassandraClientFactoryImpl;

public interface CassandraClientFactory {

    /**
     * Create a shared singleton {@link com.verapi.abyss.cassandra.CassandraClientFactory}
     *
     * @return the {@link com.verapi.abyss.cassandra.CassandraClientFactory} instance
     */
    static CassandraClientFactory getInstance() {
        return CassandraClientFactoryImpl.getInstance();
    }

    Boolean checkIfExistsCassandraClient(String context);

    CassandraClient getCassandraClient(String context);

    CassandraClient putCassandraClient(String context, CassandraClient cassandraClient);

}
