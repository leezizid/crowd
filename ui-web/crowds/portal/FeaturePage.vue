<template>
  <div style="height: 100%;">
    <KTabs v-model="activeApp"
           :closable="workApps.length > 1"
           type="no-border-card"
           style="height: 36px;"
           @remove="closeOnTab"
    >
      <KTab v-for="(tab, key) in workApps"
            :value="tab.appId"
            :key="tab.appId"
            @click="clickOnTab(tab)"
      >
        {{ tab.appTitle }}
      </KTab>
    </KTabs>
    <div style="height: calc(100% - 36px);overflow-y: auto;" v-for="(app,index) in workApps" v-show="app.appId===activeApp" :key="app.appId">
      <CrowdingApp :key="app.appId" :name="app.appName" :appType="app.appParams?app.appParams.appType:''" />
    </div>
  </div>
</template>

<script>
  import CrowdingApp from '../../commons/vue/CrowdingApp'
  import {subscribeStates} from '../../commons/Crowding'
  import {openWorkApp, closeWorkApp, activeWorkApp} from '../../reducers/workApps';

  export default {
    name: "FeaturePage",
    components: {
      'CrowdingApp': CrowdingApp
    },
    props: {

    },
    data () {
      return {
        workApps:[],
        activeApp: ''
      }
    },
    watch: {

    },
    computed: {

    },
    created () {

    },
    mounted () {
      subscribeStates(['workApps'], this.workAppsListener)
    },
    methods: {
      workAppsListener: function (param) {
        this.setWorkApps(param[0].opened)
        this.activeApp = param[0].active
      },
      clickOnTab: function (tab){
        //设置active状态
        this.$commitState(activeWorkApp(tab.appId))
      },
      closeOnTab: function (tabId) {
        //删除当前功能节点
        this.$commitState(closeWorkApp(tabId))
      },
      setWorkApps: function (apps) {
        this.workApps = Object.assign(apps)
      }
    }
  }
</script>

<style lang="scss" scoped>

</style>
