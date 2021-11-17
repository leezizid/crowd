import React from 'react';
import {Text, View, Button, StyleSheet, TouchableHighlight,AsyncStorage,ScrollView,Image} from 'react-native';
import { ListItem } from 'react-native-elements';
import BaseApp from "../../commons/BaseApp";
import BaseComponent from "../../commons/BaseComponent";
import {updateLoginInfoAction} from "../../reducers/connectInfo";
import {commitState} from "../../commons/Crowding";


const logoSide = 120;

export default class SettingsView extends BaseComponent {

    constructor(props){
        super(props);
    }

    subscribeStates() {
        return ['connectInfo'];
    }

    statesChanged(connectInfo) {
        this.setState({loginInfo : connectInfo.loginInfo});
    }

    renderContent(){
        const list = [{
            title: '修改密码',
            icon: 'lock',
            onPress:()=> {
                this.props.navigation.navigate("修改密码",{title:"修改密码"});
            }
        }, {
            title: '退出账号',
            icon: 'exit-to-app',
            onPress: () => {
                this.logout();
            }
        }];
        return (
            <ScrollView style={{backgroundColor:'white'}}>
                <View style={{flex:1, flexDirection:'row', backgroundColor:'white', marginTop:20, marginBottom:20, marginRight:10, marginLeft:10}}>
                    <View style={{flex:1}}></View>
                    <View>
                        <Image source={BaseApp.app_logo} resizeMode="contain"
                               style={ {marginHorizontal: 20,height:logoSide, width:logoSide} }
                        />
                        <Text style={{marginTop:10, textAlign:'center', fontSize:18, fontWeight:'400', color:'gray'}}>登录用户：{this.state.loginInfo.userTitle}</Text>
                    </View>
                    <View style={{flex:1}}>
                        <Text style={{textAlign:'right', fontSize:10, color:'gray'}}>程序版本V{BaseApp.app_version}</Text>
                    </View>
                </View>
                <View style={{height:1, backgroundColor:'lightgrey'}}/>
                <View style={{marginBottom:20}}>
                    {
                        list.map((item, i) => (
                            <View key={i}>
                                <ListItem  title={item.title}  leftIcon={{name: item.icon}} chevron onPress={item.onPress} />
                                <View style={{height:1, backgroundColor:'lightgrey'}}/>
                            </View>
                        ))
                    }
                </View>
            </ScrollView>
        );
    }

    logout(){
        this.invoke("utop_app.logout",{},(error, data)=>{
            if(error) {
                this.showInfo(error.message)
            } else {
                commitState(updateLoginInfoAction({}));
            }
        });
    }

}
