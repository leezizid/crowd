import React, { Component } from 'react';
import {Button, Message, Tree, Table, TableColumn, Spin, Tabs, Tab, Dialog, Form, FormItem, Input, Select, Option, Spinner,ButtonGroup, Pagination} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'
import AnalyseView from './views/AnalyseView'
import ProfitView from './views/ProfitView'
import PLStatView from './views/PLStatView'


export default class StrategyInfoView extends BaseComponent {

  constructor(props) {
    super(props);
    this.setState({loading: false, showDetailDialog: false, showOrderDialog: false, showAnalyseDialog:false, analyseDay: '', orderInfo: {}, activeTab: 0, tChannelId: null, strategyId: null, serviceName: null, progress: -1, newPrice: 0, properties: [], transactions: [], openOrders: [], history: [], historyCount:0, currentPage: 1, matches:[], orders: [], profits: [], plStats:[], productArray: []});
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

  getEmptyTableMessage() {
    return this.state.loading ? "????????????..." : "?????????";
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
                  <TableColumn key="name" title="????????????"  width="200"/>
                  <TableColumn key="value" title="????????????" />
                </Table>
      case 1:
        return <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.transactions} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="????????????"/>
                  <TableColumn key="openTime" title="????????????" />
                  <TableColumn key="positionSide" title="????????????" />
                  <TableColumn key="positionVolumn" title="????????????" />
                  <TableColumn key="positionPrice" title="????????????" />
                  <TableColumn key="cost" title="??????" />
                  <TableColumn key="takePrice" title="??????" />
                  <TableColumn key="stopPrice" title="??????" />
                  <TableColumn key="closeOrderCount" title="???????????????" />
                  <TableColumn key="openOrderCount" title="???????????????" />
                  <TableColumn key="op" title="??????" width="150"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                this.setState({showDetailDialog: true, orders: this.state.transactions[index].orders});
                            }}>????????????</a>
                            <span>&nbsp;</span>
                            <a onClick={()=>{
                                this.newOrder('Close',this.state.transactions[index].positionSide, this.state.transactions[index].symbol,this.state.transactions[index].id)
                            }}>????????????</a> 
                        </React.Fragment>
                    }}
                ></TableColumn>                  
               </Table>               
      case 2:
        return <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.openOrders} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="????????????"/>
                  <TableColumn key="time" title="????????????" />
                  <TableColumn key="serverOrderId" title="???????????????ID"/>
                  <TableColumn key="type" title="??????"/>
                  <TableColumn key="positionSide" title="??????"/>
                  <TableColumn key="volumn" title="????????????"/>
                  <TableColumn key="price" title="????????????"/>
                  <TableColumn key="execVolumn" title="????????????"/>
                  <TableColumn key="avgPrice" title="????????????"/>
                  <TableColumn key="error" title="????????????"/>
                  <TableColumn key="op" title="??????" width="50"
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
                            }}>??????</a> 
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
                  <TableColumn key="symbol" title="????????????"/>
                  <TableColumn key="openTime" title="????????????" />
                  <TableColumn key="closeTime" title="????????????" />
                  <TableColumn key="positionSide" title="????????????" />
                  <TableColumn key="balance" title="??????" />
                  <TableColumn key="cost" title="??????" />
                  <TableColumn key="op" title="??????" width="120"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                this.setState({showDetailDialog: true, orders: this.state.history[index].orders});
                            }}>??????</a> 
                            &nbsp;&nbsp;<a onClick={()=>{
                                this.analyse(this.state.history[index].symbol, this.state.history[index].tradeDay);
                            }}>??????</a>
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
          {!this.props.test ? <Button style={{width:'120px'}} type={this.state.progress >= 0 ? "" : "danger"} disabled={this.props.id == null || this.state.progress >= 0} onClick={()=>this.startStrategyWorker()}>??????</Button> : ""}
          {!this.props.test ? <span style={{width:10}}>&nbsp;&nbsp;</span> : ""}
          <Button style={{width:'120px'}} type={this.state.progress < 0 ? "" : "success"} disabled={this.props.id == null || this.state.progress < 0} onClick={()=>this.stopStrategyWorker()}>??????{this.state.progress >= 0 && this.state.progress < 1?  "("+(this.state.progress * 100).toFixed(0)+"%)" : ""}</Button> 
          <span style={{width:10}}>&nbsp;&nbsp;</span>
          <Button style={{width:'80px'}} type="secondary" disabled={this.props.id == null} onClick={()=>(this.refresh())}>??????</Button>
          <span style={{width:100}}>&nbsp;&nbsp;</span>
          <span style={{marginTop:'10px', marginRight:'20px', width:'200px'}}>{this.state.progress >= 0 ? ("???????????????????????????" + this.state.newPrice) : ""}</span>
          {!this.props.test ? <Button style={{width:'160px'}} type="warning" disabled={this.props.id == null || this.state.progress < 0} onClick={()=>(this.newOrder('Open','Long'))}>????????????</Button>: ""}
          <span style={{flex:1}}></span>
          {this.props.newCallback ? <Button style={{width:'80px'}} type="secondary" onClick={()=>(this.props.newCallback())}>??????</Button> : ""}
          {this.props.newCallback ? <span style={{width:10}}>&nbsp;&nbsp;</span> : ""}
          {this.props.deleteCallback ? <Button style={{width:'80px'}} type="secondary" disabled={this.props.id == null || this.state.progress >= 0} onClick={()=>(this.props.deleteCallback())}>??????</Button> : ""}
        </div>         
        <Tabs size="default" type="border-card" value={this.state.activeTab} on$change-value={(c, activeTab) => {
            this.setState({activeTab})
            if(this.state.activeTab == 3 || this.state.activeTab == 4 || this.state.activeTab == 5) {
              this.refresh(true);
            }
          }}>
                  <Tab disabled={this.props.id == null} value={0}>????????????</Tab>
                  <Tab disabled={this.props.id == null} value={1}>????????????</Tab>
                  <Tab disabled={this.props.id == null} value={2}>??????</Tab>
                  <Tab disabled={this.props.id == null} value={3}>????????????</Tab>
                  <Tab disabled={this.props.id == null} value={4}>????????????</Tab>
                  <Tab disabled={this.props.id == null} value={5}>????????????</Tab>
        </Tabs>           
        <div style={{marginBottom: 20}}></div>
        {this.renderTabPage(this.state.activeTab)}
        {this.state.loading ? <Spin overlay /> : ""}
        <Dialog value={this.state.showDetailDialog}
                  on$change-value={(c, show) => this.setState({showDetailDialog: show})}
                  title="????????????"
                  b-footer-wrapper=""
                  width={1200}
              >
          <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.orders} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
              <TableColumn key="type" title="????????????" />
              <TableColumn key="positionSide" title="????????????" />
              <TableColumn key="time" title="????????????" width={180}/>
              <TableColumn key="price" title="????????????" />
              <TableColumn key="volumn" title="????????????" />
              <TableColumn key="avgPrice" title="????????????" />
              <TableColumn key="execVolumn" title="????????????" />
              <TableColumn key="costValue" title="??????" />
              <TableColumn key="canceled" title="????????????" />
              <TableColumn key="error" title="????????????"  width={180}/>
          </Table>                  
        </Dialog>        
        <Dialog value={this.state.showOrderDialog} on$change-value={(c, show) => this.setState({showOrderDialog: show})} title={this.state.orderInfo.type == "Open" ? "????????????" : "????????????"} width={630}
                ok={(dialog)=>{this.confirmOrder(dialog)}}
            >
            {
              this.state.orderInfo.type == "Open" ?
                <Form ref={i => this.templateForm = i} size="default">
                  <FormItem label="?????????" value={this.state.orderInfo.side} rules={{required: true}}>
                    <ButtonGroup checkType="radio" value={this.state.orderInfo.side} on$change-value={(c, value) => this.onFormValueChange("side",value)}>
                      <Button style={{width:'240px'}} value="Long" >???</Button>
                      <Button style={{width:'240px'}} value="Short">???</Button>
                    </ButtonGroup>
                  </FormItem>
                  <FormItem label="?????????" value={this.state.orderInfo.symbol} rules={{required: true}}>
                    <Select width={480} value={this.state.orderInfo.symbol} on$change-value={(c, value) => {this.onFormValueChange("symbol",value)}} >    
                      {this.renderProductOptions()}
                    </Select>
                  </FormItem>
                  <FormItem label="?????????" value={this.state.orderInfo.price} rules={{required: true, number: true}}>
                      <Input width={480} value={this.state.orderInfo.price} on$change-value={(c, value) => {this.onFormValueChange("price",value)}} placeholder={"?????????????????????????????????"+this.state.newPrice}/>
                  </FormItem>
                  <FormItem label="?????????" value={this.state.orderInfo.takePrice} rules={{required: true, number: true}}>
                      <Input width={480} value={this.state.orderInfo.takePrice} on$change-value={(c, value) => {this.onFormValueChange("takePrice",value)}} />
                  </FormItem>
                  <FormItem label="?????????" value={this.state.orderInfo.stopPrice} rules={{required: true, number: true}}>
                      <Input width={480} value={this.state.orderInfo.stopPrice} on$change-value={(c, value) => {this.onFormValueChange("stopPrice",value)}} />
                  </FormItem>
                </Form> 
              :
                <Form ref={i => this.templateForm = i} size="default">
                  <FormItem label="?????????" value={this.state.orderInfo.symbol} rules={{required: true}}>
                      <Input width={480} value={this.state.orderInfo.symbol} on$change-value={(c, value) => {this.onFormValueChange("symbol",value)}} readonly={true}/>
                  </FormItem>
                </Form> 
            }  
        </Dialog>           
        <Dialog value={this.state.showAnalyseDialog}
                  on$change-value={(c, show) => this.setState({showAnalyseDialog: show})}
                  title="????????????"
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
      </div>
    )
  }
}
