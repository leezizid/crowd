import React from 'react';
import {
    Text,
    View,
    TouchableOpacity,
    StyleSheet,
    Animated
} from 'react-native';
import BaseComponent from "./BaseComponent";


const marginWidth = 10;
const indicatorWidth = 34 ; //34
const duration = 200;

const styles = StyleSheet.create({
    tabLabel: {
        textAlign:'center', fontSize:16, fontWeight:'400',  marginTop:12, marginBottom:12
    },
    tabView: {
        flex:1
    },
});


export default class TabView extends BaseComponent {

    constructor(props) {
        super(props)
        this.tabs = this.props.tabs;
        this.renderContentView = props.renderContentView ? props.renderContentView : () => <View/>
        this.state = {tabIndicatorPosition:new Animated.Value(0), tabIndex: props.tabIndex ? props.tabIndex : 0, animating: false};
    }

    handleTabAction = (tabIndex) => {
        this.setState({tabIndex});
        this.moveIndicator(tabIndex, 200, true);
    }

    moveIndicator = (index, duration, updateAnimatingState) => {
        let w = (this.tabWidth - marginWidth * 2) / this.tabs.length;
        let position = marginWidth + w * index +(w - indicatorWidth) / 2;
        Animated.timing(
            this.state.tabIndicatorPosition,
            {duration:duration, toValue: position}
        ).start();
        if(updateAnimatingState) {
            this.setState({animating: true});
            setTimeout(()=> {
                this.setState({animating: false});
            }, 200);
        }
    }

    onTabIndicatorContainerLayout = () => {
        this.refs.tabIndicatorContainer.measure((x,y,width) => {
            this.tabWidth = width;
            this.moveIndicator(this.state.tabIndex, 0, false);
        });
    }

    render() {
        let tabHeaders = [];
        for(let i = 0; i < this.tabs.length; i++) {
            tabHeaders.push(<TouchableOpacity key={"tab_" + i} style={styles.tabView}  activeOpacity={0.2} onPress={()=>{this.handleTabAction(i)}}><Text style={[{color:this.state.tabIndex == i ? 'red' : 'black'},styles.tabLabel]}>{this.tabs[i]}</Text></TouchableOpacity>);
        }
        return (
            <View style={{flex:1, flexDirection:'column', backgroundColor:'white'}}>
                <View style={{flex:0, flexDirection:'row', backgroundColor:'white', marginLeft:marginWidth, marginRight:marginWidth}}>
                    {tabHeaders}
                </View>
                <View  ref="tabIndicatorContainer" onLayout={this.onTabIndicatorContainerLayout} style={{height:2,backgroundColor:'white'}}>
                    <Animated.View style={{position:'absolute', height:2,width:indicatorWidth, left:this.state.tabIndicatorPosition, backgroundColor:'red'}}/>
                </View>
                <View style={{height:1, marginTop:1, backgroundColor:'lightgrey'}}/>
                <View style={{flex:1}}>
                    {this.state.animating ? <Text></Text> : this.renderContentView(this.state.tabIndex)}
                </View>
            </View>
        );
    }
}
