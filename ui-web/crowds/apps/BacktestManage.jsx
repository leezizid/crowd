import React, { Component } from 'react';
import {Button, Message, Tree, Table, TableColumn, Spin, Tabs, Tab, Dialog, Form, FormItem, Input, Select, Option, Spinner} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'
import StrategyInfoView from './StrategyInfoView'


export default class BacktestManage extends BaseComponent {

  constructor(props) {
    super(props);
    //
    this.setState({showTemplateDialog: false, showTestDialog: false, showConfirmDialog1: false, showConfirmDialog2: false, templateInfo: {}, testInfo: {}, loading: false, currentNode: null, treeNodes:[], expandedKeys:[], properties:[]});
    //
    this.invoke("/backtest-manage/list", {}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({treeNodes: data.treeNodes, expandedKeys: data.expandedKeys});
        if(data.selection >= 0) {
          this.handleNodeClick(this.tree.root.children[data.selection]);
        }
      }
    });
  }

  refresh() {
    if(!this.state.currentNode) {
      this.setState({properties: []});
      return;      
    }
    this.setState({loading: true, properties: []});
    if(this.state.currentNode.data.type == 0) {
      this.invoke("/backtest-manage/template" , {id: this.state.currentNode.key}, (error, data) => {
        if(error) {
          Message.error(error.message);
          this.setState({loading: false});
        } else {
          this.setState({loading: false, properties: data.properties});
        }
      });
    } else {
      this.infoView.refresh();
    }
  }

  handleNodeClick(node) {
    if(this.tree.getSelectedData().length == 0) {
      this.tree.toggleSelect(node.key)
    }
    this.setState({currentNode: node});
    this.refresh();
  }

  getEmptyTableMessage() {
    return this.state.loading ? "正在加载..." : "无数据";
  }

  createTest() {
    let testInfo = {arguments: '{}'};
    this.setState({showTestDialog: true, testInfo})
  } 

  async startBacktest(dialog) {
    const valid = await this.testForm.validate();
    if(valid) {
      this.tree.expand(this.state.currentNode.key);
      this.invoke("/backtest-manage/startStrategy", {id: this.state.currentNode.key, arguments: this.state.testInfo.arguments}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
          dialog.close();
          this.state.currentNode.append(data.newTreeNode)
          let children = this.state.currentNode.children;
          let newNode = children[children.length -1];
          this.tree.toggleSelect(newNode.key)
          this.handleNodeClick(newNode);
        }
      });
    }
  }

  createTemplate() {
    let templateInfo = {size: 100000, currencyType: 'CNY'};
    this.setState({showTemplateDialog: true, templateInfo})
  }  

  onTemplateFormValueChange(name, value) {
    let templateInfo = this.state.templateInfo; 
    templateInfo[name] = value; 
    this.setState({templateInfo})
  }

  onTestFormValueChange(name, value) {
    let testInfo = this.state.testInfo; 
    testInfo[name] = value; 
    this.setState({testInfo})
  }

  async confirmCreateTemplate(dialog) {
    //alert(dialog)
    const valid = await this.templateForm.validate();
    if(valid) {
      let templateInfo = this.state.templateInfo;
      this.invoke("/backtest-manage/createTemplate", {templateInfo}, (error, data) => {
        if(error) {
          Message.error(error.message);
        } else {
          dialog.close();
          this.setState({treeNodes: data.treeNodes, expandedKeys: data.expandedKeys});
          let selectionNode = this.tree.root.children[data.selection];
          this.tree.toggleSelect(selectionNode.key)
          this.handleNodeClick(selectionNode);
        }
      });
    }
  }  

  deleteTemplete() {
    this.setState({showConfirmDialog1: true})
  }

  confirmDeleteTemplate() {
    this.invoke("/backtest-manage/deleteTemplate", {id: this.state.currentNode.key}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        this.setState({treeNodes: data.treeNodes, expandedKeys: data.expandedKeys});
        if(data.treeNodes.length > 0) {
          let selectionNode = this.tree.root.children[data.selection];
          this.tree.toggleSelect(selectionNode.key)
          this.handleNodeClick(selectionNode);
        } else {
          this.setState({currentNode: null});
          this.refresh();
        }
      }
    });
  }  

  
  deleteTest() {
    this.setState({showConfirmDialog2: true})
  }

  confirmDeleteTest() {
    this.invoke("/backtest-manage/deleteTest", {id: this.state.currentNode.key}, (error, data) => {
      if(error) {
        Message.error(error.message);
      } else {
        let parentNode = this.state.currentNode.parent;
        this.state.currentNode.remove();
        this.tree.toggleSelect(parentNode.key)
        this.handleNodeClick(parentNode);
      }
    });
  }


  render() {    
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"row", backgroundColor:"white", padding: 10}}>
        {
          this.state.treeNodes.length > 0 ?
            <Tree ref={i => this.tree = i} style={{width: "300px", overflow:"auto"}} 
            data={this.state.treeNodes}
            //expandedKeys={this.state.expandedKeys}
            onClick-node={(node,event)=>{this.handleNodeClick(node)}}
          />
          :
          <div style={{width: "300px", marginTop:10}}>请先增加回测模板</div>
        }
 
        {
          this.state.currentNode == null || this.state.currentNode.data.type == 0 ?
            <div style={{flex:1, display:"flex", flexFlow:"column", overflow: "auto"}}>
              <div style={{marginTop:10, marginBottom:10, display:"flex"}}>
                <Button style={{width:'120px'}} type={this.state.loading ? "" : "danger"} disabled={this.state.currentNode == null} onClick={()=>this.createTest()}>执行回测</Button> 
                <span style={{width:10}}>&nbsp;&nbsp;</span>
                <Button style={{width:'80px'}} type="secondary" disabled={this.state.currentNode == null} onClick={()=>(this.refresh())}>刷新</Button>
                <span style={{flex:1}}></span>
                <Button style={{width:'120px'}} type="secondary" onClick={()=>(this.createTemplate())}>新增模板</Button>
                <span style={{width:10}}>&nbsp;&nbsp;</span>
                <Button style={{width:'120px'}} type="secondary" disabled={this.state.currentNode == null} onClick={()=>(this.deleteTemplete())}>删除模板</Button>

              </div>
              <Tabs size="default" type="border-card" value={0}>
                <Tab disabled={this.state.currentNode == null} value={0}>模板信息</Tab>
              </Tabs>           
              <div style={{marginBottom: 20}}></div>
              <Table type="grid" fixHeader={true} style={{flex: 1, fontFamily:'Monaco'}} data={this.state.properties} checkType="none" resizable stripe noDataTemplate={this.getEmptyTableMessage()}> 
                <TableColumn key="name" title="属性名称"  width="200"/>
                <TableColumn key="value" title="属性内容" />
              </Table>      
            </div>
            :
            <StrategyInfoView ref={i => this.infoView = i} id={this.state.currentNode ? this.state.currentNode.key: null} path="/backtest-manage" test={true} deleteCallback={()=>this.deleteTest()} ></StrategyInfoView>
        }
        <Dialog value={this.state.showTemplateDialog} on$change-value={(c, show) => this.setState({showTemplateDialog: show})} title="新增回测模板" width={630}
                  ok={(dialog)=>{this.confirmCreateTemplate(dialog)}}
              >
                <Form ref={i => this.templateForm = i} size="default">
                  <FormItem label="模板名称" value={this.state.templateInfo.name} rules={{required: true}}>
                      <Input width={480} value={this.state.templateInfo.name} on$change-value={(c, value) => {this.onTemplateFormValueChange("name",value)}}/>
                  </FormItem>
                  <FormItem label="策略服务名" value={this.state.templateInfo.service} rules={{required: true}}>
                      <Input width={480} value={this.state.templateInfo.service} on$change-value={(c, value) => {this.onTemplateFormValueChange("service",value)}}/>
                  </FormItem>
                  <FormItem label="市场数据" value={this.state.templateInfo.marketDataSource} rules={{required: true}}>
                      <Input width={480} value={this.state.templateInfo.marketDataSource} on$change-value={(c, value) => {this.onTemplateFormValueChange("marketDataSource",value)}} />
                  </FormItem>
                  <FormItem label="交易产品组" value={this.state.templateInfo.productGroup} rules={{required: true}}>
                      <Input width={480} value={this.state.templateInfo.productGroup} on$change-value={(c, value) => {this.onTemplateFormValueChange("productGroup",value)}} />
                  </FormItem>
                  <FormItem label="策略规模" value={this.state.templateInfo.size} rules={{required: true}}>
                      <Spinner min={0} width={200} value={this.state.templateInfo.size} on$change-value={(c, value) => {this.onTemplateFormValueChange("size",value)}} />
                  </FormItem>
                  <FormItem label="资产类型" value={this.state.templateInfo.currencyType} rules={{required: true}}>
                      <Select width={480} value={this.state.templateInfo.currencyType} on$change-value={(c, value) => {this.onTemplateFormValueChange("currencyType",value)}} >
                        <Option value="CNY">人民币</Option>
                        <Option value="USD">美元</Option>
                        <Option value="BTC">BTC</Option>
                        <Option value="ETH">ETH</Option>
                        <Option value="USDT">USDT</Option>
                    </Select>
                  </FormItem>
                </Form>
        </Dialog>   
        <Dialog value={this.state.showTestDialog} on$change-value={(c, show) => this.setState({showTestDialog: show})} title="执行回测" width={800}
                  ok={(dialog)=>{this.startBacktest(dialog)}}
              >
                <Form ref={i => this.testForm = i} size="default">
                  <FormItem label="参数:" value={this.state.testInfo.arguments} rules={{required: true}}>
                      <Input width={690} value={this.state.testInfo.arguments} on$change-value={(c, value) => {this.onTestFormValueChange("arguments",value)}}/>
                  </FormItem>
                </Form>
        </Dialog>       
        <Dialog value={this.state.showConfirmDialog1} on$change-value={(c, show) => this.setState({showConfirmDialog1: show})} title="确认"
                  onOk={()=>{this.confirmDeleteTemplate()}}
              >确认删除回测模板？
        </Dialog>              
        <Dialog value={this.state.showConfirmDialog2} on$change-value={(c, show) => this.setState({showConfirmDialog2: show})} title="确认"
                  onOk={()=>{this.confirmDeleteTest()}}
              >确认删除该回测结果？
        </Dialog>
      </div>
    )
  }
}
