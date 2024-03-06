/*
 * Copyright (c) 2024 General Motors GTO LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * SPDX-FileType: SOURCE
 * SPDX-FileCopyrightText: 2024 General Motors GTO LLC
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.uprotocol.simulatorproxy.utils;

import org.eclipse.uprotocol.simulatorproxy.BaseService;
import org.eclipse.uprotocol.transport.UListener;
import org.eclipse.uprotocol.v1.UPayload;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Constants {
    public static final HashMap<String, CompletableFuture<UPayload>> COMPLETE_FUTURE_REQ_RES = new HashMap<>();
    public static final HashMap<String, BaseService> ENTITY_BASESERVICE = new HashMap<>();
    public static final Map<String, Class<? extends BaseService>> ENTITY_SERVICE_MAP = new HashMap<>();
    public static final Map<String, Socket> ENTITY_SOCKET = new HashMap<>();
    public static final String ACTION = "action";
    public static final String ACTION_DATA = "data";
    public static final String STATUS_PUBLISH = "publish_status";
    public static final String UPDATE_TOPIC = "topic_update";
    public static final String STATUS_SUBSCRIBE = "subscribe_status";
    public static final String STATUS_CREATE_TOPIC_STATUS = "create_topic_status";

    public static final String STATUS_REGISTER_RPC = "register_rpc_status";

    public static final String ACTION_START_SERVICE = "start_service";
    public static final String ACTION_PUBLISH = "publish";
    public static final String ACTION_RPC_REQUEST = "rpc_request";
    public static final String ACTION_RPC_RESPONSE = "rpc_response";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_REGISTER_RPC = "register_rpc";
    public static final String ACTION_INVOKE_METHOD = "send_rpc";
    public static final String ACTION_CREATE_TOPIC = "create_topic";

    public static final Map<String, ArrayList<Socket>> TOPIC_SOCKET_MAP = new HashMap<>();
    public static final Map<String, Socket> RPC_SOCKET_LIST = new HashMap<>();
    public static HashMap<String, UListener> TOPIC_LISTENER_MAP = new HashMap<>();


}
