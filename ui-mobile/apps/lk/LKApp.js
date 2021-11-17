import React from 'react';
import BaseApp from '../../commons/BaseApp'
import MainNavigator from '../../senatus/MainNavigator'
import DefaultView from '../../crowds/base/DefaultView'
import MessageView from '../../crowds/lk/MessageView'
import ContactView from '../../crowds/lk/ContactView'
import SettingsView from '../../crowds/base/SettingsView'
import UpdateView from '../../crowds/base/UpdateView'
import UpdatePwdView from '../../crowds/base/UpdatePwdView'

const appLogo = require("./image/logo.png");

const tabInfos = [
    {
        view: MessageView,
        title: '消息',
        normalIcon: 'paper-plane-o',
        activeIcon: 'paper-plane'
    },
    {
        view: ContactView,
        title: '联系人',
        normalIcon: 'address-card-o',
        activeIcon: 'address-card'
    },
    {
        view: SettingsView,
        title: '个人中心',
        normalIcon: 'user-o',
        activeIcon: 'user'
    }
]

const mainStackInfos = [
    {
        title : '修改密码',
        view: UpdatePwdView
    }
]

export default class LKApp extends BaseApp {

    constructor(props) {
        super(props, "唠嗑", 20210201001, appLogo, "LK", "ws://127.0.0.1:58888/websocket");
    }

    renderDefault() {
        return <DefaultView  />;
    }

    renderLogin() {
        // return <LoginNavigator loginView={LoginView} />;
        return <MainNavigator tabInfos={tabInfos} stackInfos={mainStackInfos}/>;
    }


    renderMain() {
        return <MainNavigator tabInfos={tabInfos} stackInfos={mainStackInfos}/>;
    }


    renderUpdate(currentVersion, newVersion) {
        return <UpdateView newVersion={newVersion} currentVersion={currentVersion} logoImage={BaseApp.app_logo}/>
    }

}