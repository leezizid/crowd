import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import Vue from 'vue'
import {queryAppInfo} from '../AppRegistry'
import nextId from '../CID'


export default class CrowdingApp extends Component {

  constructor(props) {
    super(props)
    this.initAppInfo(this.props.name);
  }

  initAppInfo(appName) {
    this.appName = appName;
    this.appInfo = queryAppInfo(this.appName)
    this.appType = this.appInfo.type;
    this.appClazz = this.appInfo.clazz;
    this.cid = 'c' + nextId()
    this.tid = this.appType == 'vue' ?  ('vue_' + this.cid) : this.cid;
  }

  shouldComponentUpdate(nextProps) {
    if(this.props.name !== nextProps.name) {
      this.initAppInfo(nextProps.name);
      return true;
    }
    return false;
  }

  componentDidUpdate() {
    this.renderCrowdingApp()
  }

  componentDidMount() {
    this.renderCrowdingApp()
  }

  renderCrowdingApp() {
    let AppClazz = this.appClazz
    if(this.appType == 'vue') {
      new Vue({
        el: '#' + this.tid,
        template: '<' + this.appName + '/>'
      })
    } else if(this.appType == 'react') {
      ReactDOM.render(<AppClazz />, document.getElementById(this.tid));
    }
  }

  render() {
    if(this.appType == 'vue') {
      return (
        <div key={this.cid} id={this.cid} style={this.props.style}>
          <div id={this.tid} ></div>
        </div>
      );
    } else if(this.appType == 'react') {
      return (
        <div key={this.cid} id={this.cid} style={this.props.style}></div>
      );
    }
  }
}
