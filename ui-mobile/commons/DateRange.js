import React from 'react';
import {
    Text,
    View,
    Button,
    StyleSheet,
    AsyncStorage,
    TouchableOpacity,
    Modal
} from 'react-native';
import {Calendar, LocaleConfig} from 'react-native-calendars';

LocaleConfig.locales['zh'] = {
    dayNames: ['日','一','二','三','四','五','六'],
    dayNamesShort: ['日','一','二','三','四','五','六']
};

LocaleConfig.defaultLocale = 'zh';

export default class DateRange extends React.Component {

    constructor(props) {
        super(props);
        this.state = {startDate:this.props.startDate != null ? this.props.startDate : new Date(), endDate:this.props.endDate != null ? this.props.endDate : new Date(), selectDate:null, markedDates: null};
        this.selectCallback = null;
    }

    getDateString(date) {
        let s = date.getFullYear();
        s = s + '-';
        if(date.getMonth() < 9) {
            s = s + '0';
        }
        s = s +  (date.getMonth() + 1);
        s = s + '-';
        if(date.getDate() < 10) {
            s = s + '0';
        }
        s = s + date.getDate();
        return s;
    }

    getDayEnd(year, month) {
        let arr = [31,28,31,30,31,30,31,31,30,31,30,31];
        if(this.isLeapYear(year)) {
            arr[1] = 29;
        }
        return arr[month];
    }

    isLeapYear(iYear) {//是否是闰年
        if (iYear % 4 == 0 && iYear % 100 != 0) {
            return true;
        } else {
            if (iYear % 400 == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    render() {
        return (
        <View style={{flexDirection: 'row', marginTop:15, marginBottom: 15}}>
            <TouchableOpacity style={{flex:1, alignItems:'center'}} onPress={()=> {
                let markedDates = {};
                markedDates[this.getDateString(this.state.startDate)] = {selected: true, marked: true};
                this.setState({selectDate: new Date(this.state.startDate.getTime()), markedDates: markedDates});
                this.selectCallback = () => {
                    let startDate = new Date(this.state.selectDate.getTime());
                    let endDate = new Date(this.state.endDate.getTime());
                    if(startDate.getTime() > endDate.getTime()) {
                        endDate = new Date(startDate.getTime());
                    }
                    this.setState({startDate, endDate});
                    if(this.props.selectCallback) {
                        this.props.selectCallback(startDate, endDate);
                    }
                }
            }}>
                <View style={{flex:1}}></View>
                <View><Text style={{color:'gray'}}>起始日期</Text></View>
                <View><Text style={{fontWeight:'400', fontSize:20, marginTop:5}}>{this.getDateString(this.state.startDate)}</Text></View>
                <View style={{flex:1}}></View>
            </TouchableOpacity>
            <View>
                <View style={{flex:1}}></View>
                <Text>至</Text>
                <View style={{flex:1}}></View>
            </View>
            <TouchableOpacity style={{flex:1, alignItems:'center'}} onPress={()=> {
                let markedDates = {};
                markedDates[this.getDateString(this.state.endDate)] = {selected: true, marked: true};
                this.setState({selectDate: new Date(this.state.endDate.getTime()), markedDates: markedDates});
                this.selectCallback = () => {
                    let startDate = new Date(this.state.startDate.getTime());
                    let endDate = new Date(this.state.selectDate.getTime());
                    if(startDate.getTime() > endDate.getTime()) {
                        startDate = new Date(endDate.getTime());
                    }
                    this.setState({startDate, endDate});
                    if(this.props.selectCallback) {
                        this.props.selectCallback(startDate, endDate);
                    }
                }
            }}>
                <View style={{flex:1}}></View>
                <View><Text style={{color:'gray'}}>截止日期</Text></View>
                <View><Text style={{fontWeight:'400', fontSize:20, marginTop:5}}>{this.getDateString(this.state.endDate)}</Text></View>
                <View style={{flex:1}}></View>
            </TouchableOpacity>
            {
                this.state.selectDate ?
                    <Modal
                        animationType={"fade"}
                        transparent={true}
                        visible={true}
                        onRequestClose={()=>{

                        }}
                    >
                        <View style={{flex:1}}></View>
                        <View style={{flexDirection: 'row', height:380}}>
                            <View style={{flex:1}}></View>
                            <View style={{width:300, borderWidth:1, borderColor:'#efefef'}}>
                                <Calendar
                                    current={this.state.selectDate}
                                    monthFormat={'yyyy年 MM月'}
                                    onMonthChange={(date) => {
                                        //
                                        let selectDate = this.state.selectDate;
                                        let day = selectDate.getDate(); //之前的天数
                                        let isLastDay = day == this.getDayEnd(selectDate.getFullYear(), selectDate.getMonth());
                                        //
                                        selectDate.setFullYear(date.year);
                                        selectDate.setDate(1);
                                        selectDate.setMonth(date.month - 1);
                                        if(isLastDay) {
                                            selectDate.setDate(this.getDayEnd(selectDate.getFullYear(), selectDate.getMonth()));
                                        } else {
                                            selectDate.setDate(Math.min(day, this.getDayEnd(date.year, date.month - 1)));
                                        }
                                        let markedDates = {};
                                        markedDates[this.getDateString(selectDate)] = {selected: true, marked: true};
                                        this.setState({selectDate: selectDate, markedDates: markedDates});
                                    }}
                                    onDayPress={(date) => {
                                        let selectDate = this.state.selectDate;
                                        selectDate.setFullYear(date.year);
                                        selectDate.setMonth(date.month - 1);
                                        selectDate.setDate(date.day);
                                        let markedDates = {};
                                        markedDates[this.getDateString(selectDate)] = {selected: true, marked: true};
                                        this.setState({selectDate: selectDate, markedDates: markedDates});
                                    }}
                                    firstDay={1}
                                    theme={{
                                        backgroundColor: 'red',
                                        calendarBackground: '#ffffff',
                                        textSectionTitleColor: 'darkblue',
                                        selectedDayBackgroundColor: 'lightblue',
                                        selectedDayTextColor: 'black',
                                        todayTextColor: 'red',
                                        dayTextColor: 'black',
                                        textDisabledColor: '#d9e1e8',
                                        dotColor: 'red',
                                        selectedDotColor: 'gray',
                                        arrowColor: 'darkblue',
                                        monthTextColor: 'darkblue',
                                        textDayFontSize: 16,
                                        textMonthFontSize: 16,
                                        textDayHeaderFontSize: 16
                                    }}
                                    markedDates={this.state.markedDates}
                                />
                                <View style={{flexDirection:'row', alignItems:'center', padding:10, backgroundColor:'#efefef'}}>
                                    <TouchableOpacity style={{flex:1}} onPress={
                                        ()=>{
                                            this.selectCallback = null;
                                            this.setState({selectDate: null});
                                        }
                                    }
                                    >
                                        <Text style={{marginHorizontal:20, fontSize:16, color:'gray', textAlign:'right'}}>取消</Text>
                                    </TouchableOpacity>
                                    <TouchableOpacity style={{flex:1}} onPress={
                                        ()=>{
                                            this.selectCallback();
                                            this.selectCallback = null;
                                            this.setState({selectDate: null});
                                        }
                                    }
                                    >
                                        <Text style={{marginHorizontal:20, fontSize:16, color:"red"}}>确认</Text>
                                    </TouchableOpacity>
                                </View>
                            </View>
                            <View style={{flex:1}}></View>
                        </View>
                        <View style={{flex:1}}></View>
                    </Modal>
                    :
                    <View/>
            }
        </View>
        );
    }

}