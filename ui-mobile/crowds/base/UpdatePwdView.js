import React from 'react';
import{ View,TextInput,Button,Text,TouchableOpacity} from 'react-native';
import BaseComponent from "../../commons/BaseComponent";

export default class UpdatePwdView extends BaseComponent{
    constructor(props){
        super(props);
        this.process = this.process.bind(this);
        this.state = {oldPassword:"",password:"",passwordAgain:""};
    }
    renderContent(){
        let navigation = this.props.navigation;
        return (
            <View style={{marginTop:20, marginLeft:15, marginRight:15}}>
                <Text style={{fontSize: 18, fontWeight: '500', marginTop: 10, color:'gray'}}>旧密码</Text>
                <TextInput underlineColorAndroid='transparent' style={{borderWidth:1, borderColor: 'lightgrey', fontSize: 18, color: 'gray', marginTop: 5, padding: 8}}
                           onChangeText={(text)=>{this.state.oldPassword = text;}}
                           secureTextEntry={true}/>
                <Text style={{fontSize: 18, fontWeight: '500', marginTop: 10, color:'gray'}}>新密码</Text>
                <TextInput underlineColorAndroid='transparent' style={{borderWidth:1, borderColor: 'lightgrey', fontSize: 18, color: 'gray', marginTop: 5, padding: 8}}
                           onChangeText={(text)=>{this.state.password = text;}}
                           secureTextEntry={true}/>
                <Text style={{fontSize: 18, fontWeight: '500', marginTop: 10, color:'gray'}}>确认新密码</Text>
                <TextInput underlineColorAndroid='transparent' style={{borderWidth:1, borderColor: 'lightgrey', fontSize: 18, color: 'gray', marginTop: 5, padding: 8}}
                           onChangeText={(text)=>{this.state.passwordAgain = text;}}
                           secureTextEntry={true}/>
                <TouchableOpacity style={{backgroundColor:'#F36628', height:40, alignItems: 'center', marginTop: 10}} onPress={()=>{
                    this.process();
                }}>
                    <View style={{flex:1}}></View>
                    <Text style={{color:'white', fontSize: 18, fontWeight: '500'}}>确定</Text>
                    <View style={{flex:1}}></View>
                </TouchableOpacity>
            </View>
        );
    }

    process(){
        let state = this.state;
        let password = state.password;
        let passwordAgain = state.passwordAgain;
        let oldPassword = state.oldPassword;
        let navigation = this.props.navigation;
        if(!oldPassword){this.showInfo("请将旧密码填写完整!");return;}
        if(!password){this.showInfo("请将新密码填写完整!");return;}
        if(!passwordAgain){this.showInfo("请将确认的新密码填写完整!");return;}
        if(password != passwordAgain){this.showInfo("新密码和确认的新密码输入不一致");return;}
        this.invoke("utop_app.updateLoginPassword",{"oldPassword":oldPassword,"newPassword":password},(error, data)=>{
            if(error){
                this.showInfo(error.message);
                console.log(error);
            }else{
                this.showInfo("密码修改成功");
                // this.props.navigation.goBack();
            }
        });
    }
}
