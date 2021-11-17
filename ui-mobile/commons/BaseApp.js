import React from 'react';
import {View, Keyboard, TouchableWithoutFeedback, AppState} from 'react-native'
import AsyncStorage from '@react-native-community/async-storage'
import BaseComponent from "./BaseComponent";
import {isFirstTime,markSuccess} from 'react-native-update';
import DeviceInfo from 'react-native-device-info'
import {createStackNavigator} from 'react-navigation';
import {setWSUrl, initWS,closeWS} from './WSChannel';
import {initStore} from "./Crowding";
import connectInfo, {updateLoginInfoAction} from "../reducers/connectInfo";
import serverInfo, {updateServerInfoAction} from "../reducers/serverInfo";
import {commitState} from "../commons/Crowding"

//XXX：这里createStackNavigator仅仅是为了防止运行期警告
createStackNavigator({Login: {screen: View}});

//初始化Store
initStore({connectInfo, serverInfo})

export default class BaseApp extends BaseComponent {

    constructor(props, app_title, app_version, app_logo, default_name, default_url) {
        super(props);
        BaseApp.app_title = app_title;
        BaseApp.app_version = app_version;
        BaseApp.app_logo = app_logo;
        BaseApp.default_name = default_name;
        BaseApp.default_url = default_url;
        BaseApp.instance = this;
        if (isFirstTime) {
            markSuccess();
        }
        this.setState({
            appState: AppState.currentState,
            updateFlag : false
        });
    }

    subscribeStates() {
        return ['serverInfo', 'connectInfo'];
    }

    //XXX：注意statesChanged在父类初始化中触发第一次调用
    statesChanged(serverInfo, connectInfo) {
        if(this.serverInfo !== serverInfo) {
            this.serverInfo = serverInfo;
            closeWS();
            setWSUrl(serverInfo.url);
            if(serverInfo.url) {
                initWS();
            }
       }
       this.setState({loginInfo : connectInfo.loginInfo});
    }

    componentDidMount() {
        AppState.addEventListener('change', this._handleAppStateChange);
        AsyncStorage.getItem("serverInfo",(err, serverInfo)=>{
            if(serverInfo != null) {
                try {
                    let infoObj = JSON.parse(serverInfo)
                    if(infoObj.url) {
                        commitState(updateServerInfoAction(infoObj.name, infoObj.url))
                        return;
                    }
                } catch(e) {
                }
            }
            commitState(updateServerInfoAction(BaseApp.default_name, BaseApp.default_url));
        });
    }


    componentWillUnmount() {
        super.componentWillUnmount();
        AppState.removeEventListener('change', this._handleAppStateChange);
        closeWS();
    }

    _handleAppStateChange = (nextAppState) => {
        if (this.state.appState.match(/inactive|background/) && nextAppState === 'active') {
            initWS();
        } else if(nextAppState === 'background')  {
            closeWS();
        }
        this.setState({appState: nextAppState});
    }

    //
    subscribeTopics() {
        return ['system.register'];
    }

    //子类复写，监听主题消息
    messageReceived(topic, message) {
        if(topic == 'system.register') {
            // let userInfo = {id: message.userId, name: message.userName};
            if(message.userName) { //如果服务器返回用户UserName，说明该会话已经建立登录信息
                let loginInfo = this.state.loginInfo;
                if(loginInfo == null || loginInfo.userName != message.userName) {
                    commitState(updateLoginInfoAction({}))
                }
            } else {
                AsyncStorage.getItem("userName", (err, userName) => {
                    if(userName){
                        let params = {
                            "userName": userName,
                            "deviceCode" : DeviceInfo.getUniqueID(),
                            "deviceName" : DeviceInfo.getDeviceName(),
                            "version" : BaseApp.app_version,
                            "phoneNumber": userName,
                            "deviceId" : DeviceInfo.getUniqueID()
                        }
                        this.invoke("utop_app.queryUserInfo", params, (error, data) => {
                            if (error) {
                                commitState(updateLoginInfoAction({}))
                            } else {
                                commitState(updateLoginInfoAction(data))
                            }
                        });
                    } else {
                        commitState(updateLoginInfoAction({}))
                    }
                });
            }
        }
    }

    update(newVersion) {
        this.setState({updateFlag : true, newVersion: newVersion});
    }


    render() {
        if(this.state.updateFlag === true) {
            return this.renderUpdate(BaseApp.app_version, this.state.newVersion);
        }
        let loginInfo = this.state.loginInfo;
        if(loginInfo == null) {
            return this.renderDefault();
        }
        if(loginInfo.userName && loginInfo.userName != null) {
            return this.renderMain();
        }
        return this.renderLogin();
    }

    renderDefault() {

    }

    renderLogin() {

    }


    renderMain() {

    }


    renderUpdate(currentVersion, newVersion) {

    }

}
