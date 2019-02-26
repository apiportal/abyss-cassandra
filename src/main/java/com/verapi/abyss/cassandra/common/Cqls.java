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

package com.verapi.abyss.cassandra.common;

public class Cqls {
    public static final String CQL_INSERT_PLATFORM_API_LOG = "insert into platform_api_log (id, httpmethod, httppath, httpsession, \"index\", remoteaddress, source, timestamp,username)\n" +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String CQL_INSERT_TRAFFIC_LOG = "insert into api_traffic (id, accept_encoding, content_length, content_type, cookies, created, http_method, http_session,\n" +
            "http_version, referer, remote_client, request_body, request_bytes_read, request_headers,\n" +
            "request_host, request_is_ssl, request_params, request_path, request_port, request_query,\n" +
            "request_scheme, request_uri, response_body, response_bytes_written, response_headers,\n" +
            "response_status_code, timestamp, user_agent, username)\n" +
            "values (?, ?, ?, ?, ?, toTimestamp(now()), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
}
