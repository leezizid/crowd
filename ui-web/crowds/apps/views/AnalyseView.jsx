import React, { Component } from 'react';
import {Tree, Message} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../../commons/react/BaseComponent'

const normalColor = 'white';
const upColor = '#1DC26C';
const downColor = '#F6465D'; 

export default class AnalyseView extends BaseComponent {

  constructor(props) {
    super(props);
    this.chartId = "chart" + new Date().getTime();
    this.chart = null;
    let days = [];
    for(let i = 0; i < this.props.tradedays.length; i++) {
        days.push({label: this.props.tradedays[i], key: this.props.tradedays[i]});
    }
    this.symbol = this.props.symbol;
    this.days = days;
    this.matches = this.props.matches;
  }

  componentDidMount() {
    this.chart = echarts.init(document.getElementById(this.chartId), "dark");
    this.showDayKLine();
    this.tree.toggleSelect('日K线')
  }


  renderChart(title, data, pieces, showKLine, showTimeLine) {
    var option = {
        title: {
            text: ""
        },
        legend: {
            bottom: 10,
            left: 'center',
            data: [
                {name:'K线'},{name:'均价'}
            ],
            selected: {
                'K线': showKLine,
                '均价': showTimeLine
            }
        },
        dataset: {
            source: data
        },
        grid: [
            {
                left: '60',
                right: '30',
                bottom: 260
            },
            {
                left: '60',
                right: '30',
                height: 200,
                bottom: 60
            }
        ],
        axisPointer: {
            link: {xAxisIndex: 'all'},
            label: {
                backgroundColor: '#777'
            }
        },            
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false,
                type: 'cross',
                lineStyle: {
                    color: '#777',
                    width: 1,
                    opacity: 1
                }
            }
        },
        xAxis: [
            {
                type: 'category',
                axisLabel: {show: false},
                axisTick: {show: false},
                axisLine: { lineStyle: { color: 'lightgrey' } }
            },
            {
                type: 'category',
                gridIndex: 1,
                axisLabel: {
                    interval : 'auto',
                    formatter: function (value) {
                        return value.length > 11 ? value.substr(11) : value;
                      }
                },
                axisLine: { lineStyle: { color: 'lightgrey' } }
            }
        ] 
        ,
        yAxis: [
            {
                scale: true,
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisLabel: {show: true},
                splitLine: { show: true }
            },
            {
                scale: true,
                gridIndex: 1,
                splitNumber: 2,
                axisLabel: {show: false},
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisTick: {show: false},
                splitLine: {show: false}
            }
        ],
        
        dataZoom: [
            {
                type: 'inside',
                xAxisIndex: [0, 1],
                start: 0,
                end: 100
            },
            {
                show: true,
                xAxisIndex: [0, 1],
                type: 'slider',
                top: '25',
                start: 0,
                end: 100
            }
        ],            
        series: [
            {
                name: '交易量',
                type: 'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                encode: {
                    x: 1,
                    y: 7
                }
            },                      
            {
                name: '均价',
                type: 'line',
                smooth: true,
                symbol: 'none',
                encode: {
                    x: 1,
                    y: 6
                },
                lineStyle: {
                    width: 1
                }
            },
            {
                name: 'K线',
                type: 'candlestick',
                encode: {
                    x: 1,
                    y: [2, 3, 5, 4]
                } ,                   
                itemStyle: {
                    color: upColor,
                    color0: downColor,
                    borderColor: upColor,
                    borderColor0: downColor
                }
            },                  
        ],
        
        visualMap: [
            {
                show: false,
                seriesIndex: 0,
                dimension: 8,
                pieces: [{
                    value: 1,
                    color: upColor
                }, {
                    value: -1,
                    color: downColor
                }]
            },
            {
                show: false,
                seriesIndex: 1,
                dimension: 1,
                pieces: pieces                   
            }
        ]
    };
    this.chart.setOption(option);
  }      

  showDayKLine() {
    this.invoke("/history-data/dayKLine", {symbol: this.symbol}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
          let chartData = JSON.parse(data.content);
          this.renderChart("日K线", chartData, [{gte: 0, lt: chartData.length, color: normalColor}], true, false);
        }
      });  
  }

  handleNodeClick(node) {
    if(this.tree.getSelectedData().length == 0) {
        this.tree.toggleSelect(node.key)
        return;
    }
    let tradeDay = node.data.label;
    if(tradeDay == '日内分时') {
        return;
    } 
    if(tradeDay == '日K线') {
        this.showDayKLine();
        return;
    }
    this.invoke("/history-data/dayTimeLine", {symbol: this.symbol, day: tradeDay}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        let chartData = JSON.parse(data.content);
        var pieces = [];
        var matches = this.matches;

        //
        var isInMatch = false;
        var matchIndex = 0;
        var dataIndex = 0;
        var lastPicesStart = 0;
        var lastPicesEnd = 0;
        if(matches != null && matches.length > 0) {
            while(chartData[dataIndex][0] > matches[matchIndex][0]) {
                matchIndex++;
            }
        }
        while(dataIndex < chartData.length) {
            if(matches == null || matchIndex == matches.length) {
                break;
            }
            if(isInMatch) {
                if(matches[matchIndex][1] <= chartData[dataIndex][0]) {
                    lastPicesEnd = dataIndex;
                    pieces.push({gte: lastPicesStart, lt: lastPicesEnd, color: matches[matchIndex][2] == "Long" ? upColor : downColor});
                    matchIndex++;
                    isInMatch = false;
                }
            } else {
                if(matches[matchIndex][0] <= chartData[dataIndex][0]) {
                    lastPicesStart = dataIndex;
                    pieces.push({gte: lastPicesEnd, lt: lastPicesStart, color: normalColor});
                    isInMatch = true;
                }
            }
            
            dataIndex++;
        }
        if(lastPicesEnd < chartData.length) {
            pieces.push({gte: lastPicesEnd, lt: chartData.length, color: normalColor});
        }
        //alert(JSON.stringify(pieces))
        //
        this.renderChart(tradeDay, chartData, pieces, false, true);
      }
    });  
  }

  render() {
    return (
      <div style={{height: this.props.height, display:"flex", flexFlow:"row", padding: 0, backgroundColor: 'white'}}>
         <Tree ref={i => this.tree = i} style={{width: "180px", overflow:"auto"}} 
          data={[
            {label: '日K线', key: '日K线'},
            {
              label: '日内分时',
              key: "mlines", 
              disabled: true,
              children: this.days
            }
          ]}
          expandedKeys={['mlines']} 
          onClick-node={(node,event)=>{this.handleNodeClick(node)}}
        />
        <div id={this.chartId} style={{width: this.props.width - 180, height: this.props.height}}/>
      </div>
    )
  }
}
