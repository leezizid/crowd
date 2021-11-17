
import React from 'react';
import {AppState} from 'react-native'
import BaseApp from "./BaseApp";
import {commitState} from "./Crowding";
import {updateConnectStatusAction, updateLastMessageTimeAction} from "../reducers/connectInfo";
import DeviceInfo from 'react-native-device-info'
import uuid from 'uuid';

/**
 1、创建WS对象时，标记进入WS_CONNECTING状态
 2、onopen，进入WS_CONNECTED状态；
 3、onerror
 （1）如果当前状态为WS_CONNECTING，则等同于onclose处理
 （2）如果为其他状态，忽略不处理
 4、onclose
 （1）如果当前状态为WS_CONNECTING或者WS_CONNECTED，改为WS_CLOSE状态；如果APP处于前台，会在1秒后发起一个连接定时，进入WS_WAIT_CONNECTING状态；如果APP在后台，则保持WS_CLOSE状态
 （2）如果当前状态不为WS_CONNECTING或者WS_CONNECTED，则忽略（可能onerror已经触发）
 */

const TIME_WS_CHECK = 30 * 1000; //检查WS状态的间隔
const TIME_WS_CHECK_TIMEOUT = 45 * 1000; //检查时间出现异常的时间（即最后消息时间不应该超过这个时间，超过即认为掉线自动重连）

const TIME_WS_RECONNECT_WAIT = 2000; //WS断开后重连等待时间

const INVOKE_WAIT_TIMEOUT = 10 * 1000;  //调用服务等待超时时间

const WS_CLOSE = 0;           //已经关闭，属于稳定状态，可能由关闭时间定时或者在APP转到前台时触发转至WS_WAIT_CONNECTING，
const WS_WAIT_CONNECTING = 1; //已经发起一个连接定时器，准备进行连接；属于临时状态，一定会在短期内转至WS_CONNECTING或者WS_CLOSE
const WS_CONNECTING = 10;      //正在连接；属于临时状态，一定会在短期内转至WS_CONNECTED或者WS_CLOSE
const WS_CONNECTED = 100;       //已经连接；属于稳定状态，在关闭后转为WS_CLOSE

let WS_status = WS_CLOSE;
let WS_lastMessageTime = 0;

let wsUrl = null;
let websocket = null;
let checkMonitorId = null;
let webprocessorInvokers = [];
let topicMessageListeners = [];
let sessionId = null;

export function setWSUrl(url) {
    wsUrl = url;
}

export function initWS() {
    if(checkMonitorId != null) {
        try {
            clearTimeout(checkMonitorId);
        } catch(e){
        }
    }
    let checkMonitor = ()=> {
        handleCheckWSStatus();
        checkMonitorId = setTimeout(checkMonitor, TIME_WS_CHECK)
    };
    checkMonitor();
}

export function closeWS() {
    try {
        websocket.close();
    } catch(e){
    }
    try {
        clearTimeout(checkMonitorId);
    } catch(e){
    }
}

//
function handleCheckWSStatus() {
    if(WS_status == WS_WAIT_CONNECTING || WS_status == WS_CONNECTING ) {
        return;
    }
    if(WS_status == WS_CLOSE) {
        handleConnect();
        return;
    }
    if(WS_status == WS_CONNECTED) {
        let idleTime = new Date().getTime() - WS_lastMessageTime;
        if(idleTime < TIME_WS_CHECK_TIMEOUT) {
            try {
                websocket.send(JSON.stringify({topic: 'system.check', mid: uuid.v1()}));
                // alert("checked")
            } catch(e) {
            }
            return;
        } else {
            try {
                websocket.close();
            } catch(e) {
            }
            return;
        }
    }
}

