import React, { Component } from 'react';
import {Table, TableColumn,Menu, MenuItem, Message, Datepicker, Button, Spin,Select, Option, Dialog, Form, FormItem, Input} from 'kpc-react';
import echarts from 'echarts';
import BaseComponent from '../../commons/react/BaseComponent'

const normalColor = 'white';
const upColor = '#F6465D';
const downColor = '#1DC26C'; 

export default class StockInfoView extends BaseComponent {

  constructor(props) {
    super(props);
    this.chartId = "chart" + new Date().getTime();
    this.chart = null;
    //
    this.setState({loading: false,showStockDialog:false, showConfirmDialog:false, newCode:null, days:240, stock:{code:'',buyDate:''}, stocks:[]});
  }

  componentDidMount() {
    this.chart = echarts.init(document.getElementById(this.chartId), "dark");
    this.listStocks();
  }
  
  
  listStocks() {
    this.setState({loading: true});
    this.invoke("/stock/list", {}, (error, data) => {
      if(error) {
        this.setState({loading: false});
        Message.error(error.message);
      } else {
        this.setState({loading: false, stock:data.stock, stocks:data.stocks});
      }
    });
   
  }

  loadStockData() {
    this.setState({loading: true});
    this.invoke("/stock/chartData", {code:this.state.stock ? this.state.stock.code : '', buyDate: this.state.stock ? this.state.stock.buyDate : '', days:this.state.days}, (error, data) => {
      this.setState({loading: false});
      if(error) {
        Message.error(error.message);
      } else {
        this.renderChart(data.klineSeries,data.amplitudeSeries,data.buyDateIndex);
      }
    });
  }


  confirmAddStock(dialog) {
    this.invoke("/stock/add", {code:this.state.newCode}, (error, data) => {
      if(error) {
        this.setState({loading: false});
        Message.error(error.message);
      } else {
        dialog.close();
        this.setState({loading: false, stock:data.stock, stocks:data.stocks});
      }
    });
  }  

