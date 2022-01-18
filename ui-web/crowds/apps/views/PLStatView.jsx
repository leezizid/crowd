import React, { Component } from 'react';
import {Tree, Message} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../../commons/react/BaseComponent'


export default class PLStatView extends BaseComponent {

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
            show: false,
            top: -5,
            selectedMode : false,
            icon:'circle'
        },
        dataset: {
            source: data
        },
        xAxis: {
            type: 'category',
            data: data[0],
            axisLabel: {show: true,  fontSize: 10, interval: 'auto'},
            axisTick: {show: false, interval: 0},
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
                name: '亏损次数',
                type: 'bar',
                stack: 'Total',
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'red'
                },
                data: data[1],
                label: {
                    show: true,
                    position: 'top'
                }
            },
            {
              name: '平衡次数',
              type: 'bar',
              stack: 'Total',
              smooth: false,
              symbol: 'none',
              itemStyle: {
                  color: 'gray'
              },
              data: data[2],
              label: {
                  show: true,
                  position: 'top'
              }
            },
            {
              name: '盈利次数',
              type: 'bar',
              stack: 'Total',
              smooth: false,
              symbol: 'none',
              itemStyle: {
                  color: 'green'
              },
              data: data[3],
              label: {
                  show: true,
                  position: 'top'
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
