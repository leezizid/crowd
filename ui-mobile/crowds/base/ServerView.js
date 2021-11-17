import React from 'react';
import {Text,View,TextInput,TouchableOpacity} from 'react-native';
import AsyncStorage from '@react-native-community/async-storage'
import Styles from "../../senatus/Styles";
import BaseComponent from "../../commons/BaseComponent";
import BaseApp from "../../commons/BaseApp";
import CryptoJS from "crypto-js"
import {commitState} from "../../commons/Crowding"
import {updateServerInfoAction} from "../../reducers/serverInfo"

export const pkey = '_utop_123_abc_';

export default class ServerView extends BaseComponent{

    constructor(props) {
        super(props);
    }

    saveSetting() {
        this.refs.infoInput.blur();
        try {
            let infoObj = {};
            if(this.infoText && this.infoText != null && this.infoText != '') {
                infoObj = JSON.parse(CryptoJS.AES.decrypt(this.infoText, pkey).toString(CryptoJS.enc.Utf8));
                infoObj.infoText = this.infoText;
                AsyncStorage.setItem("serverInfo",JSON.stringify(infoObj), ()=> {
                    commitState(updateServerInfoAction(infoObj.name, infoObj.url))
                    this.props.navigation.goBack();
                });
            } else {
                AsyncStorage.setItem("serverInfo",JSON.stringify(infoObj), ()=> {
                    commitState(updateServerInfoAction(BaseApp.default_name, BaseApp.default_url))
                    this.props.navigation.goBack();
                });
            }
        } catch(e) {
            this.showInfo("解析服务器证书失败")
        }
    }

    componentDidMount(){
        this.refs.infoInput.focus();
        AsyncStorage.getItem("serverInfo",(err, serverInfo)=>{
            if(serverInfo != null) {
                let infoObj = JSON.parse(serverInfo)
                this.infoText = infoObj.infoText;
                // infoObj = {}
                // infoObj.url = "ws://39.106.209.246:58888/websocket";
                // infoObj.name = "LZ"
                // infoObj.title = "投资决策"
                // this.infoText = CryptoJS.AES.encrypt(JSON.stringify(infoObj), pkey).toString();
                this.setState({info: this.infoText});
            } else {
                this.setState({info: ''});
            }
        });
    }

    renderContent(){

        return <View style={{margin:5, flexDirection: 'column'}}>
            <Text style={{marginLeft:12,marginRight:12, marginTop:10, marginBottom:10, fontSize:15}}>服务器证书：（请从代理商获取）</Text>
            <TextInput
                ref="infoInput"
                onChangeText={(text)=>{this.infoText = text}}
                style={{marginLeft:12,marginRight:12, paddingBottom:10, fontSize:15, backgroundColor:'#E4E4E4', height:200}}
                multiline={true}
                defaultValue={this.state ? this.state.info:''}
            />
            <TouchableOpacity style={Styles.buttonview} onPress={()=>{this.saveSetting()}}>
                <Text style={Styles.logintext} >保 存</Text>
            </TouchableOpacity>
        </View>
    }

}
