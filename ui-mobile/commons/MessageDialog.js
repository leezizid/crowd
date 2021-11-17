import React from 'react';
import {
    View,
    ActivityIndicator,
    Text,
    TouchableOpacity,
    Modal
} from 'react-native'

export default class MessageDialog extends React.Component {

    constructor(props) {
        super(props);
        this.unmounted = false;
        this.state = {info:'', error: '', progress: '', showProgress: false};
    }

    showInfo(message) {
        //console.log(message)
        this.setState({info: message});
        this.timerId = setTimeout(()=>{
            if(this.unmounted) {
                return;
            }
            this.setState({info: ''});
        },1500)
    }

    showError(message) {
        this.setState({error: message});
        this.timerId = setTimeout(()=>{
            if(this.unmounted) {
                return;
            }
            this.setState({error: ''});
        },1500)
    }

    showProgress(message, progressDelay) {
        if(!message) {
            // message = '...';
        }
        //message && console.log(message)
        this.setState({progress: message, showProgress: false});
        //延时显示进度
        setTimeout(()=> {
            if(this.unmounted) {
                return;
            }
            this.setState({showProgress: true});
        }, progressDelay ? progressDelay : 1000);
    }

    hideProgress() {
        setTimeout(()=> {
            if(this.unmounted) {
                return;
            }
            this.setState({progress: ''});
        },1)
    }

    componentWillUnmount() {
        this.unmounted = true;
    }

    render() {
        return (
            this.state.progress ?
                <Modal
                    animationType={"fade"}
                    transparent={true}
                    visible={true}
                    onRequestClose={()=>{

                    }}
                >
                    <View style={{flex:1, flexDirection:'column', alignItems:'center'}}>
                        <View style={{flex:1}}></View>
                        {
                            this.state.showProgress ?

                                        this.state.progress == '...' ?
                                            <ActivityIndicator size="large"/>
                                            :
                                            <View style={{flexDirection:'row', backgroundColor:'whitesmoke', borderRadius:8}}>
                                                <View style={{marginLeft: 20}}/>
                                                <ActivityIndicator/>
                                                <Text style={{marginTop:15, marginBottom:15, marginLeft:10, marginRight:20, textAlign:'center'}}>{this.state.progress}</Text>
                                            </View>

                                :
                                <View/>
                        }
                        <View style={{flex:1}}></View>
                    </View>
                </Modal>
                :
                <Modal
                    animationType={"fade"}
                    transparent={true}
                    visible={this.state.info != '' || this.state.error != ''}
                    onRequestClose={()=>{

                    }}
                >
                    <TouchableOpacity activeOpacity={1} style={{flex:1, flexDirection:'column', alignItems:'center'}} onPress={
                        ()=>{
                            clearTimeout(this.timerId);
                            this.setState({info: '', error: ''})
                        }
                    }
                    >
                        <View style={{flex:1}}></View>
                        <View style={{backgroundColor:'whitesmoke', borderRadius:8}}>
                            <Text style={{marginTop:15, marginBottom:15, marginLeft:20, marginRight:20, textAlign:'center'}}>{this.state.info}</Text>
                        </View>
                        <View style={{flex:1}}></View>
                    </TouchableOpacity>
                </Modal>

        );
    }

}