  confirmDeleteStock(dialog) {
    this.invoke("/stock/delete", {code:this.state.stock.code}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        dialog.close();
        this.setState({loading: false, stock:data.stock, stocks:data.stocks});
      }
    });
  }

  updateBuyDate(buyDate) {
    if(this.state.stock && this.state.stock.code && this.state.stock.buyDate != buyDate) {
      this.invoke("/stock/update", {code:this.state.stock.code, buyDate: buyDate ? buyDate: '', days:this.state.days}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {     
          this.setState({loading: false, stock:data.stock, stocks:data.stocks});
          this.renderChart(data.klineSeries, data.amplitudeSeries,data.buyDateIndex);
        }
      });
    }
  }

  renderChart(klineSeries, amplitudeSeries, buyDateIndex) {

    for(let i = 0; i < klineSeries.length; i++) {
      let day = klineSeries[i][0];

    }
    let markLine = {
      symbol: ['none', 'none'],
      label: { show: false },
      animation: false,
      data: [{xAxis: buyDateIndex, lineStyle: {color: 'red'} }]
    };

    let option = {
      title: {
          text: ""
      },
      legend: {
          top: 10,
          left: 'center',
          data: [
            {name:'个股K线'},{name:'大盘涨跌幅度'},{name:'个股涨跌幅度'},{name:'相对涨跌幅度'}
          ],
          selected: {
              '个股K线': false,
              '大盘涨跌幅度': true,
              '个股涨跌幅度': true,
              '相对涨跌幅度': true
          }
      },
      dataset: [
          {
              source: klineSeries
          },
          {
              source: amplitudeSeries
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
          },
          // formatter: function (params) {
          //   return (
          //     '<hr><table><tr><td>交易日：</td><td align="right">' + params[0].value[0] + '</td></tr><tr><td>开盘价：</td><td align="right">' + params[0].value[1].toFixed(2) + '</td></tr><tr><td>收盘价：</td><td align="right">' + params[0].value[2].toFixed(2) + '</td></tr><tr><td>最高价：</td><td align="right">' + params[0].value[3].toFixed(2) + '</td></tr><tr><td>最低价：</td><td align="right">' + params[0].value[4].toFixed(2) + '</td></tr>'
          //     + '<tr><td>大盘累计涨幅：</td><td align="right">' + (params[1].value[1] * 100).toFixed(2) + '%</td><tr><td>个股累计涨幅：</td><td align="right">' + (params[1].value[2] * 100).toFixed(2) + '%</td><tr><td>个股相对涨幅：</td><td align="right">' + (params[1].value[3] * 100).toFixed(2)  + '%</td></tr></table><hr>'
          //   );
          // }
          formatter: function (params) {
            return (
              '<hr><table><tr><td>交易日：</td><td align="right">' + params[0].value[0] + '</td></tr>'
              + '<tr><td>大盘累计涨幅：</td><td align="right">' + (params[1].value[1] * 100).toFixed(2) + '%</td><tr><td>个股累计涨幅：</td><td align="right">' + (params[1].value[2] * 100).toFixed(2) + '%</td><tr><td>个股相对涨幅：</td><td align="right">' + (params[1].value[3] * 100).toFixed(2)  + '%</td></tr></table><hr>'
            );
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
              // axisLabel: {show: true},
              axisLabel: {
                formatter: function (val) {
                  return val * 100 + '%';
                }
              },
              axisPointer: {
                label: {
                  formatter: function (params) {
                    return (params.value * 100).toFixed(2) + '%';
                  }
                }
              },
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
      // dataZoom: [
      //     {
      //         type: 'inside',
      //         xAxisIndex: [0, 1],
      //         start: 0,
      //         end: 100
      //     },
      //     {
      //         show: true,
      //         xAxisIndex: [0, 1],
      //         type: 'slider',
      //         top: '5',
      //         start: 0,
      //         end: 100
      //     }
      // ],            
      series: [
          {
              name: '个股K线',
              type: 'candlestick',
              xAxisIndex: 0,
              yAxisIndex: 0,
              encode: {
                  x: 0,
                  y: [1, 2, 4, 3]
              } ,    
              markPoint: {
                  data: [
                      { type: 'max', name: 'Max', valueDim: 'highest', symbol: 'pin', symbolSize: 1, symbolOffset: [0, -10] },
                      { type: 'min', name: 'Min', valueDim: 'lowest', symbol: 'arrow', symbolSize: 1, symbolOffset: [0, 10]  }
                  ]
              },                
              markLine: markLine,
              itemStyle: {
                  color: upColor,
                  color0: downColor,
                  borderColor: upColor,
                  borderColor0: downColor
              }
          },
          {
              name: '交易量',
              type: 'bar',
              xAxisIndex: 1,
              yAxisIndex: 2,
              encode: {
                  x: 0,
                  y: 5
              }
          },
          {
              name: '大盘涨跌幅度',
              type: 'line',
              xAxisIndex: 0,
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
              datasetIndex: 1,
              encode: {
                  x: 0,
                  y: 1
              }
          },
          {
              name: '个股涨跌幅度',
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
                  y: 2
              }
          },
          {
              name: '相对涨跌幅度',
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
                  y: 3
              }
          }               
      ],
      
      visualMap: [
          {
              show: false,
              seriesIndex: 1,
              dimension: 6,
              pieces: [{
                  value: 1,
                  color: upColor
              }, {
                  value: 0,
                  color: downColor
              }]
          }
      ]
    };    
    this.chart.setOption(option);
  }       


  onSelect(value) {  
    //alert(value)
    for(let i = 0; i < this.state.stocks.length; i++) {
      let stock = this.state.stocks[i];
      if(stock.code == value) {
        this.setState({stock:stock})
        this.loadStockData();               
        return;
      }
    }
    this.setState({stock:{code:'',buyDate:''}})
    this.loadStockData();               
  }

  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"column", paddingTop: 10, paddingRight: 10, paddingBottom: 10}}>
        <div style={{display:"flex", flexFlow:"row"}}>
          {/* <span style={{marginTop:10}}>股票池：</span> */}
          {/* <Select value={this.state.stock ? this.state.stock.code: ''} on$change-value={(c,value) => {        
            for(let i = 0; i < this.state.stocks.length; i++) {
              let stock = this.state.stocks[i];
              if(stock.code == value) {
                this.setState({stock:stock})
                this.loadStockData();               
                return;
              }
            }
            this.setState({stock:{code:'',buyDate:''}})
            this.loadStockData();   
            }}>
            {this.state.stocks.map((value,key) => {return (<Option key={value.code} value={value.code}>{value.name}</Option>)})}
          </Select> */}
          <span style={{width:10}}>&nbsp;&nbsp;</span>
          <Button style={{width:'120px'}} type="primary" onClick={()=>(this.setState({showStockDialog: true}))}>新增</Button>
          <span style={{width:10}}>&nbsp;&nbsp;</span>
          <Button style={{width:'120px'}} type="primary"   disabled={this.state.stock == null} onClick={()=>(this.onSelect(this.state.stock.code))}>刷新</Button>
          <span style={{width:10}}>&nbsp;&nbsp;</span>
          <Button style={{width:'120px'}} type="secondary" disabled={this.state.stock == null} onClick={()=>(this.setState({showConfirmDialog: true}))}>删除</Button>    
          <span style={{flex:100}}>&nbsp;&nbsp;</span>
          <span style={{marginTop:10}}>数据周期（天）：</span> 
          <Select style={{width:'100px'}} value={this.state.days} on$change-value={(c,value) =>{
            this.setState({days:value});
            this.loadStockData();
           }}>
            <Option value={240}>240</Option>
            <Option value={300}>300</Option>
            <Option value={360}>360</Option>
            <Option value={420}>420</Option>
            <Option value={480}>480</Option>
          </Select>
          <span style={{width:'100px'}}></span> 
          <span style={{marginTop:10}}>设置买入日期：</span> 
          <Datepicker clearable disabled={!(this.state.stock && this.state.stock.code)} value={this.state.stock ? this.state.stock.buyDate : ''} on$change-value={(c, date) => this.updateBuyDate(date)} />
        </div>      
        <div style={{marginBottom: 5}}></div>
        <div style={{flex:100, display:"flex", flexFlow:"row",overflow: 'auto'}}>
          <Table type="default" rowSelectable={true} rowKey={(value)=>{return value.code}} checkedKey={this.state.stock ? this.state.stock.code: ''} fixHeader={true} style={{width:'135px'}} data={this.state.stocks} checkType="radio" noDataTemplate={"无数据"} on$change-checked={(instance, newValue, oldValue)=>{
              this.onSelect(newValue)
          }}>
            <TableColumn key="name" title="自选股票池"/>
          </Table>
          <div id={this.chartId} style={{flex:100, display:"flex", flexFlow:"column", overflow: 'auto'}}></div>
        </div>
        {this.state.loading ? <Spin overlay /> : ""}
        <Dialog value={this.state.showStockDialog} on$change-value={(c, show) => this.setState({showStockDialog: show})} title="新增股票" width={630} ok={(dialog)=>{this.confirmAddStock(dialog)}}>
                <Form ref={i => this.stockCodeForm = i} size="default">
                  <FormItem label="股票代码" rules={{required: true}}>
                      <Input width={480} on$change-value={(c, value) => {this.setState({newCode: value})}}/>
                  </FormItem>
                </Form>
        </Dialog>        
        <Dialog value={this.state.showConfirmDialog} on$change-value={(c, show) => this.setState({showConfirmDialog: show})} title="确认"
                  onOk={(dialog)=>{this.confirmDeleteStock(dialog)}}
              >确认删除该股票？
        </Dialog>
      </div>
    )
  }



}