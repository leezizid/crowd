import Vue from 'vue'
import React from 'react';

const appRegistry = {};

export function registerApp(appName, appType, appClass) {
  let appInfo = {type : appType, clazz : appClass};
  appRegistry[appName] = appInfo;
  if(appType == 'vue') {
    Vue.component(appName, appClass);
  } else if(appType == 'react') {

  }
}

export function queryAppInfo(appName) {
  return appRegistry[appName]
}
