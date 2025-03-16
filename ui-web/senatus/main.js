
import Vue from 'vue'
import React from 'react';
import ReactDOM from 'react-dom';

import {commitState, subscribeStates} from '../commons/Crowding';
import {invokeWebProcessor} from '../commons/WSChannel';

import Kpc from 'kpc-vue';

Vue.use(Kpc);

Vue.config.productionTip = false

Vue.prototype.$commitState = commitState
// Vue.prototype.$subscribeStates = subscribeStates
Vue.prototype.$invoke = (apiName, params, callback) => {
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

Vue.mixin({
  methods: {
    $subscribeStates: (stateNames, statesChanged) => {
      if(stateNames && stateNames.length > 0) {
        this.$unsubscribeStates = subscribeStates(stateNames, (stateObjects)=> {
          statesChanged.apply(this, stateObjects)
        })
      }
    }
  },
  beforeDestroy: () => {
    this.$unsubscribeStates();
  }
})

//import App from '../senatus/DemoApp';
import App from '../senatus/DataApp';

//
ReactDOM.render(<App />, document.getElementById('app'));
