import React from 'react';
import { createStackNavigator, createBottomTabNavigator, createAppContainer } from 'react-navigation';
import {Text} from 'react-native';
import Ionicons from 'react-native-vector-icons/FontAwesome';
import {getNavigationOptionsHeader,colors} from './Styles'
import BaseComponent from '../commons/BaseComponent'

export default class MainNavigator extends BaseComponent {

    constructor(props) {
        super(props);
        this.appContainer = createAppContainer(createStackNavigator(
            {
                Main: {
                    screen: createBottomTabNavigator(this.createTabScreens(props.tabInfos),{
                        // initialRouteName : props.tabInfos[0].title,
                        lazy: false,
                        tabBarOptions: {
                            activeTintColor: colors.c1,
                            inactiveTintColor: colors.c3,
                            showIcon: true,
                            style: {
                                backgroundColor: colors.c2,
                                height: 56
                            },
                            labelStyle: {
                                fontSize: 12,
                            },
                            indicatorStyle: {
                                height:0
                            }
                        },
                        navigationOptions: ({ navigation }) => ({
                            headerTitle: props.tabInfos[navigation.state.index].title + (this.state.connectStatus == 0 ? '(未连接)' : (this.state.connectStatus == 10 ? '(连接中)' : '')),
                            ...getNavigationOptionsHeader(0)
                        })
                    })
                },
                ...this.createStackObjects(props.stackInfos)
            }, {
                initialRouteName: 'Main',
            }
        ));
    }

    subscribeStates() {
        return ['connectInfo'];
    }

    statesChanged(connectInfo) {
        this.setState({connectStatus: connectInfo.status});
    }


    createTabScreen(tabInfo) {
        return {
            screen: tabInfo.view,
            navigationOptions: {
                tabBarLabel: ({ tintColor, focused }) => (
                    <Text style={{ color: tintColor, fontSize:12, fontWeight: '500', marginBottom: 4}}>{tabInfo.title}</Text>
                ),
                tabBarIcon: ({ tintColor, focused }) => (
                    <Ionicons
                        name={focused ? tabInfo.activeIcon : tabInfo.normalIcon}
                        size={20}
                        style={{ color: tintColor }}
                    />
                )
            }
        }
    }

    createTabScreens(tabInfos) {
        let screens = {};
        for(let i = 0; i < tabInfos.length; i++) {
            screens[tabInfos[i].title] = this.createTabScreen(tabInfos[i]);
        }
        return screens;
    }


    createStackObjects(stackInfos) {
        let stackObjects = {};
        for(let i = 0; i < stackInfos.length; i++) {
            let stackObject = {};
            stackObject.screen = stackInfos[i].view;
            stackObject.navigationOptions = ({navigation})=> ({
                headerTitle: (navigation.state.params.title ? navigation.state.params.title: stackInfos[i].title) + (this.state.connectStatus == 0 ? '(未连接)' : (this.state.connectStatus == 10 ? '(连接中)' : '')),
                ...getNavigationOptionsHeader(0),
            })
            stackObjects[stackInfos[i].title] = stackObject;
        }
        return stackObjects;
    }


    renderContent() {
        return <this.appContainer />;
    }
}