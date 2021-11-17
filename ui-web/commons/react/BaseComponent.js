import React, {Component}  from 'react';
import {subscribeStates} from "../Crowding";
import {invokeWebProcessor, subscribeTopics} from "../WSChannel";


export default class BaseComponent extends Component {

    constructor(props) {
        super(props);
        //
        let stateNames = this.subscribeStates();
        if(stateNames && stateNames.length > 0) {
            this.unsubscribeStates = subscribeStates(stateNames, (stateObjects)=> {
                this.statesChanged.apply(this, stateObjects)
            })
        }
        //
        let topics = this.subscribeTopics();
        if(topics && topics.length > 0) {
            this.unsubscribeTopics = subscribeTopics(topics, (topic,message)=> {
                this.messageReceived(topic, message);
            });
        }
    }

    setState(state) {
        if (state == null) return
        if (this.$isMounted) {
            super.setState(state)
        } else if (this.state) {
            for (let n in state) {
                if (state.hasOwnProperty(n)) {
                    const v = state[n]
                    if (v === undefined) {
                        delete this.state[n]
                    } else {
                        this.state[n] = v
                    }
                }
            }
        } else {
            this.state = state
        }
    }

    UNSAFE_componentWillMount() {
        //标记mounted状态
        this.$isMounted = true
    }

    componentWillUnmount() {
        //标记willUnmount状态
        this.$isWillUnmount = true
        //清除状态订阅监听器
        if(this.unsubscribeStates) {
            this.unsubscribeStates();
        }
        //清除主题订阅监听器
        if(this.unsubscribeTopics) {
            this.unsubscribeTopics();
        }
    }

    //子类复写，返回需要监听的主题名称数组
    subscribeTopics() {
        return [];
    }

    //子类复写，监听主题消息
    messageReceived(topic, message) {

    }

    //子类复写，返回需要监听的状态名称数组
    subscribeStates() {
        return [];
    }

    //子类复写，监听状态变化
    statesChanged() {

    }

    invoke(apiName, params, callback) {
        invokeWebProcessor(apiName, params, (error, result) => {
            if(this.$isWillUnmount) {
                return
            }
            if(error && error.message == '设备登录过期') {
                //TODO：重定向登录界面
            }
            callback(error, result);
        })
    }

    invokeInterval(apiName, params, callback, interval) {
        let invokeFunc = () => {
            invokeWebProcessor(apiName, params, (error, result) => {
                if(this.$isWillUnmount) {
                    return
                }
                callback(error, result);
                this.intervalInvokeTimer = setTimeout(invokeFunc, interval);
            });
        }
        invokeFunc();
    }

    cancelIntervalInvoke() {
        if(this.intervalInvokeTimer) {
            clearTimeout(this.intervalInvokeTimer)
            this.intervalInvokeTimer = null;
        }
    }

}
