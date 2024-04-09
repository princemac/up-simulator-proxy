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
package org.eclipse.uprotocol.simulatorproxy;

import static org.eclipse.uprotocol.common.util.UStatusUtils.isOk;
import static org.eclipse.uprotocol.common.util.UStatusUtils.toStatus;
import static org.eclipse.uprotocol.common.util.log.Formatter.join;
import static org.eclipse.uprotocol.common.util.log.Formatter.status;
import static org.eclipse.uprotocol.common.util.log.Formatter.stringify;
import static org.eclipse.uprotocol.simulatorproxy.SimulatorProxyService.LOG_TAG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import org.eclipse.uprotocol.UPClient;
import org.eclipse.uprotocol.UprotocolOptions;
import org.eclipse.uprotocol.cloudevent.serialize.Base64ProtobufSerializer;
import org.eclipse.uprotocol.common.UStatusException;
import org.eclipse.uprotocol.common.util.log.Key;
import org.eclipse.uprotocol.core.usubscription.v3.CreateTopicRequest;
import org.eclipse.uprotocol.core.usubscription.v3.USubscription;
import org.eclipse.uprotocol.simulatorproxy.utils.Constants;
import org.eclipse.uprotocol.transport.UListener;
import org.eclipse.uprotocol.uri.factory.UResourceBuilder;
import org.eclipse.uprotocol.uri.serializer.LongUriSerializer;
import org.eclipse.uprotocol.v1.UEntity;
import org.eclipse.uprotocol.v1.UMessage;
import org.eclipse.uprotocol.v1.UStatus;
import org.eclipse.uprotocol.v1.UUri;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseService extends Service {
    public static final String CHANNEL_ID = "BaseService";
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final UListener mURpcListener = this::handleRequestMessage;
    private Descriptors.ServiceDescriptor serviceDescriptor;
    private UUri SERVICE_URI;
    private String TAG;
    private UPClient mUPClient;
    private USubscription.Stub mUSubscriptionStub;

    static synchronized void sendRpcRequestToHost(Socket mClientSocket, UMessage requestMessage) {
        JSONObject jsonObj = new JSONObject();
        try {
            PrintWriter wr = new PrintWriter(mClientSocket.getOutputStream());
            String serializedMsg = Base64ProtobufSerializer.deserialize(requestMessage.toByteArray());
            jsonObj.put(Constants.ACTION, Constants.ACTION_RPC_REQUEST);
            jsonObj.put(Constants.ACTION_DATA, serializedMsg);
            Log.d(LOG_TAG, "Sending rpc request to host: " + jsonObj);
            wr.println(jsonObj);
            wr.flush();
        } catch (JSONException | IOException e) {
            Log.e(LOG_TAG, Objects.requireNonNullElse(e.getMessage(), "Exception occurs while sending rpc request to host"));
        }
    }

    public void initializeUPClient(Descriptors.ServiceDescriptor serviceDescriptor) {
        this.serviceDescriptor = serviceDescriptor;

        UEntity SERVICE = UEntity.newBuilder().setName(getVehicleServiceName()).setVersionMajor(getServiceVersion()).build();
        TAG = SERVICE.getName();
        SERVICE_URI = UUri.newBuilder().setEntity(SERVICE).build();
        mUPClient = UPClient.create(getApplicationContext(), SERVICE, mExecutor, (client, ready) -> {
            if (ready) {
                Log.i(TAG, join(Key.EVENT, "up client connected"));
            } else {
                Log.w(TAG, join(Key.EVENT, "up client unexpectedly disconnected"));
            }
        });
        mUSubscriptionStub = USubscription.newStub(mUPClient);
        mUPClient.connect().thenCompose(status -> {
            logStatus("connect", status);
            return isOk(status) ? CompletableFuture.completedFuture(status) : CompletableFuture.failedFuture(new UStatusException(status));
        });


        Constants.ENTITY_BASESERVICE.put(SERVICE.getName(), BaseService.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public UStatus registerMethod(@NonNull UUri methodUri) {
        final UStatus status = mUPClient.registerListener(methodUri, mURpcListener);
        return logStatus("registerMethod", status, Key.URI, stringify(methodUri));

    }

    public UStatus publish(@NonNull UMessage message) {
        final UStatus status = mUPClient.send(message);
        logStatus("publish", status, Key.TOPIC, stringify(message.getAttributes().getSource()));
        return status;
    }

    public UStatus send_response(@NonNull UMessage message) {
        final UStatus status = mUPClient.send(message);
        logStatus("successfully send rpc response", status, Key.TOPIC, stringify(message.getAttributes().getSource()));
        return status;
    }

    private CompletableFuture<UStatus> unregisterMethod(@NonNull UUri methodUri) {
        return CompletableFuture.supplyAsync(() -> {
            final UStatus status = mUPClient.unregisterListener(methodUri, mURpcListener);
            return logStatus("unregisterMethod", status, Key.URI, stringify(methodUri));
        });
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "on start");

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Android Proxy Base Service").setContentText("Running...").setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void handleRequestMessage(@NonNull UMessage requestMessage) {
        System.out.println("Request received");
        final UUri methodUri = requestMessage.getAttributes().getSink();

        String uri = LongUriSerializer.instance().serialize(methodUri);

        // Send this request to python service
        if (Constants.RPC_SOCKET_LIST.containsKey(uri)) {
            Socket socket = Constants.RPC_SOCKET_LIST.get(uri);
            //write data to socket
            if (socket != null)
                sendRpcRequestToHost(socket, requestMessage);
        }

    }

    public String getVehicleServiceName() {
        final DescriptorProtos.ServiceOptions options = serviceDescriptor.getOptions();
        return (options != null) ? options.getExtension(UprotocolOptions.name) : "";
    }

    private List<String> getAllRPCNames(Descriptors.ServiceDescriptor serviceDescriptor) {
        System.out.println("Service Name: " + serviceDescriptor.getName());
        List<String> rpcs = new ArrayList<>();

        for (Descriptors.MethodDescriptor methodDescriptor : serviceDescriptor.getMethods()) {
            System.out.println("RPC Name: " + methodDescriptor.getName());
            rpcs.add(methodDescriptor.getName());
        }
        return rpcs;
    }

    public int getServiceVersion() {
        final DescriptorProtos.ServiceOptions options = serviceDescriptor.getOptions();
        return (options != null) ? options.getExtension(UprotocolOptions.versionMajor) : 0;
    }

    /******************************************************************************
     * function:     createNotificationChannel
     * Description:  Create notification channel with channel ID.
     *****************************************************************************/
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Android Proxy Base Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mExecutor.shutdown();
        List<CompletableFuture<UStatus>> rpcs = new ArrayList<>();
        for (String rpc : getAllRPCNames(serviceDescriptor)) {
            CompletableFuture<UStatus> topicFuture = unregisterMethod(UUri.newBuilder(SERVICE_URI).setResource(UResourceBuilder.forRpcRequest(rpc)).build());
            rpcs.add(topicFuture);
        }
        CompletableFuture.allOf(rpcs.toArray(new CompletableFuture[0])).exceptionally(exception -> null).thenCompose(it -> mUPClient.disconnect()).whenComplete((status, exception) -> logStatus("disconnect", status));
        super.onDestroy();
    }

    protected CompletableFuture<UStatus> createTopic(@NonNull UUri topic) {
        return mUSubscriptionStub.createTopic(CreateTopicRequest.newBuilder().setTopic(topic).build()).toCompletableFuture().whenComplete((status, exception) -> {
            if (exception != null) { // Communication failure
                status = toStatus(exception);
            }
            logStatus("createTopic", status, Key.TOPIC, stringify(topic));
        });
    }

    private @NonNull UStatus logStatus(@NonNull String method, @NonNull UStatus status, Object... args) {
        Log.println(isOk(status) ? Log.INFO : Log.ERROR, TAG, status(method, status, args));
        return status;
    }
}
