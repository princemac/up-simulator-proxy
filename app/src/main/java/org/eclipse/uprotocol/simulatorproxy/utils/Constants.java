/**
 * SPDX-FileCopyrightText: Copyright (c) 2024 Contributors to the Eclipse Foundation
 * <p>
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http: *www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * SPDX-FileType: SOURCE
 * SPDX-License-Identifier: Apache-2.0
 */
package org.eclipse.uprotocol.simulatorproxy.utils;

import org.eclipse.uprotocol.simulatorproxy.BaseService;
import org.eclipse.uprotocol.transport.UListener;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final HashMap<String, BaseService> ENTITY_BASESERVICE = new HashMap<>();
    public static final Map<String, Class<? extends BaseService>> ENTITY_SERVICE_MAP = new HashMap<>();
    public static final Map<String, Socket> ENTITY_SOCKET = new HashMap<>();
    public static final String ACTION = "action";
    public static final String ACTION_DATA = "data";
    public static final String STATUS_ID = "status_id";
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
