import React from 'react';
import BaseApp from '../commons/BaseApp';
import {registerApp} from '../commons/AppRegistry';
import {initStore} from '../commons/Crowding';


import LoginView from '../crowds/portal/Login.vue';
import MainView from '../crowds/portal/Main.vue';
import dateRange from '../reducers/dateRange';
import currentUnit from '../reducers/currentUnit';
import workApps from '../reducers/workApps';
import connectInfo from '../reducers/connectInfo';
import serverInfo from '../reducers/serverInfo';
import ActiveWorkerView from '../crowds/apps/ActiveWorkerView';
import ChannelManage from '../crowds/apps/ChannelManage';
import StrategyManage from '../crowds/apps/StrategyManage';
import BacktestManage from '../crowds/apps/BacktestManage';


initStore({dateRange,currentUnit,workApps,connectInfo,serverInfo})
registerApp('Main','vue', MainView)
registerApp('Login', 'vue', LoginView)
registerApp('ActiveWorker','react', ActiveWorkerView)
registerApp('ChannelManage','react', ChannelManage)
registerApp('StrategyManage','react', StrategyManage)
registerApp('BacktestManage','react', BacktestManage)

export default class DemoApp extends BaseApp {

  constructor(props) {
    super(props, '', 'ws://192.168.100.118:33333/websocket')
  }


  getTargetAppName(loginInfo) {
    return loginInfo && loginInfo.userName ? 'Main' : 'Login';
  }


}
