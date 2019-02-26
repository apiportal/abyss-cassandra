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
