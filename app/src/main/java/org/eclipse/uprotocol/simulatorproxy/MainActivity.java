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
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.uprotocol.simulatorproxy.utils.Constants;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.BodyMirrors;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Braking;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.CabinClimate;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Chassis;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Engine;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Exterior;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.HelloWorld;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Horn;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Suspension;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Transmission;
import org.eclipse.uprotocol.simulatorproxy.vehicleservice.Vehicle;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Access properties from gradle.properties
        String appVersionName = BuildConfig.VERSION_NAME;
        TextView tv = (TextView) findViewById(R.id.tvVersion);
        tv.setText(getString(R.string.version) + appVersionName);

        // Start the SocketServerService when the activity is created
        startService(new Intent(this, SimulatorProxyService.class));
        //vehicle service will start only when there is request from host
        Constants.ENTITY_SERVICE_MAP.put("body.cabin_climate", CabinClimate.class);
        Constants.ENTITY_SERVICE_MAP.put("body.mirrors", BodyMirrors.class);
        Constants.ENTITY_SERVICE_MAP.put("chassis.braking", Braking.class);
        Constants.ENTITY_SERVICE_MAP.put("chassis", Chassis.class);
        Constants.ENTITY_SERVICE_MAP.put("propulsion.engine", Engine.class);
        Constants.ENTITY_SERVICE_MAP.put("vehicle.exterior", Exterior.class);
        Constants.ENTITY_SERVICE_MAP.put("example.hello_world", HelloWorld.class);
        Constants.ENTITY_SERVICE_MAP.put("body.horn", Horn.class);
        Constants.ENTITY_SERVICE_MAP.put("chassis.suspension", Suspension.class);
        Constants.ENTITY_SERVICE_MAP.put("propulsion.transmission", Transmission.class);
        Constants.ENTITY_SERVICE_MAP.put("vehicle", Vehicle.class);


    }
}