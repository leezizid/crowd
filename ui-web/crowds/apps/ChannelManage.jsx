import React, { Component } from 'react';
import {Button, Message, Tree, Table, TableColumn, Spin, Tabs, Tab} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'
import {commitState} from "../../commons/Crowding";

export default class ChannelManage extends BaseComponent {

  constructor(props) {
    super(props);
    //
    this.setState({loading: false, dataError: "", currentNode: null, activeTab : 0, status: false, channels:[], expandedKeys:[], properties:[], positions:[], openOrders:[]});
    //
    this.invoke("/tchannel/list", {}, (error, data) => {
      if(error) {
        
      } else {
        this.setState({channels: data.channels, expandedKeys: data.expandedKeys});
      }
    });
  }

  startChannelWorker() {
    this.invoke("/tchannel/start", {id: this.state.currentNode.key}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.refresh();
      }
    });
  }

  stopChannelWorker() {
    this.invoke("/tchannel/stop", {id: this.state.currentNode.key}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.refresh();
      }
    });
  }  

  refresh() {
    this.setState({loading: true, dataError: "", status: false, properties: [], positions: [], openOrders:[]});
    this.invoke("/tchannel/status", {id: this.state.currentNode.key}, (error, data) => {
      if(error) {
        Message.error(error.message);
        this.setState({loading: false});
      } else {
        this.setState({loading: true, dataError: "", status: data.status, properties: data.properties});
        this.invoke("/tchannel/info", {id: this.state.currentNode.key, type: this.state.activeTab}, (error, data) => {
          if(error) {
            Message.error(error.message);
            this.setState({loading: false});
          } else {
            this.setState({loading: false, dataError: data.dataError, status: data.status, properties: data.properties, positions: data.positions, openOrders:data.openOrders});
          }
        });
      }
    });
  }


  handleNodeClick(node,event) {
    if(this.tree.getSelectedData().length == 0) {
      this.tree.toggleSelect(node.key)
    }        
    this.setState({currentNode: node});
    this.refresh();
  }

  handleTabChange(activeTab) {
    this.setState({activeTab});
    this.refresh();
  }

  getEmptyTableMessage() {
    return this.state.loading ? "正在加载..." : (this.state.dataError ?this.state.dataError :"无数据");
  }

  renderTable(tab) {
    switch(tab) {
      case 0:
        return <Table type="grid" fixHeader={true} style={{flex: 1}} data={this.state.properties} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="name" title="属性名称"  width="200"/>
                  <TableColumn key="value" title="属性内容" />
                </Table>
      case 1:
        return <Table type="grid" fixHeader={true} style={{flex: 1}} data={this.state.positions} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="合约名称"  width="200"/>
                  <TableColumn key="volume" title="持仓数量" />
                  <TableColumn key="marketPrice" title="参考价格" />
               </Table>        
      case 2:
        return <Table type="grid" fixHeader={true} style={{flex: 1}} data={this.state.openOrders} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                  <TableColumn key="symbol" title="合约名称"/>
                  <TableColumn key="serverOrderId" title="服务端订单ID"/>
                  <TableColumn key="type" title="开平" />
                  <TableColumn key="positionSide" title="方向" />
                  <TableColumn key="time" title="提交时间" />
                  <TableColumn key="volume" title="委托数量" />
                  <TableColumn key="price" title="委托价格" />
                  <TableColumn key="execVolume" title="完成数量" />
                  <TableColumn key="avgPrice" title="成交均价" />
                  <TableColumn key="op" title="操作" width="50"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                let tChannelId = this.state.currentNode.key;
                                let serverOrderId = this.state.openOrders[index].serverOrderId;
                                let symbol = this.state.openOrders[index].symbol
                                this.invoke("/tchannel/cancelOrder", {tChannelId,symbol,serverOrderId}, (error, data) => {this.refresh()});
                            }}>撤单</a> 
                        </React.Fragment>
                    }}
                ></TableColumn>
               </Table>        
    }
  }

  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"row", backgroundColor:"white", padding: 10}}>
        <Tree ref={i => this.tree = i} style={{width: "300px", overflow:"auto"}} 
          data={this.state.channels}
          expandedKeys={this.state.expandedKeys}
          onClick-node={(node,event)=>{this.handleNodeClick(node,event)}}
        />
        <div style={{flex:1, display:"flex", flexFlow:"column"}}>
          <div style={{marginTop:10, marginBottom:10}}>
            <Button type={this.state.loading || this.state.status ? "" : "danger"} disabled={this.state.currentNode == null || this.state.status} onClick={()=>this.startChannelWorker()}>启动服务</Button> 
            <span style={{width:30}}>&nbsp;&nbsp;</span>
            <Button type={this.state.loading || !this.state.status ? "" : "success"} disabled={this.state.currentNode == null || !this.state.status} onClick={()=>this.stopChannelWorker()}>停止服务</Button> 
            <span style={{width:30}}>&nbsp;&nbsp;</span>
            <Button type="secondary" disabled={this.state.currentNode == null || !this.state.status} onClick={()=>(this.refresh())}>刷新</Button>
          </div>        
          <Tabs size="default" type="border-card" value={this.state.activeTab} on$change-value={(c, activeTab) => this.handleTabChange(activeTab)}>
                    <Tab disabled={this.state.currentNode == null || !this.state.status} value={0}>基本信息</Tab>
                    <Tab disabled={this.state.currentNode == null || !this.state.status} value={1}>持仓</Tab>
                    <Tab disabled={this.state.currentNode == null || !this.state.status} value={2}>挂单</Tab>
          </Tabs> 
          <div style={{marginBottom: 20}}></div>
          {this.renderTable(this.state.activeTab)}
          {this.state.loading ? <Spin overlay /> : ""}
        </div>
      </div>
    )
  }
}
