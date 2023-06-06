import React, { Component } from 'react';
import {Button, Message, Tree, Table, TableColumn, Spin, Tabs, Tab, Dialog, Form, FormItem, Input, Select, Option, Spinner,ButtonGroup, Pagination} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'
import AnalyseView from './views/AnalyseView'
import ContextChartView from './views/ContextChartView'
import ProfitView from './views/ProfitView'
import PLStatView from './views/PLStatView'


export default class StrategyInfoView extends BaseComponent {

  constructor(props) {
    super(props);
    this.setState({loading: false, showDetailDialog: false, showOrderDialog: false, showAnalyseDialog:false, showContextChartDialog:false, analyseDay: '', orderInfo: {}, activeTab: 0, tChannelId: null, strategyId: null, serviceName: null, progress: -1, newPrice: 0, properties: [], transactions: [], openOrders: [], history: [], historyCount:0, currentPage: 1, matches:[], orders: [], profits: [], plStats:[], productArray: []});
  }



  subscribeTopics() {
    return ["portal.workers"];
  }
  
  messageReceived(topic, message) {
    if(this.state.serviceName) {
      let matchPath = '/' + this.state.serviceName + '/start'
      if(topic == "portal.workers") {
        let workers = message.workers;
        for(let i = 0; i < workers.length; i++) {
          let worker = workers[i];
          if(worker.path == matchPath && JSON.parse(worker.params)["id"] == this.state.strategyId) {
            this.setState({progress: worker.progress, newPrice: worker.info});
            return;
          }
        }
        this.setState({progress: -1});
      }     
    }
  }

