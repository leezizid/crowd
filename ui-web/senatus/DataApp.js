import React from 'react';
import BaseApp from '../commons/BaseApp';
import {registerApp} from '../commons/AppRegistry';
import {initStore} from '../commons/Crowding';
import dateRange from '../reducers/dateRange';
import currentUnit from '../reducers/currentUnit';
import workApps from '../reducers/workApps';
import connectInfo from '../reducers/connectInfo';
import serverInfo from '../reducers/serverInfo';


import DataView from '../crowds/portal/DataView';

initStore({dateRange,currentUnit,workApps,connectInfo,serverInfo})
registerApp('DataView','react', DataView)

export default class DataApp extends BaseApp {

  constructor(props) {
    super(props, '', 'ws://127.0.0.1:33333/websocket')
  }

  getTargetAppName(loginInfo) {
    return "DataView";
  }


}
