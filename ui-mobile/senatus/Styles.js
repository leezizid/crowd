import React from 'react';
import {StyleSheet} from 'react-native';
import {marginRight} from './StylesT'


var colors = {
    c1:'#E93030',
    c2:"#151616",
    c3:"#fff",
    c4:"#938d99"
};
export {colors};
export default Styles = StyleSheet.create({
    content:{
        flex:1,
        justifyContent:"center"
    },
    text:{
        fontSize:12
    },
    container: {
        flex: 1,
        backgroundColor: '#FFFFFF'
    },
    header: {
        height: 60,
        backgroundColor: colors.c1,
        paddingTop:40,
        paddingBottom:15,
        justifyContent: 'center',
    },
    headtitle: {
        alignSelf: 'center',
        fontSize: 20,
        color: colors.c3,
    },
    marginTopview: {
        height: 5,
        backgroundColor: '#F7F7F9'
    },
    inputview: {

        marginTop:5
    },
    textinput: {
        flex: 1,
        fontSize: 16,

    },
    dividerview: {
        flexDirection: 'row',
    },
    divider: {
        flex: 1,
        height: 1,
        backgroundColor: '#ECEDF1'
    },
    bottomview: {
        backgroundColor: '#ECEDF1',
        flex: 1,
    },
    buttonview: {
        backgroundColor: colors.c1,
        margin: 10,
        borderRadius: 6,
        justifyContent: 'center',
        alignItems: 'center',
    },
    logintext: {
        fontSize: 17,
        color: '#FFFFFF',
        marginTop: 10,
        marginBottom: 10,
    },
    emptyview: {
        flex: 1,
    },
    bottombtnsview: {
        flexDirection: 'row',
    },
    bottomleftbtnview: {
        flex: 1,
        height: 50,
        paddingLeft: 20,
        alignItems: 'flex-start',
        justifyContent: 'center',
    },
    bottomrightbtnview: {
        flex: 1,
        height: 50,
        paddingRight: 20,
        alignItems: 'flex-end',
        justifyContent: 'center',
    },
    bottombtn: {
        fontSize: 15,
        color: colors.c2,
    }
});


export function getNavigationOptionsHeader(type){
    let adjustment = 0;
    switch(type){
        //标题栏只有标题
        default:
            adjustment = marginRight[0];
            break;
        //标题栏有标题，左边返回图标
        case 1:
            adjustment = marginRight[1];
            break;
        //标题栏有标题，右边图标
        case 2:
            adjustment = marginRight[2];
            break;
    }
    return  {
        headerStyle:{
            backgroundColor: '#234b35',
            paddingTop:20,
            height:52,
            // justifyContent:"center",
        },
        headerTitleStyle: {
            alignItems:"center",
            alignSelf: 'center',
            marginRight: adjustment,
            fontSize: 16,
            textAlign: "center",
            flex: 1
        },
        headerTintColor: colors.c3,
        headerBackTitle: "返回",
        headerBackTitleStyle: {
            justifyContent:"center",
            alignItems:"center",
            textAlign: 'center',
        },
        // headerRight : <Ionicons
        //     name={'ios-alert'}
        //     size={24}
        //     style={{color:'white', marginRight:10}}
        // />,
        gesturesEnabled:true
    };
}