  startStrategyWorker() {
    this.invoke(this.props.path + "/startStrategy", {id: this.props.id}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.refresh();
      }
    });
  }

  stopStrategyWorker() {
    this.invoke(this.props.path + "/stopStrategy", {id: this.props.id}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.refresh();
      }
    });
  }    
 
  refresh(reset) {
    if(reset) {
      this.setState({historyCount: 0, currentPage: 1});
    }
    this.setState({loading: true, tChannelId: null, strategyId: null, serviceName: null, progress: -1, properties: [], transactions: [], openOrders: [], history: [], orders: [], profits: [], plStats: [], productArray: []});
    if(!this.props.id) {
      this.setState({loading: false});
      return;
    }
    this.invoke(this.props.path + "/info", {id: this.props.id, history: this.state.activeTab == 3, profits: this.state.activeTab == 4, plStat: this.state.activeTab == 5, currentPage: this.state.currentPage}, (error, data) => {
      if(error) {
        Message.error(error.message);
        this.setState({loading: false});
      } else {
        this.setState({loading: false, tChannelId: data.tChannelId, strategyId: data.strategyId, serviceName: data.serviceName, progress: data.progress, properties: data.properties, transactions: data.transactions, openOrders: data.openOrders, profits: data.profits, plStats: data.plStats, history: data.history, historyCount: data.historyCount, matches: data.matches, productArray: data.productArray, tradeDays: data.tradeDays});
      }
    });
  }

  analyse(symbol, day) {
    this.setState({showAnalyseDialog: true, analyseSymbol: symbol, analyseDay: day})
  }

  contextChart(symbol, contextTime) {
    this.setState({showContextChartDialog: true, analyseSymbol: symbol, contextTime: contextTime})
  }

  getEmptyTableMessage() {
    return this.state.loading ? "正在加载..." : "无数据";
  }


  renderProductOptions() {
    let options = [];
    let productArray = this.state.productArray;
    for(let i = 0; i < productArray.length; i++) {
      options.push(<Option value={productArray[i].symbol} key={productArray[i].symbol}>{productArray[i].title}({productArray[i].symbol})</Option>)
    }
    return options;
  }

  renderTabPage(tab) {
    switch(tab) {
      case 0:
        return <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.properties} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="name" title="属性名称"  width="200"/>
                  <TableColumn key="value" title="属性内容" />
                </Table>
      case 1:
        return <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.transactions} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="交易标的"/>
                  <TableColumn key="openTime" title="开始时间" />
                  <TableColumn key="positionSide" title="持仓方向" />
                  <TableColumn key="positionVolume" title="持仓数量" />
                  <TableColumn key="positionPrice" title="持仓均价" />
                  <TableColumn key="cost" title="费用" />
                  <TableColumn key="takePrice" title="止盈" />
                  <TableColumn key="stopPrice" title="止损" />
                  <TableColumn key="closeOrderCount" title="已完成订单" />
                  <TableColumn key="openOrderCount" title="未完成订单" />
                  <TableColumn key="op" title="操作" width="150"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                this.setState({showDetailDialog: true, orders: this.state.transactions[index].orders});
                            }}>订单明细</a>
                            <span>&nbsp;</span>
                            <a onClick={()=>{
                                this.newOrder('Close',this.state.transactions[index].positionSide, this.state.transactions[index].symbol,this.state.transactions[index].id)
                            }}>手动平仓</a> 
                        </React.Fragment>
                    }}
                ></TableColumn>                  
               </Table>               
      case 2:
        return <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.openOrders} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="合约名称"/>
                  <TableColumn key="time" title="提交时间" />
                  <TableColumn key="serverOrderId" title="服务端订单ID"/>
                  <TableColumn key="type" title="开平"/>
                  <TableColumn key="positionSide" title="方向"/>
                  <TableColumn key="volume" title="委托数量"/>
                  <TableColumn key="price" title="委托价格"/>
                  <TableColumn key="execVolume" title="完成数量"/>
                  <TableColumn key="avgPrice" title="成交均价"/>
                  <TableColumn key="error" title="错误信息"/>
                  <TableColumn key="op" title="操作" width="50"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                let id = this.props.id;
                                let symbol = this.state.openOrders[index].symbol;
                                let serverOrderId = this.state.openOrders[index].serverOrderId;
                                let clientOrderId = this.state.openOrders[index].clientOrderId;
                                let tChannelId = this.state.tChannelId;
                                //let strategyId = this.state.strategyId;
                                this.invoke(this.props.path + "/cancelOrder", {id, tChannelId, symbol, clientOrderId, serverOrderId}, (error, data) => {
                                  if(error) {
                                    Message.error(error.message);
                                  } else {
                                    this.refresh()
                                  }
                                });
                            }}>撤单</a> 
                        </React.Fragment>
                    }}
                ></TableColumn>
               </Table>        
      case 3:
        return <div style={{flex: 1, display:"flex", flexFlow:"column", overflow: 'auto'}}>
                <Pagination counts={10} current={this.state.currentPage} showLimits={false} total={this.state.historyCount} limit={18} onChange={(v)=> {
                  this.setState({currentPage: v.current});
                  this.refresh();
                }}/> 
                <div style={{marginBottom: 10}}></div> 
                <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.history} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="交易标的"/>
                  <TableColumn key="openTime" title="开始时间" />
                  <TableColumn key="closeTime" title="结束时间" />
                  <TableColumn key="positionSide" title="交易方向" />
                  <TableColumn key="balance" title="盈亏" />
                  <TableColumn key="cost" title="费用" />
                  <TableColumn key="op" title="操作" width="120"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                this.setState({showDetailDialog: true, orders: this.state.history[index].orders});
                            }}>明细</a> 
                            &nbsp;&nbsp;<a onClick={()=>{
                                this.analyse(this.state.history[index].symbol, this.state.history[index].tradeDay);
                            }}>日内</a>
                            &nbsp;&nbsp;<a onClick={()=>{
                                this.contextChart(this.state.history[index].symbol, this.state.history[index].openTime);
                            }}>场景</a>
                        </React.Fragment>
                    }}
                  ></TableColumn>
                </Table> 
               </div>
      case 4:
        return <ProfitView data={this.state.profits}/>     
      case 5:
        return <PLStatView data={this.state.plStats}/>             
    }
  }

  newOrder(type, side, symbol, transactionId) {
    symbol = this.state.productArray[0].symbol;
    let orderInfo = {type, side, symbol, transactionId};
    this.setState({showOrderDialog: true, orderInfo})
  }    

  onFormValueChange(name, value) {
    let orderInfo = this.state.orderInfo; 
    orderInfo[name] = value; 
    this.setState({orderInfo})
  }

  async confirmOrder(dialog) {
    const valid = await this.templateForm.validate();
    if(valid) {
      let orderInfo = this.state.orderInfo;
      // alert(JSON.stringify(orderInfo))
      this.invoke(this.props.path + "/createOrder", {strategyId: this.props.id, orderInfo}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
          dialog.close();
          this.refresh();
        }
      });
    }
  }    

  render() {
    return (
      <div style={{flex:1, display:"flex", flexFlow:"column", overflow: "auto"}}>
        <div style={{marginTop:10, marginBottom:10, display:"flex"}}>
          {!this.props.test ? <Button style={{width:'120px'}} type={this.state.progress >= 0 ? "" : "danger"} disabled={this.props.id == null || this.state.progress >= 0} onClick={()=>this.startStrategyWorker()}>启动</Button> : ""}
          {!this.props.test ? <span style={{width:10}}>&nbsp;&nbsp;</span> : ""}
          <Button style={{width:'120px'}} type={this.state.progress < 0 ? "" : "success"} disabled={this.props.id == null || this.state.progress < 0} onClick={()=>this.stopStrategyWorker()}>停止{this.state.progress >= 0 && this.state.progress < 1?  "("+(this.state.progress * 100).toFixed(0)+"%)" : ""}</Button> 
          <span style={{width:10}}>&nbsp;&nbsp;</span>
          <Button style={{width:'80px'}} type="secondary" disabled={this.props.id == null} onClick={()=>(this.refresh())}>刷新</Button>
          <span style={{width:100}}>&nbsp;&nbsp;</span>
          <span style={{marginTop:'10px', marginRight:'20px', width:'200px'}}>{this.state.progress >= 0 ? ("市场来源推送价格：" + this.state.newPrice) : ""}</span>
          {!this.props.test ? <Button style={{width:'160px'}} type="warning" disabled={this.props.id == null || this.state.progress < 0} onClick={()=>(this.newOrder('Open','Long'))}>手动开仓</Button>: ""}
          <span style={{flex:1}}></span>
          {this.props.newCallback ? <Button style={{width:'80px'}} type="secondary" onClick={()=>(this.props.newCallback())}>新增</Button> : ""}
          {this.props.newCallback ? <span style={{width:10}}>&nbsp;&nbsp;</span> : ""}
          {this.props.deleteCallback ? <Button style={{width:'80px'}} type="secondary" disabled={this.props.id == null || this.state.progress >= 0} onClick={()=>(this.props.deleteCallback())}>删除</Button> : ""}
        </div>         
        <Tabs size="default" type="border-card" value={this.state.activeTab} on$change-value={(c, activeTab) => {
            this.setState({activeTab})
            if(this.state.activeTab == 3 || this.state.activeTab == 4 || this.state.activeTab == 5) {
              this.refresh(true);
            }
          }}>
                  <Tab disabled={this.props.id == null} value={0}>基本信息</Tab>
                  <Tab disabled={this.props.id == null} value={1}>活动交易</Tab>
                  <Tab disabled={this.props.id == null} value={2}>挂单</Tab>
                  <Tab disabled={this.props.id == null} value={3}>历史交易</Tab>
                  <Tab disabled={this.props.id == null} value={4}>收益曲线</Tab>
                  <Tab disabled={this.props.id == null} value={5}>盈亏统计</Tab>
        </Tabs>           
        <div style={{marginBottom: 20}}></div>
        {this.renderTabPage(this.state.activeTab)}
        {this.state.loading ? <Spin overlay /> : ""}
        <Dialog value={this.state.showDetailDialog}
                  on$change-value={(c, show) => this.setState({showDetailDialog: show})}
                  title="订单明细"
                  b-footer-wrapper=""
                  width={1200}
              >
          <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.orders} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
              <TableColumn key="type" title="订单类型" />
              <TableColumn key="positionSide" title="持仓方向" />
              <TableColumn key="time" title="订单时间" width={180}/>
              <TableColumn key="price" title="委托价格" />
              <TableColumn key="volume" title="委托数量" />
              <TableColumn key="avgPrice" title="成交均价" />
              <TableColumn key="execVolume" title="成交数量" />
              <TableColumn key="costValue" title="费用" />
              <TableColumn key="canceled" title="撤单标志" />
              <TableColumn key="error" title="错误信息"  width={180}/>
          </Table>                  
        </Dialog>        
        <Dialog value={this.state.showOrderDialog} on$change-value={(c, show) => this.setState({showOrderDialog: show})} title={this.state.orderInfo.type == "Open" ? "手动开仓" : "手动平仓"} width={630}
                ok={(dialog)=>{this.confirmOrder(dialog)}}
            >
            {
              this.state.orderInfo.type == "Open" ?
                <Form ref={i => this.templateForm = i} size="default">
                  <FormItem label="方向：" value={this.state.orderInfo.side} rules={{required: true}}>
                    <ButtonGroup checkType="radio" value={this.state.orderInfo.side} on$change-value={(c, value) => this.onFormValueChange("side",value)}>
                      <Button style={{width:'240px'}} value="Long" >多</Button>
                      <Button style={{width:'240px'}} value="Short">空</Button>
                    </ButtonGroup>
                  </FormItem>
                  <FormItem label="合约：" value={this.state.orderInfo.symbol} rules={{required: true}}>
                    <Select width={480} value={this.state.orderInfo.symbol} on$change-value={(c, value) => {this.onFormValueChange("symbol",value)}} >    
                      {this.renderProductOptions()}
                    </Select>
                  </FormItem>
                  <FormItem label="价格：" value={this.state.orderInfo.price} rules={{required: true, number: true}}>
                      <Input width={480} value={this.state.orderInfo.price} on$change-value={(c, value) => {this.onFormValueChange("price",value)}} placeholder={"市场来源数据推送参考："+this.state.newPrice}/>
                  </FormItem>
                  <FormItem label="止盈：" value={this.state.orderInfo.takePrice} rules={{required: true, number: true}}>
                      <Input width={480} value={this.state.orderInfo.takePrice} on$change-value={(c, value) => {this.onFormValueChange("takePrice",value)}} />
                  </FormItem>
                  <FormItem label="止损：" value={this.state.orderInfo.stopPrice} rules={{required: true, number: true}}>
                      <Input width={480} value={this.state.orderInfo.stopPrice} on$change-value={(c, value) => {this.onFormValueChange("stopPrice",value)}} />
                  </FormItem>
                </Form> 
              :
                <Form ref={i => this.templateForm = i} size="default">
                  <FormItem label="合约：" value={this.state.orderInfo.symbol} rules={{required: true}}>
                      <Input width={480} value={this.state.orderInfo.symbol} on$change-value={(c, value) => {this.onFormValueChange("symbol",value)}} readonly={true}/>
                  </FormItem>
                </Form> 
            }  
        </Dialog>           
        <Dialog value={this.state.showAnalyseDialog}
                  on$change-value={(c, show) => this.setState({showAnalyseDialog: show})}
                  title="日内行情分析"
                  b-footer-wrapper=""
                  width={document.body.clientWidth-10}
                  style={{paddingLeft:0}}
              >
                  <AnalyseView 
                    width={document.body.clientWidth-50} 
                    height={document.body.clientHeight-150} 
                    symbol={this.state.analyseSymbol} 
                    matches={this.state.matches}
                    tradedays={this.state.tradeDays}
                    profits={this.state.profits}
                    analyseDay={this.state.analyseDay}
                  />
        </Dialog> 
        <Dialog value={this.state.showContextChartDialog}
                  on$change-value={(c, show) => this.setState({showContextChartDialog: show})}
                  title={"上下文行情分析（"+this.state.contextTime+"）"}
                  b-footer-wrapper=""
                  width={document.body.clientWidth-10}
                  style={{paddingLeft:0}}
              >
                  <ContextChartView 
                    width={document.body.clientWidth-50} 
                    height={document.body.clientHeight-150} 
                    symbol={this.state.analyseSymbol} 
                    time={this.state.contextTime}
                  />
        </Dialog>         
      </div>
    )
  }
}
