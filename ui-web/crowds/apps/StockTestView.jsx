import React, { Component } from 'react';
import {Table, TableColumn,Menu, MenuItem, Message, Datepicker, Button, Spin,Select, Option, Dialog, Form, FormItem, Input} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../commons/react/BaseComponent'

const normalColor = 'white';
const upColor = '#F6465D';
const downColor = '#1DC26C'; 

export default class StockTestView extends BaseComponent {

  constructor(props) {
    super(props);
    this.chartId = "chart" + new Date().getTime();
    this.chart = null;
    //
    this.setState({loading: false,startDay: '2018-01-02', maxPositionDays: 100, targetAmplitude: 0.05});
  }

  componentDidMount() {
    this.chart = echarts.init(document.getElementById(this.chartId), "dark");
    this.renderChart([]);
  }
  
  startTest() {
    this.setState({loading: true});
    this.invoke("/stock/test0", {startDay: this.state.startDay, maxPositionDays:this.state.maxPositionDays, targetAmplitude: this.state.targetAmplitude}, (error, data) => {
      this.setState({loading: false});
      if(error) {
        Message.error(error.message);
      } else {
        this.renderChart(data.dataSeries);
      }
    });
  }



  renderChart(dataSeries) {
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
        yAxis: {
            scale: true,
            axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
            axisLabel: {show: true},
            splitLine: { show: true , lineStyle: {opacity: 0.4}}
        },
        series: [
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
                    y: 1
                }
            },
            {
                name: '持仓市值',
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
                name: '资金余额',
                type: 'line',
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'green'
                },
                lineStyle: {
                    width: 1,
                    color: 'green'
                },
                encode: {
                    x: 0,
                    y: 3
                }
            },
            {
                name: '合约盈亏',
                type: 'line',
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'red'
                },
                lineStyle: {
                    width: 1,
                    color: 'red'
                },
                encode: {
                    x: 0,
                    y: 4
                }
            }
        ]
      };
    this.chart.setOption(option);
  }      


  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"column", padding: 10}}>
        <div style={{display:"flex", flexFlow:"row"}}>
          <span style={{marginTop:10}}>开始日期：</span> 
          <Datepicker value={this.state.startDay} on$change-value={(c, date) => this.setState({startDay:date})} />
          <span style={{width:'20px'}}></span> 
          <span style={{marginTop:10}}>最大持股时间（天）：</span> 
          <Select style={{width:'100px'}} value={this.state.maxPositionDays} on$change-value={(c,value) =>{
            this.setState({maxPositionDays:value});
           }}>
            <Option value={100}>100</Option>
            <Option value={150}>150</Option>
            <Option value={200}>200</Option>
            <Option value={250}>250</Option>
            <Option value={300}>300</Option>
          </Select>
          <span style={{width:'20px'}}></span> 
          <span style={{marginTop:10}}>目标涨幅（相对指数）：</span> 
          <Select style={{width:'100px'}} value={this.state.targetAmplitude} on$change-value={(c,value) =>{
            this.setState({targetAmplitude:value});
           }}>
            <Option value={0.02}>0.02</Option>
            <Option value={0.03}>0.03</Option>
            <Option value={0.04}>0.04</Option>
            <Option value={0.05}>0.05</Option>
            <Option value={0.08}>0.08</Option>
            <Option value={0.1}>0.10</Option>
          </Select>
          <span style={{width:10}}>&nbsp;&nbsp;</span>
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