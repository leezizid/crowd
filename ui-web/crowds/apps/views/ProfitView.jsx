import React, { Component } from 'react';
import {Tree, Message} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../../commons/react/BaseComponent'


export default class ProfitView extends BaseComponent {

  constructor(props) {
    super(props);
    this.chartId = "chart" + new Date().getTime();
  }

  componentDidMount() {
    this.chart = echarts.init(document.getElementById(this.chartId), "dark");
    this.renderChart(this.props.data);
  }

  componentDidUpdate() {
    this.renderChart(this.props.data);
  }

  shouldComponentUpdate(nextProps) {
    if(this.props.data !== nextProps.data) {
      return true;
    }
    return false;
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
        dataset: {
            source: data
        },
        xAxis: {
            type: 'category',
            axisLabel: {show: true},
            axisTick: {show: true},
            axisLine: { lineStyle: { color: 'lightgrey' } },
            splitLine: { show: true , lineStyle: {opacity: 0.4}}
        },
        yAxis: {
            scale: true,
            axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
            axisLabel: {show: true},
            splitLine: { show: true , lineStyle: {opacity: 0.4}}
        },
        series: [
            {
                name: '不计费用盈亏',
                type: 'line',
                smooth: false,
                symbol: 'none',
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
                name: '实际盈亏',
                type: 'line',
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'orange'
                },
                lineStyle: {
                    width: 1,
                    color: 'orange'
                },
                encode: {
                    x: 0,
                    y: 2
                }
            }
        ]
      };
    this.chart.setOption(option);
  }      

  

  render() {
    return (
      <div id={this.chartId} style={{flex:1, display:"flex", flexFlow:"row", backgroundColor: 'white'}}>
      </div>
    )
  }
}
