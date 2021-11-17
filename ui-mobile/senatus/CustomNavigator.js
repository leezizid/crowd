import React from 'react';
import { createStackNavigator, createBottomTabNavigator, createAppContainer } from 'react-navigation';
import {Text, View, TouchableOpacity, TextInput, Platform, Modal, Image} from 'react-native';
import Ionicons from 'react-native-vector-icons/FontAwesome';
import {getNavigationOptionsHeader,colors} from './Styles'
import BaseApp from '../commons/BaseApp';
import BaseComponent from '../commons/BaseComponent'
import {updateLoginInfoAction} from "../reducers/connectInfo";
import {commitState} from "../commons/Crowding";
import DeviceInfo from 'react-native-device-info'
import AsyncStorage from '@react-native-community/async-storage'

export default class CustomNavigator extends BaseComponent {

    constructor(props) {
        super(props);
        this.appContainer = createAppContainer(createStackNavigator(
            {
                Main: {
                    screen: MainScreen,
                    navigationOptions: ({navigation}) => ({
                        header: null, //设置页面有无标题,
                        headerBackTitle: '返回'
                    }),
                    params: {tabInfos : this.props.tabInfos}
                },
                ...this.createStackObjects(props.stackInfos)
            }, {
                initialRouteName: 'Main',
            }
        ));
    }

    createStackObjects(stackInfos) {
        let stackObjects = {};
        for(let i = 0; i < stackInfos.length; i++) {
            let stackObject = {};
            stackObject.screen = stackInfos[i].view;
            stackObject.navigationOptions = ({navigation})=> ({
                headerTitle: (navigation.state.params.title ? navigation.state.params.title: stackInfos[i].title) + (this.state.connectStatus == 0 ? '' : (this.state.connectStatus == 10 ? '' : '')),
                ...getNavigationOptionsHeader(0),
            })
            stackObjects[stackInfos[i].title] = stackObject;
        }
        return stackObjects;
    }

    subscribeStates() {
        return ['connectInfo'];
    }

    statesChanged(connectInfo) {
        this.setState({connectStatus: connectInfo.status});
    }


    renderContent() {
        return <this.appContainer />;
    }
}

class MainScreen extends BaseComponent {

    constructor(props) {
        super(props);
        this.setState({activeTab: 0, activeParams:{}, lastUserName: '', showMenu: false});
    }

    componentDidMount(){
        AsyncStorage.getItem("userName",(err, userName)=>{
            if(userName){
                this.userName = userName;
                this.setState({lastUserName: userName});
            }
        });
    }

    subscribeStates() {
        return ['connectInfo'];
    }

    statesChanged(connectInfo) {
        if(connectInfo && connectInfo.loginInfo && connectInfo.loginInfo.units && connectInfo.loginInfo.units.length > 0) {
            let unitInfo = connectInfo.loginInfo.units[0];
            this.setState({unitInfo});
        } else {
            this.setState({unitInfo: null});
        }
        this.setState({connectStatus: connectInfo.status});
        if(connectInfo.status != 100) {
            this.hideMenu();
        }
    }

    login() {
        if(!this.userName){
            this.showInfo("请输入用户名!");
            return;
        }
        let params = {
            "userName": this.userName,
            "deviceCode" : DeviceInfo.getUniqueID(),
            "deviceName" : DeviceInfo.getDeviceName(),
            "version" : BaseApp.app_version,
            "phoneNumber": this.userName,
            "deviceId" : DeviceInfo.getUniqueID(),
            "password" : this.userPwd
        }
        this.invoke("utop_app.login", params, (error, data) => {
            if(error) {
                this.showInfo(error.message)
            } else {
                commitState(updateLoginInfoAction(data))
                AsyncStorage.setItem('userName', this.userName);
                this.setState({lastUserName: this.userName, activeTab: 0, activeParams:{}});
            }
        });
    }

    showMenu() {
        if(!this.state.unitInfo) {
            return;
        }
        this.setState({showMenu: true})
    }

    hideMenu() {
        this.setState({showMenu: false})
    }

