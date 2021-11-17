import React from 'react';
import { Text, View, AsyncStorage,Image, TouchableOpacity} from 'react-native';
import BaseComponent from '../../commons/BaseComponent'
import BaseApp from '../../commons/BaseApp'

export default class DefaultView extends BaseComponent {

    constructor(props) {
        super(props)
    }

    subscribeStates() {
        return ['serverInfo', 'connectInfo'];
    }

    statesChanged(serverInfo, connectInfo) {
        this.setState({serverName:serverInfo.name, connectStatus: connectInfo.status});
    }

    render() {
        return <View style={{justifyContent:"center",alignItems:"center",flex:1}}>
            {/*<Image source={BaseApp.app_logo} style={{width:30,height:30}} resizeMode="contain"/>*/}
            {/*<TouchableOpacity style={{marginRight:16, marginVertical:8}}*/}
                              {/*onPress={*/}
                                  {/*()=>{*/}
                                      {/*this.props.navigation.navigate("服务器信息",{title:"服务器设置",bizType:"setting"});*/}
                                  {/*}*/}
                              {/*}>*/}
                {/*<Text style={{fontSize: 16, textAlign: 'right'}}>{this.state.connectStatus}交易服务器：{this.state.serverName ? this.state.serverName : '未设置'}</Text>*/}
            {/*</TouchableOpacity>*/}
        </View>
    }

}

