import React, { Component } from 'react';
import {Table, TableColumn, Tabs, Tab, Message, Datepicker, Button, Spin,Select, Option, Pagination} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../commons/react/BaseComponent'

const normalColor = 'white';
const upColor = '#F6465D';
const downColor = '#1DC26C'; 

export default class IdeaTestView extends BaseComponent {

  constructor(props) {
    super(props);
    this.chartId = "chart" + new Date().getTime();
    this.setState({loading: false, dataSeries: []});
  }

  componentDidMount() {
    this.startTest();
  }
    
  startTest() {
    this.setState({loading: true});
    this.invoke("/idea/startTest", {}, (error, data) => {
      this.setState({loading: false});
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({dataSeries: data.dataSeries});  
        this.renderChart(this.state.dataSeries);
      }
    });
  }

  renderChart(dataSeries) {
    let chart = echarts.init(document.getElementById(this.chartId), "dark");
    if(dataSeries == null) {
        return;
    }
    var option = {
        grid: [
            {
                left: 80,
                right: 50,
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
            source: dataSeries
        },
        xAxis: {
            type: 'category',
            axisLabel: {show: true},
            axisTick: {show: true},
            axisLine: { lineStyle: { color: 'lightgrey' } },
            splitLine: { show: true , lineStyle: {opacity: 0.4}}
        },
        yAxis: [
              {
                scale: false,
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisLabel: {show: true},
                splitLine: { show: true , lineStyle: {opacity: 0.4}}
              },
              {
                scale: false,
                axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
                axisLabel: {show: true},
                splitLine: { show: true , lineStyle: {opacity: 0.4}}
              }
        ],
        series: [
            {
                name: '测试数据1',
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
                    y: 1
                }
            },
            {
                name: '测试数据2',
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
                    y: 2
                }
            },
            {
                name: '测试数据3',
                type: 'line',
                smooth: false,
                symbol: 'none',
                yAxisIndex: 1,
                itemStyle: {
                    color: 'lightgreen'
                },
                lineStyle: {
                    width: 1,
                    color: 'lightgreen'
                },
                encode: {
                    x: 0,
                    y: 3
                }
            }
        ]
      };
    chart.setOption(option);
  }      


  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"column", padding: 10}}>
        <div style={{display:"flex", flexFlow:"row"}}>
          <Button style={{width:'120px'}} type="primary"   onClick={()=>(this.startTest())}>开始测试</Button>
          <span style={{flex:100}}>&nbsp;&nbsp;</span>
        </div>
        <div style={{marginBottom: 5}}></div>
        <div id={this.chartId} style={{flex:100, display:"flex", flexFlow:"column", overflow: 'auto'}}></div>
        {this.state.loading ? <Spin overlay /> : ""}
      </div>
    )
  }



}