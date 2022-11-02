import React, { Component } from 'react';
import {Table, TableColumn, Message, Tabs, Tab, Button, Spin} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'


export default class FuturesInfoView extends BaseComponent {

  constructor(props) {
    super(props);
    //
    this.setState({loading: false,activeTab: 'main',instruments:[]});
    this.refresh();
  }


  refresh() {
    this.setState({loading: true, instruments: []});
    this.invoke("/tchannel.ctp/instruments", {exchangeId: this.state.activeTab}, (error, data) => {
      if(error) {
        Message.error(error.message);
        this.setState({loading: false});
      } else {
        this.setState({loading: false, instruments: data.instruments});
      }
    });
  }

  update() {
    this.setState({loading: true, instruments: []});
    this.invoke("/tchannel.ctp/updateInstrumentData", {exchangeId: this.state.activeTab}, (error, data) => {
      if(error) {
        Message.error(error.message);
        this.setState({loading: false});
      } else {
        this.setState({loading: false, instruments: data.instruments});
      }
    });
  }

  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"column", padding: 10}}>        
        <Tabs size="default" type="border-card" value={this.state.activeTab} on$change-value={(c, activeTab) => {
            this.setState({activeTab})
            this.refresh();
          }}>
                  <Tab value={'main'}>主力合约</Tab> 
                  <Tab value={'CFFEX'}>中金所</Tab>
                  <Tab value={'SHFE'}>上期所</Tab>
                  <Tab value={'CZCE'}>郑商所</Tab>
                  <Tab value={'DCE'}>大商所</Tab>
                  <Tab value={'INE'}>能源中心</Tab>
        </Tabs>           
        <div style={{marginBottom: 20}}></div>
        <div style={{flex:100, display:"flex", flexFlow:"column", overflow: 'auto'}}>
          <Table type="grid" fixHeader={true} style={{flex: 1}} data={this.state.instruments} checkType="none" resizable stripe noDataTemplate={"无数据"}>
            <TableColumn key="id" title="合约ID" width="100" />
            <TableColumn key="name" title="合约名称" width="100" />
            <TableColumn key="sinaName" title="行情名称" width="100" />
            <TableColumn key="timeInfo" title="行情时间" width="100" />
            <TableColumn key="positionVolume" title="持仓量" width="100" align="right"/>
            <TableColumn key="tradeVolume" title="交易量" width="100"  align="right"/>
            <TableColumn key="lastPrice" title="最新价" width="100" align="right"/>
            <TableColumn key="isMain" title="是否主力合约" width="100" align="center"/>
          </Table>
        </div>
        {this.state.loading ? <Spin overlay /> : ""}
      </div>
    )
  }


}