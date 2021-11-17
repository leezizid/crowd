import React, { Component } from 'react';
import {Table, TableColumn} from 'kpc-react';
import BaseComponent from '../../commons/react/BaseComponent'


export default class ActiveWorkerView extends BaseComponent {

  constructor(props) {
    super(props);
    //
    this.setState({activeWorkers:[]});
    //
    this.invoke("/portal/listActiveWorkers", {}, (error, data) => {
      if(error) {
        
      } else {
        this.setState({activeWorkers: data.activeWorkers});
      }
    });
  }

  subscribeTopics() {
    return ["portal.workers"];
  }
  
  messageReceived(topic, message) {
    if(topic == "portal.workers") {
        this.setState({activeWorkers: message.workers});
    }
  }

  render() {
    
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"row", backgroundColor:"white", padding: 10}}>
        <div style={{flex:1, display:"flex", flexFlow:"column"}}>
          <Table type="grid" fixHeader={true} style={{flex: 1}} data={this.state.activeWorkers} checkType="none" resizable stripe noDataTemplate={"无数据"}>
            <TableColumn key="path" title="服务名称" width="100" />
            <TableColumn key="params" title="服务参数" width="100" />
            <TableColumn key="time" title="启动时间" width="100" />
            <TableColumn key="status" title="服务状态" width="50" />
            <TableColumn key="progress" title="进度" width="50" />
            <TableColumn key="info" title="信息" width="100" />
            <TableColumn key="handle" title="服务句柄"  width="200"/>
            <TableColumn key="op" title="操作" width="50"
                    b-template={(data, index) => {
                        return <React.Fragment>
                            <a onClick={()=>{
                                let workerHandle = this.state.activeWorkers[index].handle;
                                this.invoke("/portal/disposeWorker", {workerHandle});
                            }}>强制结束</a> 
                        </React.Fragment>
                    }}
                ></TableColumn>
          </Table>
        </div>
      </div>
    )
  }
}