    modifyPwd() {
        this.hideMenu();
        this.props.navigation.navigate('修改密码',{})
    }

    logout() {
        this.hideMenu();
        this.invoke("utop_app.logout",{},(error, data)=>{
            if(error) {
                this.showInfo(error.message)
            } else {
                commitState(updateLoginInfoAction({}));
                this.userPwd = '';
            }
        });
    }

    changeTabView(index, params) {
        this.setState({activeTab: index, activeParams: params})
    }

    renderContent() {
        let tabInfos = this.props.navigation.state.params.tabInfos;
        let tabs = [];
        for(let i = 0; i < tabInfos.length; i++) {
            let tabInfo = tabInfos[i];
            tabs.push(<TouchableOpacity onPress={()=>{
                if(this.state.unitInfo) {
                    this.setState({activeTab: i, activeParams:{}});
                }
            }} key={"tab" + i} style={{flex:1, flexDirection: 'column', justifyContent:'center',alignItems:'center'}}>
                <View style={{flex:1}}></View>
                <Ionicons
                    name={this.state.activeTab == i && this.state.unitInfo ?  tabInfo.activeIcon : tabInfo.normalIcon}
                    size={20}
                    style={{ color: this.state.activeTab == i && this.state.unitInfo ? '#F36628' : 'gray'}}
                />
                <Text style={{marginTop: 5, color: this.state.activeTab == i && this.state.unitInfo ? '#F36628' : 'gray'}}>{tabInfo.title}</Text>
                <View style={{flex:1}}></View>
            </TouchableOpacity>)
        }
        let TabView = tabInfos[this.state.activeTab].view;
        return <View style={{flexDirection: 'column', flex: 1}}>
            <View style={{height:Platform.OS == 'ios' ? 72 : 46, backgroundColor:'#F36628', justifyContent:'center',alignItems:'center'}}>
                <View style={{height: Platform.OS == 'ios' ? 26 : 0}}></View>
                <View style={{flex:1}}></View>
                <View style={{flexDirection: 'row'}}>
                    <View style={{width: 50}}></View>
                    <View style={{flex:1}}></View>
                    <Text style={{fontSize: 18, fontWeight: '500', color: 'white'}}>{this.state.unitInfo ? tabInfos[this.state.activeTab].title : '登录'}</Text>
                    <View style={{flex:1}}></View>
                    <TouchableOpacity style={{width:50, flexDirection:'row'}} onPress={()=>{
                        this.showMenu();
                    }}>
                        <View style={{flex:1}}/>
                        <Ionicons
                            name={this.state.connectStatus == 0 ? 'hourglass-o' : (this.state.connectStatus == 10 ? 'hourglass-half' : 'user-o')}
                            size={20}
                            style={{ color: 'white'}}
                        />
                        <View style={{flex:1}}/>
                    </TouchableOpacity>
                </View>
                <View style={{flex:1}}></View>
            </View>
            {/*<Text style={{backgroundColor:'#FEEBDF', textAlign:'center', paddingHorizontal:10, paddingVertical:5, fontSize:12}}>数据更新于3秒前</Text>*/}
            <View style={{flex:1, backgroundColor:'white'}}>
                {
                    this.state.unitInfo ?
                        <TabView unitInfo={this.state.unitInfo} $upper={this} params={this.state.activeParams} />
                        :
                        <View style={{marginTop:20, marginLeft:15, marginRight:15}}>
                            <Text style={{fontSize: 18, fontWeight: '500', marginTop: 10, color:'gray'}}>用户</Text>
                            <TextInput underlineColorAndroid='transparent' style={{borderWidth:1, borderColor: 'lightgrey', fontSize: 18, color: 'gray', marginTop: 5, padding:8}}
                                       onChangeText={(text)=>{this.userName = text;}}
                                       defaultValue={this.state.lastUserName}/>
                            <Text style={{fontSize: 18, fontWeight: '500', marginTop: 10, color:'gray'}}>密码</Text>
                            <TextInput underlineColorAndroid='transparent' style={{borderWidth:1, borderColor: 'lightgrey', fontSize: 18, color: 'gray', marginTop: 5, padding:8}}
                                       onChangeText={(text)=>{this.userPwd = text;}}
                                       secureTextEntry={true} onEndEditing={()=>{
                                           //this.login();
                            }}/>
                            <TouchableOpacity style={{backgroundColor:'#F36628', height:40, alignItems: 'center', marginTop: 10}} onPress={()=>{
                                this.login();
                            }}>
                                <View style={{flex:1}}></View>
                                <Text style={{color:'white', fontSize: 18, fontWeight: '500'}}>确定</Text>
                                <View style={{flex:1}}></View>
                            </TouchableOpacity>
                            <Text style={{fontSize: 12, marginTop: 30, color:'lightgrey'}}>此APP由专业交易软件bq7.io提供技术服务</Text>
                        </View>
                }

            </View>
            <View style={{height:60, backgroundColor:'#F0F0F0', flexDirection: 'row'}}>
                {tabs}
            </View>
            <Modal
                animationType={"fade"}
                transparent={true}
                visible={this.state.showMenu === true}
                onRequestClose={()=>{
                    {/*if(this.modalCloseTimer) {*/}
                        {/*clearTimeout(this.modalCloseTimer);*/}
                    {/*}*/}
                }}
            >
                <View style={{flexDirection: 'column', flex: 1}}>
                    <TouchableOpacity activeOpacity={0.6} style={{height:Platform.OS == 'ios' ? 72 : 46}} onPress={()=>{this.hideMenu()}}></TouchableOpacity>
                    <View style={{flexDirection:'row'}}>
                        <TouchableOpacity activeOpacity={0.6} style={{flex:1}} onPress={()=>{this.hideMenu()}}></TouchableOpacity>
                        <View style={{width: 150, height:250, padding: 8, paddingTop:12, backgroundColor: '#F1F1F1', borderRadius: 5, borderColor: 'lightgrey', borderWidth:1}}>
                            <TouchableOpacity onPress={()=>{this.modifyPwd();}} style={{margin:6, flexDirection:'row'}}><Ionicons name={'lock'} size={16} style={{ marginRight:5, width: 20, color: 'gray'}}/><Text style={{fontSize: 16, color:'gray'}}>修改密码</Text></TouchableOpacity>
                            <View style={{height:1, backgroundColor:'lightgrey', margin:6}}/>
                            <TouchableOpacity onPress={()=>{this.logout();}} style={{margin:6, flexDirection:'row'}}><Ionicons name={'sign-out'} size={16} style={{ marginRight:5, width: 20, color: 'gray'}}/><Text style={{fontSize: 16, color:'gray'}}>退出</Text></TouchableOpacity>
                            <View style={{height:1, backgroundColor:'lightgrey', margin:6}}/>
                            <View style={{flex:1}} />
                            <View style={{height:1, backgroundColor:'lightgrey', margin:6}}/>
                            <Text style={{color:'gray', margin: 6}}>版权所有：</Text>
                            <View style={{flexDirection: 'row', marginLeft:6, marginBottom: 5, marginTop: 10}}>
                                <View>
                                    <Image source={BaseApp.app_logo} resizeMode="contain"
                                           style={ {marginLeft:7, height: 20, width:18} }
                                    />
                                    <Text style={{fontSize: 10, margin:6, color:'gray'}}>{BaseApp.app_title}</Text>
                                </View>
                                <View>
                                    <Ionicons
                                        name={'gg'}
                                        size={20}
                                        style={{ marginLeft:7, color: '#F36628'}}
                                    />
                                    <Text style={{fontSize: 10, margin:6, color:'gray'}}>bq7.io</Text>
                                </View>
                            </View>
                            <Text style={{color:'gray', marginLeft: 6}}>V{BaseApp.app_version}</Text>
                        </View>
                        <TouchableOpacity activeOpacity={0.6} style={{width:15}} onPress={()=>{this.hideMenu()}}></TouchableOpacity>
                    </View>
                    <TouchableOpacity activeOpacity={0.6} style={{flex:1}} onPress={()=>{this.hideMenu()}}></TouchableOpacity>
                </View>
            </Modal>
        </View>;
    }
}