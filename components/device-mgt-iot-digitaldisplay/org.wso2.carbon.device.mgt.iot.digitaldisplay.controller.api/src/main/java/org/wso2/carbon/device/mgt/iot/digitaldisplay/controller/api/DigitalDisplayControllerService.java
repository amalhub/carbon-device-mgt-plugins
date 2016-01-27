/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.digitaldisplay.controller.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.annotations.api.API;
import org.wso2.carbon.apimgt.annotations.device.DeviceType;
import org.wso2.carbon.apimgt.annotations.device.feature.Feature;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.DeviceManagement;
import org.wso2.carbon.device.mgt.iot.controlqueue.mqtt.MqttConfig;
import org.wso2.carbon.device.mgt.iot.digitaldisplay.controller.api.exception.DigitalDisplayException;
import org.wso2.carbon.device.mgt.iot.digitaldisplay.controller.api.util.DigitalDisplayMQTTConnector;
import org.wso2.carbon.device.mgt.iot.digitaldisplay.plugin.constants.DigitalDisplayConstants;
import org.wso2.carbon.device.mgt.iot.transport.TransportHandlerException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


@API(name = "digital_display", version = "1.0.0", context = "/digital_display")
@DeviceType(value = "digital_display")
public class DigitalDisplayControllerService {


    private static Log log = LogFactory.getLog(DigitalDisplayControllerService.class);

    private static DigitalDisplayMQTTConnector digitalDisplayMQTTConnector;

