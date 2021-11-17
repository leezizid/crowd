import React from 'react';
import CrowdingApp from './react/CrowdingApp'
import BaseComponent from './react/BaseComponent'
import {setWSUrl, initWS, closeWS} from './WSChannel'
import {commitState} from "./Crowding"
import {updateServerInfoAction} from "../reducers/serverInfo";
import {updateLoginInfoAction} from "../reducers/connectInfo";


export default class BaseApp extends BaseComponent {

  constructor(props, name, url) {
    super(props)
    BaseApp.default_name = name;
    BaseApp.default_url = url;
  }

  subscribeStates() {
    return ['serverInfo', 'connectInfo'];
  }

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
    let serverInfo = localStorage.serverInfo;
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
  }

  componentWillUnmount() {
    super.componentWillUnmount();
    closeWS();
  }


  //
  subscribeTopics() {
    return ['system.register'];
  }

  //
  messageReceived(topic, message) {
    if(topic == 'system.register') {
      // let userInfo = {id: message.userId, name: message.userName};
      if(message.userName) { //如果服务器返回用户UserName，说明该会话已经建立登录信息
        let loginInfo = this.state.loginInfo;
        if(loginInfo == null || loginInfo.userName != message.userName) {
          commitState(updateLoginInfoAction({userId: message.userId, userName: message.userName}))
        }
      } else {
        commitState(updateLoginInfoAction({userId: '', userName: ''}))
      }
    }
  }

  getTargetAppName(loginInfo) {
    //子类复写，返回相应的登录或者主页面的CrowdingApp名称
  }

  render() {
    return this.state.loginInfo ? <CrowdingApp name={this.getTargetAppName(this.state.loginInfo)} style={{height: '100%', width: '100%'}} appStyle="width: '100%';height: '100%'"/> : <div></div>;
  }


}
