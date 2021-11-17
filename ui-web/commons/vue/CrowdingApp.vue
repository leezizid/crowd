<template>
    <div v-if="appType=='vue'" :id="cid" :style="appStyle"></div>
    <div v-else-if="appType=='react'" :id="cid" style="height: 100%;"></div>
</template>

<script>
  import React from 'react'
  import ReactDOM from 'react-dom'
  import {queryAppInfo} from '../AppRegistry'
  import nextId from '../CID'
  import Vue from 'vue'

  export default {
    name: 'CrowdingApp',
    data () {
      return {
        appType: '',
        appInfo: {}
      }
    },
    props: {
      name: String,
      appStyle: String
    },
    computed: {
      cid: function () {
        return 'c' + nextId()
      }
    },
    watch: {

    },
    created: function () {
      // 查询组件信息
      this.appInfo = queryAppInfo(this.name)
      this.appType = this.appInfo?this.appInfo.type:'';
    },
    mounted: function () {
      let appName = this.name
      let cid = this.cid
      if(this.appType == 'vue') {
        let attrs = this.$attrs;
        let listeners = this.$listeners;
        new Vue({
          el: '#' + cid,
          template: `<` + appName + ` v-bind="bridgeAttrs"  v-on="bridgeListeners" />`,
          data: {
            bridgeAttrs: attrs,
            bridgeListeners: listeners
          }
        })
      } else if(this.appType == 'react') {
        let AppClazz = this.appInfo.clazz
        ReactDOM.render(<AppClazz />, document.getElementById(cid))
        // require('../../' + appName).renderApp(targetId)   //XXX：看能否优化不需要App自己实现renderApp方法
      }
    }
  }
</script>
