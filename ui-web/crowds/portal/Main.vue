<template>
  <KLayout style="height: 100%;">
    <KAside :collapse="collapse" fixed size="large">
      <div class="logo">CrowdStrategy</div>
      <KMenu :selectedKey.sync="activeApp" >
        <template v-for="(menu,key) in menuDatas">
          <KMenuItem v-if="!menu.children" :index="menu.appId" :key="menu.appId" @select="handleSelect">
            <KIcon v-if="menu.icon.type==='KIcon'" :class="menu.icon.class" />
            {{menu.appTitle}}
          </KMenuItem>
          <KMenuItem v-else-if="menu.children.length>0" :index="menu.appId" :key="menu.appId">
            <KIcon v-if="menu.icon.type==='KIcon'" :class="menu.icon.class" />
            {{menu.appTitle}}
            <KMenu>
              <KMenuItem v-for="(subMenu, subKey) in menu.children" :key="subMenu.appId" :index="subMenu.appId"  @select="handleSelect">
                {{subMenu.appTitle}}
              </KMenuItem>
            </KMenu>
          </KMenuItem>
        </template>
      </KMenu>
    </KAside>
    <KLayout>
      <KHeader fixed>
        <KButton type="none" size="large" style="height: 64px;" @click="_toggle">
          <KIcon class="ion-navicon" size="30" style="margin-left: -15px;"/>
        </KButton>
        <span>CrowdStrategy System @ </span>
        <span :style="connectStatusStyle">
          {{serverUrl}}
        </span>
        <!-- <span style="color: dodgerblue;">
          &nbsp;|&nbsp;{{connectStatus}}
        </span> -->
        <KDropdown trigger="click" style="float: right;margin-right:5px;line-height: 64px;height: 42px;">
          <KButton type="none">
            {{ loginUserName }} <KIcon class="ion-ios-arrow-down" />
          </KButton>
          <KDropdownMenu>
            <KDropdownItem>个人中心</KDropdownItem>
            <KDropdownItem @click="logout">注销</KDropdownItem>
          </KDropdownMenu>
        </KDropdown>
      </KHeader>
      <KBody class="body">
        <FeaturePage />
      </KBody>
    </KLayout>
  </KLayout>
</template>

<script>
  import CrowdingApp from '../../commons/vue/CrowdingApp'
  import {openWorkApp} from '../../reducers/workApps';

  import FeaturePage from './FeaturePage'
  import {commitState} from "../../commons/Crowding";
  import {updateLoginInfoAction} from "../../reducers/connectInfo";

  export default {
    name: "Main",
    components: {
      'CrowdingApp': CrowdingApp,
      'FeaturePage': FeaturePage
    },
    data() {
      return {
        menuDatas: [
          {appId: 'ActiveWorker', appName: 'ActiveWorker', appTitle: '后台服务监控', icon: {type: 'KIcon', class: 'ion-ios-timer'}},
          {appId: 'ChannelManage', appName: 'ChannelManage', appTitle: '通道管理', icon: {type: 'KIcon', class: 'ion-playstation'}},
          {appId: 'FuturesInfoView', appName: 'FuturesInfoView', appTitle: '期货合约信息', icon: {type: 'KIcon', class: 'ion-ios-paper-outline'}},
          {appId: 'StrategyManage', appName: 'StrategyManage', appTitle: '策略管理', icon: {type: 'KIcon', class: 'ion-ios-cog'}},
          {appId: 'BacktestManage', appName: 'BacktestManage', appTitle: '回测管理', icon: {type: 'KIcon', class: 'ion-clipboard'}},
          // {appId: 'query', appTitle: '查询', icon: {type: 'KIcon', class: 'ion-ios-search-strong'},
          //   children: [
          //     {appId: 'q1',appName: 'q1', appTitle: '查询1'},
          //     {appId: 'q2',appName: 'q2', appTitle: '查询2'},
          //     {appId: 'q3',appName: 'q3', appTitle: '查询3'}
          // ]}
        ],
        activeApp: 'TradeChannel',
        expandedKeys: [],
        collapse: false,
        loginUserName: '',
        serverUrl: '',
        connectStatus: 0,
        connectStatusStyle: 'color: red;'
      };
    },
    created () {
      // this.unsubscribeStates = this.$subscribeStates(['workApps'], this.workAppsListener)
      this.$subscribeStates(['workApps','serverInfo', 'connectInfo'], this.statesChanged);
    },
    mounted () {
      let defaultMenu = this.menuDatas[0]
      this.$commitState(openWorkApp(defaultMenu.appId, defaultMenu.appName, defaultMenu.appTitle, defaultMenu.appParams));
    },
    computed: {

    },
    methods: {
      statesChanged (workApps, serverInfo, connectInfo) {
        // console.log("statesChanged workApps:");
        // console.log(workApps);
        this.activeApp = workApps.active;
        // console.log("statesChanged serverInfo:");
        //console.log(serverInfo);
        this.serverUrl = serverInfo.url;
        // console.log("statesChanged connectInfo:");
        //console.log(connectInfo);
        let loginInfo = connectInfo.loginInfo;
        this.loginUserName = loginInfo.userName;
        this.connectStatus = connectInfo.status;if (this.connectStatus === 100) {
          this.connectStatusStyle = 'color: green;'
        } else {
          this.connectStatusStyle = 'color: red;'
        }
      },
      workAppsListener (param) {
        this.activeApp = param[0].active
      },
      _toggle() {
        this.collapse = !this.collapse;
      },
      handleSelect (item) {
        let k = item.get('key');
        for (let m of this.menuDatas) {
          if (m.appName && m.appId === k) {
            this.$commitState(openWorkApp(m.appId, m.appName, m.appTitle, m.appParams));
          }
          if (m.children && m.children.length>0) {
            for (let child of m.children) {
              if (child.appId === k) {
                this.$commitState(openWorkApp(child.appId, child.appName, child.appTitle, child.appParams));
              }
            }
          }
        }
      },
      logout () {
        console.log('logout');
        this.$invoke("/portal/logout", {}, (error, data) => {
          if(error) {
            alert(error.message)
          } else {
            commitState(updateLoginInfoAction(data));
          }
        });
      }
    }
  }
</script>
<style lang="scss" scoped>
  .body {
    //display: flex;
    //flex-direction: column;
    margin-left: 2px;
    margin-right: 0px;
    background-color: ghostwhite;
    overflow-y: auto;
  }

  .logo {
    height: 30px;
    line-height: 30px;
    text-align: center;
    color: #fff;
    background: gray;
    margin: 17px 20px;
    transition: all .25s ease-in-out;
  }
  .k-breadcrumb{
    margin: 20px 0
  }

  .k-collapsed > .logo {
    margin: 17px 5px;
  }
</style>
