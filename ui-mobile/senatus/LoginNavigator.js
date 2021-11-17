import React from 'react';
import { createStackNavigator, createAppContainer } from 'react-navigation';
import {getNavigationOptionsHeader} from './Styles'
import BaseComponent from '../commons/BaseComponent'

export default class LoginNavigator extends BaseComponent {

    constructor(props) {
        super(props);
        this.appContainer = createAppContainer(createStackNavigator(
            this.createStackObjects(props.stackInfos)
        ));
    }

    subscribeStates() {
        return ['connectInfo'];
    }

    statesChanged(connectInfo) {
        this.setState({connectStatus: connectInfo.status});
    }

    createStackObjects(stackInfos) {
        let stackObjects = {};
        for(let i = 0; i < stackInfos.length; i++) {
            let stackObject = {};
            stackObject.screen = stackInfos[i].view;
            stackObject.navigationOptions = ()=> ({
                headerTitle: stackInfos[i].title + (this.state.connectStatus == 0 ? '(未连接)' : (this.state.connectStatus == 10 ? '(连接中)' : '')),
                ...getNavigationOptionsHeader(0),
            })
            stackObjects[stackInfos[i].title] = stackObject;
        }
        return stackObjects;
    }


    renderContent() {
        return <this.appContainer></this.appContainer>;
    }
}