import React from 'react';
import { StyleSheet, Text, View, AsyncStorage,Image, TouchableOpacity, Linking} from 'react-native';
import { downloadUpdate, switchVersion} from 'react-native-update';
import DeviceInfo from 'react-native-device-info'
import BaseComponent from '../../commons/BaseComponent'

const delayTime = 500;

export default class UpdateView extends BaseComponent {

    constructor(props){
        super(props);
        this.state = {info: '正在检查更新...', error: null, url: null};
    }

    componentDidMount(){
        this.prepareUpdate();
    }

    prepareUpdate() {
        this.setState({info: '正在检查更新...', error: null, url: null})
        setTimeout(()=>this.doUpdate(), delayTime);
    }

    downloadUrl() {
        Linking.openURL(this.state.url);
    }

    doUpdate() {
        let newVersion = this.props.newVersion;
        let currentVersion = this.props.currentVersion;
        this.invoke("utop_app.queryUpdateUrl", {currentVersion: currentVersion, systemName: DeviceInfo.getSystemName()}, (error, data) => {
            if(error) {
                this.setState({error:'检查更新出现错误，点击重试'});
            } else {
                if(data.updateType == 0) {
                    this.setState({info: '需要下载新的安装包，点击开始下载。如已点击请到系统下载列表中查看下载进度，请勿重复下载', url: data.url});
                } else {
                    let options = {
                        updateUrl: data.url,
                        hash: "_" + newVersion + "_",  //hash必须是字符串
                        update : true
                    }
                    downloadUpdate(options).then(hash => {
                        this.setState({info:'更新已经下载完成，正在准备重启应用...'});
                        setTimeout(()=> {
                            switchVersion(hash);
                        }, delayTime);
                    }).catch(error => {
                        //处理错误
                        this.setState({error:'下载更新出现错误，点击重试'});
                    });
                }
            }
        });
    }

    renderContent() {
        return <View style={{justifyContent:"center",alignItems:"center",flex:1}}>
            {
                this.state.error
                    ?
                    <TouchableOpacity onPress={()=>{this.prepareUpdate();}}>
                        <Text style={{color:'red', marginHorizontal:20}}>{this.state.error}</Text>
                    </TouchableOpacity>
                    :
                    this.state.url
                        ?
                        <TouchableOpacity onPress={()=>{this.downloadUrl();}}>
                            <Text style={{marginHorizontal:20}}>{this.state.info}</Text>
                        </TouchableOpacity>
                        :
                        <Text>{this.state.info}</Text>
            }
            <Image source={this.props.logoImage} style={{width:30,height:30, marginTop:10}} resizeMode="contain"/>
        </View>
    }

}

