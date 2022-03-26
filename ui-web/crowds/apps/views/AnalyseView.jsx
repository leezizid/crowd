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
    this.profits = this.props.profits;
    this.analyseDay = this.props.analyseDay;
  }

  componentDidMount() {
    this.chart = echarts.init(document.getElementById(this.chartId), "dark");
    // this.showDayKLine();
    // this.tree.toggleSelect('日K线')
    this.showDayTimeLine(this.analyseDay);
    this.tree.toggleSelect(this.analyseDay)
  }


  renderChart(title, data, profits, pieces, markPoints, markLines, markAreas, showKLine, showTimeLine) {
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
        dataset: [
            {
                source: data
            },{
                source: profits
            }
        ],
        grid: [
            {
                left: '64',
                right: '64',
                bottom: 260
            },
            {
                left: '64',
                right: '64',
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
                gridIndex: 0,
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
                        return value.length > 11 ? value.substr(11, 5) : value;
                      }
                },
                axisLine: { lineStyle: { color: 'lightgrey' } }
            }
        ] 
        ,
        yAxis: [
            {
                scale: true,
                gridIndex: 0,
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisLabel: {show: true},
                splitLine: { show: true , lineStyle: {opacity: 0.4}}
            },
            {
                scale: true,
                gridIndex: 0,
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisLabel: {show: true},
                splitLine: {show: false}
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
                top: '5',
                start: 0,
                end: 100
            }
        ],            
        series: [
            {
                name: '交易量',
                type: 'bar',
                xAxisIndex: 1,
                yAxisIndex: 2,
                encode: {
                    x: 1,
                    y: 7
                }
            },                      
            {
                name: '均价',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 0,
                smooth: true,
                symbol: 'none',
                encode: {
                    x: 1,
                    y: 6
                },
                lineStyle: {
                    width: 1
                },
                markPoint: {
                    data: markPoints,
                    animation: false
                },                
                markLine: {
                    data : markLines,
                    animation: false
                },
                markArea: {
                    data: markAreas,
                    animation: false
                }
            },
            {
                name: '不计费用盈亏',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 1,
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'lightblue'
                },
                lineStyle: {
                    width: 1,
                    color: 'lightblue'
                },
                datasetIndex: 1,
                encode: {
                    x: 0,
                    y: 1
                }
            },
            {
                name: '实际盈亏',
                type: 'line',
                xAxisIndex: 0,
                yAxisIndex: 1,
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'orange'
                },
                lineStyle: {
                    width: 1,
                    color: 'orange'
                },
                datasetIndex: 1,
                encode: {
                    x: 0,
                    y: 2
                }
            },
            {
                name: 'K线',
                type: 'candlestick',
                xAxisIndex: 0,
                yAxisIndex: 0,
                encode: {
                    x: 1,
                    y: [2, 3, 5, 4]
                } ,    
                markPoint: {
                    data: [
                        { type: 'max', name: 'Max', valueDim: 'highest', symbol: 'pin', symbolSize: 1, symbolOffset: [0, -10] },
                        { type: 'min', name: 'Min', valueDim: 'lowest', symbol: 'arrow', symbolSize: 1, symbolOffset: [0, 10]  }
                    ]
                },                
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
                    value: 0,
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
    this.invoke("/history-data/dayKLine", {symbol: this.symbol, startDay: this.profits[0][0], endDay: this.profits[this.profits.length - 1][0]}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
          let chartData = JSON.parse(data.content);
          this.renderChart("日K线", chartData, this.profits, [{gte: 0, lt: chartData.length, color: normalColor}], [], [], [], true, false);
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
    this.showDayTimeLine(tradeDay);
  }

  showDayTimeLine(tradeDay) {
    this.invoke("/history-data/dayTimeLine", {symbol: this.symbol, day: tradeDay}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        let chartData = JSON.parse(data.content);
        let pieces = [];
        let matches = this.matches;

        //
        let markPoints = [];
        let markLines = [];
        let markAreas = [];

        let isInMatch = false;
        let matchIndex = 0;
        let dataIndex = 0;
        let lastPicesStart = 0;
        let lastPicesEnd = 0;
        if(matches != null && matches.length > 0) {
            while(chartData[dataIndex][0] > matches[matchIndex][0]) {
                matchIndex++;
            }
        }
        while(dataIndex < chartData.length) {
            if(isInMatch) {
                //alert(matches[matchIndex][1] + "--" + chartData[dataIndex][0]) 
                if(matches[matchIndex][2] <= chartData[dataIndex][0]) {
                    lastPicesEnd = dataIndex;
                    pieces.push({gte: lastPicesStart, lt: lastPicesEnd, color: matches[matchIndex][6] == "Long" ? upColor : downColor});
                    if(matches[matchIndex][1] > 0) {
                        markPoints.push({coord: [chartData[dataIndex][1],chartData[dataIndex][6]], value: '←平',symbolSize: 1, symbolOffset: [16, 0]})
                    }
                    markAreas[markAreas.length-1].push({xAxis: chartData[dataIndex][1]});
                    markLines[markLines.length-1].push({xAxis: chartData[dataIndex][1], yAxis:matches[matchIndex][5], symbol:'none'});
                    markLines[markLines.length-2].push({xAxis: chartData[dataIndex][1], yAxis:matches[matchIndex][4], symbol:'none'});
                    markLines[markLines.length-3].push({xAxis: chartData[dataIndex][1], yAxis:matches[matchIndex][3], symbol:'none'});
                    matchIndex++;
                    isInMatch = false;
                }
            } else {
                if(matches[matchIndex][0] <= chartData[dataIndex][0]) {
                    lastPicesStart = dataIndex;
                    pieces.push({gte: lastPicesEnd, lt: lastPicesStart, color: normalColor});
                    isInMatch = true;
                    markAreas.push([{xAxis: chartData[dataIndex][1], name: '(' + matches[matchIndex][7] + ')', itemStyle: {color: matches[matchIndex][7] == 0 ? 'rgba(173, 216, 230, 0.1)' : (matches[matchIndex][7] > 0 ? 'rgba(143, 188, 143, 0.3)' : 'rgba(255, 127, 80, 0.3)')}}]);
                    markLines.push([{xAxis:chartData[dataIndex][1], yAxis:matches[matchIndex][3], symbol:'none', lineStyle: {color: 'lightblue'}}]);
                    markLines.push([{xAxis:chartData[dataIndex][1], yAxis:matches[matchIndex][4], symbol:'none', lineStyle: {color: matches[matchIndex][6] == "Long" ? upColor : downColor}}]);
                    markLines.push([{xAxis:chartData[dataIndex][1], yAxis:matches[matchIndex][5], symbol:'none', lineStyle: {color: 'orange'}}]);
                    //markLines.push([{x:100, yAxis:matches[matchIndex][5], symbol:'none'}, {x:400, yAxis:matches[matchIndex][5], symbol:'none'}]);
                }
            }
            if(matches == null || matchIndex == matches.length) {
                break;
            }
            if(dataIndex > 0 && matches[matchIndex][1] >= chartData[dataIndex - 1][0] && matches[matchIndex][1] <= chartData[dataIndex][0]) {
                //alert(dataIndex)
                markPoints.push({coord: [chartData[dataIndex][1],chartData[dataIndex][6]], value: '开→',symbolSize: 1, symbolOffset: [-16, 0] })
            }
            dataIndex++;
        }
        if(lastPicesEnd < chartData.length) {
            pieces.push({gte: lastPicesEnd, lt: chartData.length, color: normalColor});
        }
        //
        this.renderChart(tradeDay, chartData, [], pieces, markPoints, markLines, markAreas, false, true);
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