function handleConnect() {
    if(WS_status == WS_WAIT_CONNECTING) {
        return;
    }
    if(AppState.currentState != 'active') {
        handleWSClosed();
        return;
    }
    try {
        if(!wsUrl) {
            throw new Error();
        }
        websocket = new WebSocket(wsUrl);
        commitState(updateConnectStatusAction(WS_status = WS_CONNECTING));
        websocket.onopen = () => {
            commitState(updateConnectStatusAction(WS_status = WS_CONNECTED), updateLastMessageTimeAction(WS_lastMessageTime = new Date().getTime()));
            websocket.send(JSON.stringify({topic: 'system.register', mid: uuid.v1(), clientInfo: {}, sessionId: sessionId, content: {topics: getSubscribeTopics()}}));
        };
        websocket.onmessage = e => {
            try {
                let messageObject =  JSON.parse(e.data);
                if(messageObject)  {
                    if(messageObject.sessionId) {
                        sessionId = messageObject.sessionId;
                        // alert(sessionId)
                    }
                    let topic = messageObject.topic;
                    let mid = messageObject.mid;
                    let content = messageObject.content;
                    if(topic == 'system.webprocessor') {
                        let invoker = webprocessorInvokers[mid];
                        delete webprocessorInvokers[mid];
                        if(invoker) {
                            clearTimeout(invoker.timerId)
                            if(content.data) {
                                let requireAppVersion = content.data.$requireAppVersion;
                                if(requireAppVersion && requireAppVersion > BaseApp.app_version) {
                                    BaseApp.instance.update(requireAppVersion)
                                    // alert(requireAppVersion);
                                    //invoker.callback(null, content.data);
                                } else {
                                    invoker.callback(null, content.data);
                                }
                            } else if(content.error) {
                                invoker.callback(content.error);
                            } else {
                                callback(content);
                            }
                        }
                    } else {
                        for(let i = 0; i < topicMessageListeners.length; i++) {
                            if(topicMessageListeners[i].matchTopic(topic)) {
                                topicMessageListeners[i].notify(topic, content)
                            }
                        }
                    }
                }
            } catch(e) {

            }
            //标记最后通讯时间
            commitState(updateLastMessageTimeAction(WS_lastMessageTime = new Date().getTime()));
        };
        websocket.onerror = e => {
            if(WS_status == WS_CONNECTING) {
                handleWSClosed();
            }
        };
        websocket.onclose = e => {
            if(WS_status == WS_CONNECTING || WS_status == WS_CONNECTED) {
                handleWSClosed();
            }
        };
    } catch(e) {
        handleWSClosed();
    }
}

function handleWSClosed() {
    commitState(updateConnectStatusAction(WS_status = WS_CLOSE));
    websocket = null;
    setTimeout(()=>{
        handleCheckWSStatus();
    }, TIME_WS_RECONNECT_WAIT);
    //TODO：清空所有调用过程
    let invokers = webprocessorInvokers;
    for(let k in invokers) {
        let invoker = invokers[k];
        clearTimeout(invoker.timerId);
        invoker.callback(new Error('WS连接关闭'));
    }
    webprocessorInvokers = [];
}

function getSubscribeTopics() {
    let topicSet = [];
    for(let i = 0; i < topicMessageListeners.length; i++) {
        let listenerTopics = topicMessageListeners[i].getTopics();
        for(let j = 0; j< listenerTopics.length; j++) {
            topicSet[listenerTopics[j]] = true;
        }
    }
    let topics = [];
    for(let topic in topicSet) {
        topics.push(topic);
    }
    return topics;
}

function sendSubscribeMessage() {
    if(WS_status == WS_CONNECTED) {
        websocket.send(JSON.stringify({topic: 'system.subscribe', mid: uuid.v1(), content: {topics: getSubscribeTopics()}}));
    }
}


export function getSessionId() {
    return sessionId;
}

export function getInvokerSize() {
    let size = 0;
    for(let k in webprocessorInvokers) {
        size++;
    }
    return size;
}


export function invokeWebProcessor(apiPath, params, callback) {
    if(WS_status != WS_CONNECTED) {
        callback(new Error('网络已断开'));
        return;
    }
    let dotIndex = apiPath.indexOf('.');
    let processorName = apiPath.substring(0, dotIndex);
    let apiName = apiPath.substring(dotIndex + 1);
    let mid = uuid.v1();
    //
    let timerId = setTimeout(()=> {
        let invoker = webprocessorInvokers[mid];
        delete webprocessorInvokers[mid];
        if(invoker) {
            invoker.callback(new Error('等待结果超时'));
        }
    }, INVOKE_WAIT_TIMEOUT);
    webprocessorInvokers[mid] = {callback, timerId};
    //
    let msg = {topic: 'system.webprocessor', mid, content: {processorName, methodName: apiName, params}};
    if(apiName.indexOf('login') != -1 || apiName.indexOf('logout') != -1 || apiName.indexOf('queryUserInfo') != -1) {
        msg.methodCategory = 'session'
    }
    params.$deviceId = DeviceInfo.getUniqueID();
    params.$deviceCode = DeviceInfo.getUniqueID();
    params.$deviceName = DeviceInfo.getDeviceName();
    params.$version = BaseApp.app_version;
    websocket.send(JSON.stringify(msg));
}

export function subscribeTopics(topics, func) {
    let listener = new TopicMessageListener(topics, func);
    topicMessageListeners.push(listener);
    sendSubscribeMessage();
    return function() {
        let index = topicMessageListeners.indexOf(listener);
        if(index >= 0) {
            topicMessageListeners.splice(index, 1);
        }
        sendSubscribeMessage();
    }
}

class TopicMessageListener {

    constructor(topics,func) {
        this.topics = topics;
        this.func = func;
    }

    getTopics() {
        return this.topics;
    }

    matchTopic(topic) {
        return this.topics.indexOf(topic) >= 0;
    }

    notify(topic, message) {
        this.func(topic, message)
    }

}
