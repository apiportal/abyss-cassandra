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

package com.verapi.abyss.cassandra.common;

public class Cqls {
    public static final String CQL_INSERT_PLATFORM_API_LOG = "insert into platform_api_log (id, httpmethod, httppath, httpsession, \"index\", remoteaddress, source, timestamp,username)\n" +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String CQL_INSERT_TRAFFIC_LOG_OLD = "insert into api_traffic (id, accept_encoding, content_length, content_type, cookies, created, created_unixtimestamp, http_method, http_session,\n" +
            "http_version, referer, remote_client, request_body, request_bytes_read, request_headers,\n" +
            "request_host, request_is_ssl, request_params, request_path, request_port, request_query,\n" +
            "request_scheme, request_uri, response_body, response_bytes_written, response_headers,\n" +
            "response_status_code, object_timestamp, object_unixtimestamp, user_agent, username)\n" +
            "values (?, ?, ?, ?, ?, toTimestamp(now()), toUnixTimestamp(now()), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String CQL_INSERT_TRAFFIC_LOG = "insert into api_traffic_halil (day, timestamp, api_id, app_id, accept_encoding, api, app, content_length, content_type,\n" +
            "contract, contract_id, cookies, correlation_id, country, country_id, created,\n" +
            "http_method, http_session, http_version, id, license, license_id, organization,\n" +
            "organization_id, referer, remote_client, request_body, request_bytes_read,\n" +
            "request_headers, request_host, request_is_ssl, request_params, request_path,\n" +
            "request_port, request_query, request_scheme, request_uri, response_body,\n" +
            "response_bytes_written, response_headers, response_status_code, route, user_agent,\n" +
            "username)\n" +
            "values (todate(now()), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, totimestamp(now()), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
}
