import React, { Component } from 'react';
import {Table, TableColumn, Tabs, Tab, Message, Datepicker, Button, Spin,Select, Option, Pagination} from 'kpc-react';
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
    this.setState({loading: false,startDay: '2022-01-01', maxPositionDays: 200, targetAmplitude: 0.1, stopAmplitude: 0.15, algorithm:'cci', amplitudeMode: 0, activeTab: 0, pageNum:1, properties:[], dataSeries:[], orders:[], orderCount:0});
  }

  componentDidMount() {
    this.invoke("/stock/queryTestInfo", {pageNum: this.state.pageNum}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({properties: data.properties, dataSeries: data.dataSeries, orders: data.orders, orderCount: data.orderCount,startDay: data.startDay, maxPositionDays: data.maxPositionDays, targetAmplitude: data.targetAmplitude, stopAmplitude: data.stopAmplitude, algorithm: data.algorithm, amplitudeMode: data.amplitudeMode});
      }
    });
  }
    
  startTest() {
    this.setState({loading: true});
    this.invoke("/stock/startTest", {startDay: this.state.startDay, maxPositionDays:this.state.maxPositionDays, targetAmplitude: this.state.targetAmplitude, stopAmplitude: this.state.stopAmplitude, amplitudeMode: this.state.amplitudeMode, algorithm: this.state.algorithm}, (error, data) => {
      this.setState({loading: false});
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({pageNum:1, properties: data.properties, dataSeries: data.dataSeries, orders: data.orders, orderCount: data.orderCount});  
        if(this.state.activeTab == 1) {
          this.renderChart(this.state.dataSeries);
        }
      }
    });
  }

  queryTestOrders() {
    this.setState({loading: true});
    this.invoke("/stock/queryTestOrders", {pageNum: this.state.pageNum}, (error, data) => {
      this.setState({loading: false});
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({orders: data.orders});  
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
            scale: true,
            axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
            axisLabel: {show: true},
            splitLine: { show: true , lineStyle: {opacity: 0.4}}
          },
          {
            max: 200,
            axisLine: { show: true, lineStyle: { color: 'lightgrey'} },
            axisLabel: {show: true},
            splitLine: { show: true , lineStyle: {opacity: 0.4}}
          }
        ],
        series: [
            {
                name: '综合盈亏',
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
                name: '股票盈亏',
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
                    y: 2
                }
            },
            {
                name: '合约盈亏',
                type: 'line',
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'blue'
                },
                lineStyle: {
                    width: 1,
                    color: 'blue'
                },
                encode: {
                    x: 0,
                    y: 3
                }
            },
            {
                name: '每日开仓数量',
                type: 'bar',
                yAxisIndex: 1,
                smooth: false,
                symbol: 'none',
                itemStyle: {
                    color: 'lightgrey'
                },
                lineStyle: {
                    width: 1,
                    color: 'lightgrey'
                },
                encode: {
                    x: 0,
                    y: 4
                }
            },
            {
                name: '每日持仓比例',
                type: 'line',
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
                encode: {
                    x: 0,
                    y: 5
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
          <span style={{marginTop:10}}>开始日期：</span> 
          <Datepicker value={this.state.startDay} on$change-value={(c, date) => this.setState({startDay:date})} />
          <span style={{width:'30px'}}></span> 
          <span style={{marginTop:10}}>最大持股时间（天）：</span> 
          <Select style={{width:'100px'}} value={this.state.maxPositionDays} on$change-value={(c,value) =>{
            this.setState({maxPositionDays:value});
           }}>
            <Option value={1}>1</Option>
            <Option value={2}>2</Option>
            <Option value={3}>3</Option>
            <Option value={5}>5</Option>
            <Option value={10}>10</Option>
            <Option value={15}>15</Option>
            <Option value={20}>20</Option>
            <Option value={25}>25</Option>
            <Option value={30}>30</Option>
            <Option value={40}>40</Option>
            <Option value={50}>50</Option>
            <Option value={100}>100</Option>
            <Option value={150}>150</Option>
            <Option value={200}>200</Option>
            <Option value={2000}>2000</Option>
          </Select>
          <span style={{width:'30px'}}></span> 
          <span style={{marginTop:10}}>止盈：</span> 
          <Select style={{width:'100px'}} value={this.state.targetAmplitude} on$change-value={(c,value) =>{
            this.setState({targetAmplitude:value});
           }}>
            <Option value={0.02}>2%</Option>
            <Option value={0.03}>3%</Option>
            <Option value={0.05}>5%</Option>
            <Option value={0.08}>8%</Option>
            <Option value={0.10}>10%</Option>
            <Option value={0.15}>15%</Option>
            <Option value={0.20}>20%</Option>
            <Option value={0.25}>25%</Option>
            <Option value={0.50}>50%</Option>
          </Select>
          <span style={{width:'30px'}}></span> 
          <span style={{marginTop:10}}>止损：</span> 
          <Select style={{width:'100px'}} value={this.state.stopAmplitude} on$change-value={(c,value) =>{
            this.setState({stopAmplitude:value});
           }}>
            <Option value={0.02}>2%</Option>
            <Option value={0.03}>3%</Option>
            <Option value={0.05}>5%</Option>
            <Option value={0.08}>8%</Option>
            <Option value={0.10}>10%</Option>
            <Option value={0.15}>15%</Option>
            <Option value={0.16}>16%</Option>
            <Option value={0.18}>18%</Option>
            <Option value={0.20}>20%</Option>
            <Option value={0.25}>25%</Option>       
            <Option value={0.5}>50%</Option>       
            <Option value={0.99}>100%</Option>           
          </Select>
          <span style={{width:'30px'}}></span> 
          <span style={{marginTop:10}}>波动模式：</span> 
          <Select style={{width:'100px'}} value={this.state.amplitudeMode} on$change-value={(c,value) =>{
            this.setState({amplitudeMode:value});
           }}>
            <Option value={0}>相对</Option>
            <Option value={1}>绝对</Option>
          </Select>
          <span style={{width:'30px'}}></span> 
          <span style={{marginTop:10}}>选股算法：</span> 
          <Select style={{width:'100px'}} value={this.state.algorithm} on$change-value={(c,value) =>{
            this.setState({algorithm:value});
           }}>
            <Option value={'random'}>随机</Option>
            <Option value={'trend'}>趋势</Option>
          </Select>
          <span style={{width:50}}>&nbsp;&nbsp;</span>
          <Button style={{width:'120px'}} type="primary"   onClick={()=>(this.startTest())}>开始测试</Button>
          <span style={{flex:100}}>&nbsp;&nbsp;</span>
        </div>
        <div style={{marginBottom: 5}}></div>
        <Tabs size="default" type="border-card" value={this.state.activeTab} on$change-value={(c, activeTab) => {
            this.setState({activeTab});
            if(this.state.activeTab == 1) {
              this.renderChart(this.state.dataSeries);
            } 
          }}>
                  <Tab value={0}>基本信息</Tab>
                  <Tab value={1}>收益曲线</Tab>
                  <Tab value={2}>历史交易</Tab>
        </Tabs> 
        <div style={{marginBottom: 5}}></div>
        {
          this.state.activeTab == 0 ? 
            <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.properties} checkType="none" resizable stripe noDataTemplate={"无数据"}> 
              <TableColumn key="name" title="属性名称"  width="200"/>
              <TableColumn key="value" title="属性内容" />
            </Table>          
            : 
          ""
        }
        { this.state.activeTab == 1 ? <div id={this.chartId} style={{flex:100, display:"flex", flexFlow:"column", overflow: 'auto'}}></div> : "" }
        {
          this.state.activeTab == 2 ? 
            <div style={{flex: 1, display:"flex", flexFlow:"column", overflow: 'auto'}}>
              <div style={{marginBottom: 10}}></div> 
              <Pagination  counts={10} current={this.state.pageNum} showLimits={false} total={this.state.orderCount} limit={20} onChange={(v)=> {
                  this.setState({pageNum: v.current});
                  this.queryTestOrders();
              }}/>
              <div style={{marginBottom: 10}}></div> 
              <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.orders} checkType="none" resizable stripe noDataTemplate={"无数据"}> 
                <TableColumn key="openDay" title="买入日期"  width="120"/>
                <TableColumn key="closeDay" title="卖出日期"  width="120"/>
                <TableColumn key="code" title="股票代码"  width="120"/>
                <TableColumn key="name" title="股票名称"  width="120"/>
                <TableColumn key="openPrice" title="买入价格"  width="120"/>
                <TableColumn key="closePrice" title="卖出价格"  width="120"/>
                <TableColumn key="volume" title="数量"  width="120"/>
                <TableColumn key="profitRadio" title="盈亏比例"  width="120" template={(data,index)=>{return <span style={{color: this.state.orders[index].profitRadio.startsWith('-') ? 'red' : 'green'}}>{this.state.orders[index].profitRadio}</span>}}/>
                <TableColumn key="days" title="持股天数"  width="120"/>
                <TableColumn key="forceClose" title="强平标志" template={(data,index)=>{return <span style={{color: 'red'}}>{this.state.orders[index].forceClose}</span>}}/>
              </Table>          
            </div>
            : 
          ""
        }        
        {this.state.loading ? <Spin overlay /> : ""}
      </div>
    )
  }



}