    private boolean waitForServerStartup() {
        while (!DeviceManagement.isServerReady()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return true;
            }
        }
        return false;
    }

    public DigitalDisplayMQTTConnector getDigitalDisplayMQTTConnector() {
        return DigitalDisplayControllerService.digitalDisplayMQTTConnector;
    }

    public void setDigitalDisplayMQTTConnector(final
            DigitalDisplayMQTTConnector digitalDisplayMQTTConnector) {

        Runnable connector = new Runnable() {
            public void run() {
                if (waitForServerStartup()) {
                    return;
                }
                DigitalDisplayControllerService.digitalDisplayMQTTConnector = digitalDisplayMQTTConnector;
                if (MqttConfig.getInstance().isEnabled()) {
                    digitalDisplayMQTTConnector.connect();
                } else {
                    log.warn("MQTT disabled in 'devicemgt-config.xml'. " +
                             "Hence, DigitalDisplayMQTTConnector not started.");
                }
            }
        };
        Thread connectorThread = new Thread(connector);
        connectorThread.setDaemon(true);
        connectorThread.start();
    }

    /**
     * Restart the running browser in the given digital display.
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/restart-browser")
    @POST
    @Feature(code = "restart-browser", name = "Restart Browser", type = "operation",
            description = "Restart Browser in Digital Display")
    public void restartBrowser(@HeaderParam("deviceId") String deviceId,
                               @HeaderParam("owner") String owner,
                               @HeaderParam("Authorization") String token,
                               @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.RESTART_BROWSER_CONSTANT + "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        }

    }

    /**
     * Terminate all running processes. If this execute we have to reboot digital display manually.
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/terminate-display")
    @POST
    @Feature(code = "terminate-display", name = "Terminate Display", type = "operation",
            description = "Terminate all running process in Digital Display")
    public void terminateDisplay(@HeaderParam("deviceId") String deviceId,
                                 @HeaderParam("owner") String owner,
                                 @HeaderParam("Authorization") String token,
                                 @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.TERMINATE_DISPLAY_CONSTANT + "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }

    /**
     * Reboot running digital display
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/restart-display")
    @POST
    @Feature(code = "restart-display", name = "Restart Display", type = "operation",
            description = "Restart Digital Display")
    public void restartDisplay(@HeaderParam("deviceId") String deviceId,
                               @HeaderParam("owner") String owner,
                               @HeaderParam("Authorization") String token,
                               @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.RESTART_DISPLAY_CONSTANT + "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }

    /**
     * Search through the sequence and edit requested resource
     *
     * @param deviceId  id of the controlling digital display
     * @param owner     owner of the digital display
     * @param token     web socket id of the method invoke client
     * @param response  response type of the method
     * @param name      name of page need to change
     * @param attribute this can be path,time or type
     * @param newValue  page is used to replace path
     */
    @Path("/edit-sequence")
    @POST
    @Feature(code = "edit-sequence", name = "Edit Sequence", type = "operation",
            description = "Search through the sequence and edit requested resource in Digital Display")
    public void editSequence(@HeaderParam("deviceId") String deviceId,
                             @HeaderParam("owner") String owner,
                             @FormParam("name") String name,
                             @FormParam("attribute") String attribute,
                             @FormParam("new-value") String newValue,
                             @HeaderParam("Authorization") String token,
                             @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            String params = name + "|" + attribute + "|" + newValue;
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.EDIT_SEQUENCE_CONSTANT + "::", params);
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }

    @Path("/upload-content")
    @POST
    @Feature(code = "upload-content", name = "Upload Content", type = "operation",
            description = "Search through the sequence and edit requested resource in Digital Display")
    public void uploadContent(@HeaderParam("deviceId") String deviceId,
                              @HeaderParam("owner") String owner,
                              @FormParam("remote-path") String remotePath,
                              @FormParam("screen-name") String screenName,
                              @HeaderParam("Authorization") String token,
                              @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            String params = remotePath + "|" + screenName;
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.UPLOAD_CONTENT_CONSTANT + "::",
                    params);
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }

    /**
     * Add new resource end to the existing sequence
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     * @param type     type of new resource
     * @param time     new resource visible time
     * @param path     URL of the new resource
     */
    @Path("/add-resource")
    @POST
    @Feature(code = "add-resource", name = "Add Resource", type = "operation",
            description = "Add new resource end to the existing sequence in Digital Display")
    public void addNewResource(@HeaderParam("deviceId") String deviceId,
                               @HeaderParam("owner") String owner,
                               @FormParam("type") String type,
                               @FormParam("time") String time,
                               @FormParam("path") String path,
                               @FormParam("name") String name,
                               @FormParam("position") String position,
                               @HeaderParam("Authorization") String token,
                               @Context HttpServletResponse response) {

        String params;
        try {

            if (position.isEmpty()) {
                params = type + "|" + time + "|" + path + "|" + name;
            } else {
                params = type + "|" + time + "|" + path + "|" + name +
                        "|" + "after=" + position;
            }
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" +
                    DigitalDisplayConstants.ADD_NEW_RESOURCE_CONSTANT + "::", params);
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Delete a resource in sequence
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     * @param name     name of the page no need to delete
     */
    @Path("/remove-resource")
    @POST
    @Feature(code = "remove-resource", name = "Remove Resource", type = "operation",
            description = "Delete a resource from sequence in Digital Display")
    public void removeResource(@HeaderParam("deviceId") String deviceId,
                               @HeaderParam("owner") String owner,
                               @FormParam("name") String name,
                               @HeaderParam("Authorization") String token,
                               @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" +
                    DigitalDisplayConstants.REMOVE_RESOURCE_CONSTANT + "::", name);
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Restart HTTP in running display
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/restart-server")
    @POST
    @Feature(code = "restart-server", name = "Restart Server", type = "operation",
            description = "Stop HTTP Server running in Digital Display")
    public void restartServer(@HeaderParam("deviceId") String deviceId,
                              @HeaderParam("owner") String owner,
                              @HeaderParam("Authorization") String token,
                              @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.RESTART_SERVER_CONSTANT + "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }

    /**
     * Get screenshot of running display
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/screenshot")
    @POST
    @Feature(code = "screenshot", name = "Take Screenshot", type = "operation",
            description = "Show current view in Digital Display")
    public void showScreenshot(@HeaderParam("deviceId") String deviceId,
                               @HeaderParam("owner") String owner,
                               @HeaderParam("Authorization") String token,
                               @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.SCREENSHOT_CONSTANT + "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Get statistics of running display
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/get-device-status")
    @POST
    @Feature(code = "get-device-status", name = "Get Device Status", type = "operation",
            description = "Current status in Digital Display")
    public void getDevicestatus(@HeaderParam("deviceId") String deviceId,
                                @HeaderParam("owner") String owner,
                                @HeaderParam("Authorization") String token,
                                @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.GET_DEVICE_STATUS_CONSTANT +
                    "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Stop specific display
     *
     * @param deviceId id of the controlling digital display
     * @param owner    owner of the digital display
     * @param token    web socket id of the method invoke client
     * @param response response type of the method
     */
    @Path("/get-content-list")
    @POST
    @Feature(code = "get-content-list", name = "Get Content List", type = "operation",
            description = "Content List in Digital Display")
    public void getResources(@HeaderParam("deviceId") String deviceId,
                             @HeaderParam("owner") String owner,
                             @HeaderParam("Authorization") String token,
                             @Context HttpServletResponse response) {

        try {
            token = token.split(" ")[1];
            sendCommandViaMQTT(owner, deviceId, token + "::" + DigitalDisplayConstants.GET_CONTENTLIST_CONSTANT + "::", "");
            response.setStatus(Response.Status.OK.getStatusCode());
        } catch (DeviceManagementException e) {
            log.error(e);
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        } catch (DigitalDisplayException e) {
            log.error(e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }
    }

    /**
     * Send message via MQTT protocol
     *
     * @param deviceOwner owner of target digital display
     * @param deviceId    id of the target digital display
     * @param operation   operation need to execute
     * @param param       parameters need to given operation
     * @throws DeviceManagementException
     * @throws DigitalDisplayException
     */
    private void sendCommandViaMQTT(String deviceOwner, String deviceId, String operation,
                                    String param)
            throws DeviceManagementException, DigitalDisplayException {

        String topic = String.format(DigitalDisplayConstants.PUBLISH_TOPIC, deviceOwner, deviceId);
        String payload = operation + param;

        try {
            digitalDisplayMQTTConnector.publishToDigitalDisplay(topic, payload, 2, false);
        } catch (TransportHandlerException e) {
            String errorMessage = "Error publishing data to device with ID " + deviceId;
            throw new DigitalDisplayException(errorMessage, e);
        }
    }

}