import React, { Component } from 'react';
import {Table, TableColumn, Button, Select, Option} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'


export default class SZ50StrategyView extends BaseComponent {

  constructor(props) {
    super(props);
    //
    this.setState({tradePeriod:'2024-04', stocks:[]});
    this.refresh();
  }


  refresh() {
    //
    this.setState({stocks:[]});
    this.invoke("/sz50/listStocks", {tradePeriod:this.state.tradePeriod}, (error, data) => {
      if(error) {
        
      } else {
        this.setState({stocks: data.stocks});
      }
    }, 3 * 1000); //XXX：定时器销毁有问题
  }

  render() {
    
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"column", backgroundColor:"white", padding: 10}}>
        <div style={{display:"flex", flexFlow:"row"}}>
          <Button style={{width:'120px'}} type="primary"   onClick={()=>(this.refresh())}>刷新</Button>
          <span style={{flex:100}}>&nbsp;&nbsp;</span>
          <span style={{width:'30px'}}></span> 
          <span style={{marginTop:10}}>交易周期：</span> 
          <Select style={{width:'200px'}} value={this.state.tradePeriod} on$change-value={(c,value) =>{
            this.setState({tradePeriod:value});
            this.refresh();
           }}>
            <Option value='2024-03'>2024-03</Option>
            <Option value='2024-04'>2024-04</Option>
          </Select>
        </div>
        <div style={{marginBottom: 5}}></div>
        <div style={{flex:1, display:"flex", flexFlow:"column", overflow: 'auto'}}>
          <Table type="grid" fixHeader={true} style={{flex: 1}} data={this.state.stocks} checkType="none" resizable stripe noDataTemplate={"无数据"}>
            <TableColumn key="serial" title="序号" width="60" align="center"/>
            <TableColumn key="code" title="股票代码" width="80" />
            <TableColumn key="name" title="股票名称" width="80" />
            <TableColumn key="maxCostValue" title="最大持仓成本" width="100" align="right"/>
            <TableColumn key="costValue" title="当前持仓成本" width="100" align="right"/>
            <TableColumn key="amount" title="持仓数量" width="80" align="right"/>
            <TableColumn key="price" title="持仓均价" width="80" align="right"/>
            <TableColumn key="lastPrice" title="当前价格" width="80" align="right"/>
            <TableColumn key="index" title="平均指数" width="80" align="right"/>
            <TableColumn key="lastIndex" title="当前指数" width="80" align="right"/>
            <TableColumn key="profit" title="综合盈亏" width="100" align="right"/>
            <TableColumn key="profitRadio" title="盈亏比例" width="80" align="right" />
            <TableColumn key="historyProfit" title="历史总盈亏" width="100" align="right" />
            <TableColumn key="op" title="操作" width="100"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                // let workerHandle = this.state.stocks[index].handle;
                                // this.invoke("/portal/disposeWorker", {workerHandle});
                            }}>查看交易详情</a> 
                        </React.Fragment>
                    }}
                ></TableColumn>
          </Table>
        </div>
      </div>
    )
  }
}
