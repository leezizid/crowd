import React, { Component } from 'react';
import {Tree, Message} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../../commons/react/BaseComponent'


export default class ContextChartView extends BaseComponent {

  constructor(props) {
    super(props);
    this.chartId = "chart" + new Date().getTime();
    this.symbol = this.props.symbol;
    this.time = this.props.time;
    //alert(this.symbol + ":" + this.time)
  }

  componentDidMount() {
    this.chart = echarts.init(document.getElementById(this.chartId), "dark");
    this.invoke("/cta_strategy/testdata", {symbol: this.symbol, time: this.time}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
            this.renderChart(data.data);
        }
    });  
  }

  renderChart(data) {
    if(data == null || data.length == 0) {
        return;
    }
    var option = {
        grid: [
            {
                left: 60,
                right: 20,
                top: 20,
                bottom: 40
            }
        ],
        legend: {
            top: 10,
            selectedMode : false,
            icon:'circle'
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
        dataset: {
            source: data
        },
        xAxis: [
            {
                type: 'category',
                gridIndex: 0,
                axisLabel: {show: false},
                axisTick: {show: false},
                axisLine: { lineStyle: { color: 'lightgrey' } },
                splitLine: { show: true , lineStyle: {opacity: 0.4}}
            },
            {
                type: 'category',
                gridIndex: 1,
                axisLabel: {
                    interval : 'auto'
                },
                axisLine: { lineStyle: { color: 'lightgrey' } }
            }
        ],      
        yAxis: [
            {
                scale: true,
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisLabel: {show: true},
                splitLine: { show: true , lineStyle: {opacity: 0.4}}
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
                start: 0,
                end: 100
            },
            {
                show: true,
                type: 'slider',
                top: '5',
                start: 0,
                end: 100
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
                name: '价格',
                type: 'line',
                smooth: false,
                symbol: 'none',
                xAxisIndex: 0,
                yAxisIndex: 0,
                itemStyle: {
                    color: 'lightblue'
                },
                lineStyle: {
                    width: 1,
                    color: 'lightblue'
                },
                encode: {
                    x: 0,
                    y: 1
                }
            },
            {
                name: '交易量',
                type: 'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                encode: {
                    x: 0,
                    y: 2
                }
            }, {
                name: 'MA5',
                type: 'line',
                smooth: false,
                symbol: 'none',
                xAxisIndex: 0,
                yAxisIndex: 0,
                itemStyle: {
                    color: 'orange'
                },
                lineStyle: {
                    width: 1,
                    color: 'orange'
                },
                encode: {
                    x: 0,
                    y: 3
                }
            }
        ]
      };
    this.chart.setOption(option);
  }      

  

  render() {
    return (
      <div id={this.chartId} style={{flex:1, display:"flex", flexFlow:"row", backgroundColor: 'white', width: this.props.width, height: this.props.height}}>
      </div>
    )
  }
}
