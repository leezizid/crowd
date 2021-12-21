import React, { Component } from 'react';
import {Button, Message, Tree, Table, TableColumn, Spin, Tabs, Tab, Dialog, Form, FormItem, Input, Select, Option, Spinner} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'
import StrategyInfoView from './StrategyInfoView'

export default class StrategyManage extends BaseComponent {

  constructor(props) {
    super(props);
    //
    this.setState({showConfirmDialog: false, showStrategyDialog: false, strategyInfo: {}, currentNode: null, treeNodes:[], expandedKeys:[]});
    //
    this.invoke("/strategy-manage/list", {}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({treeNodes: data.treeNodes, expandedKeys: data.expandedKeys});
      }
    });
  }

  delete() {
    this.setState({showConfirmDialog: true})
  }

  confirmDelete() {
    this.invoke("/strategy-manage/deleteStrategy", {id: this.state.currentNode.key}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({treeNodes: data.treeNodes, expandedKeys: data.expandedKeys});
        if(data.selection >= 0) {
          let selectionNode = this.tree.root.children[0].children[data.selection];
          this.tree.toggleSelect(selectionNode.key)
          this.handleNodeClick(selectionNode);
        } else {
          this.setState({currentNode: null});
          this.infoView.refresh(true);
        }        
      }
    });
  }

  handleNodeClick(node,event) {
    if(this.tree.getSelectedData().length == 0) {
      this.tree.toggleSelect(node.key)
    }    
    this.setState({currentNode: node});
    this.infoView.refresh(true);
  }


  createStrategy() {
    let strategyInfo = {size: 100000, currencyType: 'CNY', arguments: ""};
    this.setState({showStrategyDialog: true, strategyInfo})
  }  

  onFormValueChange(name, value) {
    let strategyInfo = this.state.strategyInfo; 
    strategyInfo[name] = value; 
    this.setState({strategyInfo})
  }

  async confirmCreateStrategy(dialog) {
    //alert(dialog)
    const valid = await this.strategyForm.validate();
    if(valid) {
      let strategyInfo = this.state.strategyInfo;
      this.invoke("/strategy-manage/createStrategy", {strategyInfo}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
          dialog.close();
          this.setState({treeNodes: data.treeNodes, expandedKeys: data.expandedKeys});
          let selectionNode = this.tree.root.children[0].children[data.selection];
          this.tree.toggleSelect(selectionNode.key)
          this.handleNodeClick(selectionNode);
        }
      });
    }
  }    
 
  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"row", backgroundColor:"white", padding: 10}}>
        <Tree ref={i => this.tree = i} style={{width: "300px", overflow:"auto"}} 
          data={this.state.treeNodes}
          expandedKeys={this.state.expandedKeys}
          onClick-node={(node,event)=>{this.handleNodeClick(node,event)}}
        />
        <StrategyInfoView ref={i => this.infoView = i} id={this.state.currentNode ? this.state.currentNode.key: null} path="/strategy-manage" test={false} deleteCallback={()=>this.delete()} newCallback={()=>this.createStrategy()}></StrategyInfoView>
        <Dialog value={this.state.showStrategyDialog} on$change-value={(c, show) => this.setState({showStrategyDialog: show})} title="新增策略" width={630}
                  ok={(dialog)=>{this.confirmCreateStrategy(dialog)}}
              >
                <Form ref={i => this.strategyForm = i} size="default">
                  <FormItem label="策略名称" value={this.state.strategyInfo.name} rules={{required: true}}>
                      <Input width={480} value={this.state.strategyInfo.name} on$change-value={(c, value) => {this.onFormValueChange("name",value)}}/>
                  </FormItem>
                  <FormItem label="策略服务名" value={this.state.strategyInfo.service} rules={{required: true}}>
                      <Input width={480} value={this.state.strategyInfo.service} on$change-value={(c, value) => {this.onFormValueChange("service",value)}}/>
                  </FormItem>
                  <FormItem label="策略参数" value={this.state.strategyInfo.arguments} rules={{required: true}}>
                      <Input width={480} value={this.state.strategyInfo.arguments} on$change-value={(c, value) => {this.onFormValueChange("arguments",value)}}/>
                  </FormItem>
                  <FormItem label="市场数据" value={this.state.strategyInfo.marketDataSource} rules={{required: true}}>
                      <Input width={480} value={this.state.strategyInfo.marketDataSource} on$change-value={(c, value) => {this.onFormValueChange("marketDataSource",value)}} />
                  </FormItem>
                  <FormItem label="交易产品组" value={this.state.strategyInfo.productGroup} rules={{required: true}}>
                      <Input width={480} value={this.state.strategyInfo.productGroup} on$change-value={(c, value) => {this.onFormValueChange("productGroup",value)}} />
                  </FormItem>
                  <FormItem label="交易通道" value={this.state.strategyInfo.channelId} rules={{required: true}}>
                      <Input width={480} value={this.state.strategyInfo.channelId} on$change-value={(c, value) => {this.onFormValueChange("channelId",value)}} />
                  </FormItem>
                  <FormItem label="策略规模" value={this.state.strategyInfo.size} rules={{required: true}}>
                      <Spinner min={0} width={200} value={this.state.strategyInfo.size} on$change-value={(c, value) => {this.onFormValueChange("size",value)}} />
                  </FormItem>
                  <FormItem label="资产类型" value={this.state.strategyInfo.currencyType} rules={{required: true}}>
                      <Select width={480} value={this.state.strategyInfo.currencyType} on$change-value={(c, value) => {this.onFormValueChange("currencyType",value)}} >
                        <Option value="CNY">人民币</Option>
                        <Option value="USD">美元</Option>
                        <Option value="BTC">BTC</Option>
                        <Option value="ETH">ETH</Option>
                        <Option value="USDT">USDT</Option>
                    </Select>
                  </FormItem>
                </Form>
        </Dialog>        
        <Dialog value={this.state.showConfirmDialog} on$change-value={(c, show) => this.setState({showConfirmDialog: show})} title="确认"
                  onOk={()=>{this.confirmDelete()}}
              >确认删除该策略？
        </Dialog>
      </div>
    )
  }
}