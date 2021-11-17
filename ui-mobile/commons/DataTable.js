import React from 'react';
import BaseComponent from "./BaseComponent";
import {
    Text,
    View,
    Button,
    FlatList,
    TouchableHighlight,
    TouchableOpacity,
    StyleSheet,
    Animated
} from 'react-native';
import Ionicons from 'react-native-vector-icons/FontAwesome';

const styles = StyleSheet.create({
    textLabel: {
        flex:1
    },
});

const AnimatedFlatList = Animated.createAnimatedComponent(FlatList);

export default class DataTable extends BaseComponent {

    constructor(props) {
        super(props);
        let columns = this.props.columns;
        //
        this.cellViewStyles = [];
        this.cellText1Styles = [];
        this.cellText2Styles = [];
        for(let i = 0; i < columns.length; i++) {
            this.cellViewStyles.push({flexDirection:'column', flex: columns[i].flex, marginLeft:10, marginRight:10, marginTop: columns[i].contentMargin});
            this.cellText1Styles.push([styles.textLabel,{textAlign: columns[i].align, fontSize:columns[i].fontSize1}])
            this.cellText2Styles.push([styles.textLabel,{textAlign: columns[i].align, fontSize:columns[i].fontSize2}])
        }
        this.state = {actionRow:null};
    }

    onRowPress(item) {
        if(this.props.onPress) {
            this.props.onPress(item);
        }
        if(this.state.actionRow != null && this.state.actionRow.key == item.key) {
            this.setState({actionRow: null});
        } else {
            this.setState({actionRow: item});
        }
    }

    onRowAction(item, actionName) {
        if(this.props.onAction) {
            this.props.onAction(item, actionName);
        }
        this.setState({actionRow: null});
    }

    renderItem = ({item})=> {
        if(item.cells) {
            let cells = [];
            for(let i = 0; i < item.cells.length; i++) {
                let cellData = item.cells[i];
                let text1 = cellData[0]?<Text style={[{color:item.color},this.cellText1Styles[i]]}>{cellData[0]}</Text>:null;
                let text2 = cellData[1]?<Text style={[{color:item.color},this.cellText2Styles[i]]}>{cellData[1]}</Text>:null;
                let cell =
                    <View  key={i} style={this.cellViewStyles[i]}>
                        {text1}
                        {text2}
                    </View>;
                cells.push(cell)
            }
            let actionView = [];
            if(item.actions != null && this.state.actionRow != null && this.state.actionRow.key == item.key) {
                for(let i = 0; i < item.actions.length; i++) {
                    actionView.push(<TouchableOpacity key={i} onPress={()=>this.onRowAction(item, item.actions[i].name)} style={{flex:1, flexDirection:'row',justifyContent:'center',alignItems:'center'}}><View style={{flex:1}}/>{item.actions[i].icon ? <Ionicons name={item.actions[i].icon} size={16} style={{ marginRight:5, color: item.actions[i].color}}/> : <View/>}<Text style={{marginTop: 12, marginBottom:12, textAlign: 'center', fontSize:16, color:item.actions[i].color}}>{item.actions[i].title}</Text><View style={{flex:1}}/></TouchableOpacity>);
                    if(i < item.actions.length - 1) {
                        actionView.push(<View key={"_"+i} style={{width:1, backgroundColor:'lightgrey'}}/>);
                    }
                }
            }
            return <View>
                <TouchableHighlight underlayColor="#F0F0F0" activeOpacity={1} onPress={()=>this.onRowPress(item)}>
                    <View style={{flexDirection: 'row', marginTop:5, marginBottom:5}}>{cells}</View>
                </TouchableHighlight>
                <View style={{height:1, backgroundColor:'lightgrey'}}/>
                {item.actions != null && this.state.actionRow != null && this.state.actionRow.key == item.key ?
                    <View>
                        <View style={{flexDirection: 'row'}}>
                            {actionView}
                        </View>
                        <View style={{height:1, backgroundColor:'lightgrey'}}/>
                    </View>
                    :
                    <View/>
                }
            </View>
        } else {
            return <View>
                <Text style={{marginLeft:10, fontWeight:'500', marginVertical:5, color:item.color}}>{item.text}</Text>
                <View style={{height:1, backgroundColor:'lightgrey'}}/>
            </View>
        }
    }

    renderSeparator = (sectionID, rowID, adjacentRowHighlighted)=> {
        return <View style={{height:1, backgroundColor:'lightgrey'}}/>
    }

    render() {
        let columns = this.props.columns;
        let columnViews = [];
        for(let i = 0;i < columns.length; i++) {
            columnViews.push(<Text key={i} style={{flex: columns[i].flex, textAlign: columns[i].align, marginTop: columns[i].headerMargin, marginLeft:10, marginRight:10, fontSize:14}}>{columns[i].text}</Text>);
            if(i < columns.length - 1) {
                columnViews.push(<Text key={"_"+i} style={{textAlign:'center', color:'lightgrey'}}>|</Text>)
            }
        }

        return (
            <View style={{flex:1}}>
                <View style={{flexDirection: 'row', marginTop:8, marginBottom:8}}>
                    {columnViews}
                </View>
                <View style={{height:1, backgroundColor:'lightgrey'}}/>
                <AnimatedFlatList style={{flex:1}}
                                  data={this.props.rows}
                                  renderItem={this.renderItem}
                                  //ItemSeparatorComponent={this.renderSeparator}
                                  //ListHeaderComponent={this.renderSeparator}
                                  //ListFooterComponent={this.renderSeparator}
                />
            </View>
        );
    }

}