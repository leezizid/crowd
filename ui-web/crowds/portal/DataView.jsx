import React, { Component} from 'react';
import {Table, TableColumn, Message, Tabs, Tab, Button, Spin, Tooltip} from 'kpc-react';
import {Icon} from '@king-design/react';
import BaseComponent from '../../commons/react/BaseComponent'


const titleStyle1={background:"#DDEBF7", textAlign:"center", color:"windowText", fontStyle:'italic', textDecoration:'underline', fontSize:'20pt', fontWeight:700};
const titleStyle2={background:"#DDEBF7", textAlign:"center", color:"windowText", fontStyle:'italic', textDecoration:'underline', fontSize:'12pt', fontWeight:700};
const baseStyle0={background:"#DDEBF7", textAlign:"center", color:"windowText"};
const baseStyle1={background:"#F4B084", textAlign:"center", color:"windowText"};
const baseStyle2={background:"#C6E0B4", textAlign:"center", color:"windowText"};
const baseStyle3={background:"#F4B084", textAlign:"right", color:"windowText"};
const baseStyle4={background:"#FFE699", textAlign:"center", color:"windowText"};

const otherStyle1={display:"flex", justifyContent:'right', alignItems:'center', fontSize:'10pt', fontWeight:700};
const otherStyle2={display:"flex", justifyContent:'center', alignItems:'center', fontSize:'10pt', fontWeight:700};
const otherStyle3={display:"flex", fontSize:'10pt', fontWeight:700};


const border_3_3_1_1 = {borderLeft:"3pt solid #002060", borderTop:"3pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_3_1_1 = {borderLeft:"0pt solid #002060", borderTop:"3pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_3_1_0 = {borderLeft:"0pt solid #002060", borderTop:"3pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"0pt solid #002060"};
const border_0_3_3_1 = {borderLeft:"0pt solid #002060", borderTop:"3pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"1pt solid #002060"};
const border_3_0_3_0 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"0pt solid #002060"};
const border_3_1_1_3 = {borderLeft:"3pt solid #002060", borderTop:"1pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"3pt solid #002060"};
const border_3_1_1_1 = {borderLeft:"3pt solid #002060", borderTop:"1pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_1_1_3 = {borderLeft:"0pt solid #002060", borderTop:"1pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"3pt solid #002060"};
const border_0_1_3_3 = {borderLeft:"0pt solid #002060", borderTop:"1pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"3pt solid #002060"};
const border_0_0_1_3 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"3pt solid #002060"};
const border_3_0_0_1 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"0pt solid #002060", borderBottom:"1pt solid #002060"};
const border_3_0_1_1 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_0_1_0 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"0pt solid #002060"};
const border_0_0_1_1 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_1_3_1_1 = {borderLeft:"1pt solid #002060", borderTop:"3pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_1_1_3_1 = {borderLeft:"1pt solid #002060", borderTop:"1pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"1pt solid #002060"};
const border_1_1_0_1 = {borderLeft:"1pt solid #002060", borderTop:"1pt solid #002060", borderRight:"0pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_1_1_1 = {borderLeft:"0pt solid #002060", borderTop:"1pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_0_3_1 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"1pt solid #002060"};
const border_3_0_3_1 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"1pt solid #002060"};
const border_3_0_1_3 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"3pt solid #002060"};
const border_0_0_3_3 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"3pt solid #002060"};
const border_0_0_3_0 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"3pt solid #002060", borderBottom:"0pt solid #002060"};
const border_0_3_0_0 = {borderLeft:"0pt solid #002060", borderTop:"3pt solid #002060", borderRight:"0pt solid #002060", borderBottom:"0pt solid #002060"};
const border_3_0_0_0 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"0pt solid #002060", borderBottom:"0pt solid #002060"};
const border_3_0_1_0 = {borderLeft:"3pt solid #002060", borderTop:"0pt solid #002060", borderRight:"1pt solid #002060", borderBottom:"0pt solid #002060"};
const border_0_0_0_1 = {borderLeft:"0pt solid #002060", borderTop:"0pt solid #002060", borderRight:"0pt solid #002060", borderBottom:"1pt solid #002060"};
const border_0_3_0_1 = {borderLeft:"0pt solid #002060", borderTop:"3pt solid #002060", borderRight:"0pt solid #002060", borderBottom:"1pt solid #002060"};

const styles = function(style1, style2) {
  return Object.assign({},style1,style2);
}


const createDownArrowLine = function() {
  return <div style={{display:'flex' , flexDirection:'column', justifyContent:'center', alignItems:'center'}}>
                  <div style={{background:'#F4B084', height:32, width: 2}}/>
                  <svg width="8" height="8" viewBox="0 0 100 100"><path d="M50 100 L100 0 L0 0 Z" fill="#F4B084"/></svg>
                </div>
}

const createUpArrowLine = function() {
  return <div style={{display:'flex' , flexDirection:'column', justifyContent:'center', alignItems:'center'}}>
                  <svg width="8" height="8" viewBox="0 0 100 100"><path d="M50 100 L100 0 L0 0 Z" fill="#F4B084" transform="rotate(180 50 50)"/></svg>
                  <div style={{background:'#F4B084', height:32, width: 2}}/>
                </div>
}

const createRightArrowLine = function() {
  return <div style={{display:'flex' , flexDirection:'row', justifyContent:'center', alignItems:'center'}}>
                  <div style={{background:'#F4B084', height:2, width: 72}}/>
                  <svg width="8" height="8" viewBox="0 0 100 100"><path d="M50 100 L100 0 L0 0 Z" fill="#F4B084" transform="rotate(-90 50 50)"/></svg>
                </div>
}
const createLeftArrowLine = function() {
  return <div style={{display:'flex' , flexDirection:'row', justifyContent:'center', alignItems:'center'}}>
                  <svg width="8" height="8" viewBox="0 0 100 100"><path d="M50 100 L100 0 L0 0 Z" fill="#F4B084" transform="rotate(90 50 50)"/></svg>
                  <div style={{background:'#F4B084', height:2, width: 72}}/>
                </div>
}

const createRightBraceFigure = function(height) {
  let color = 'black';
  let margin = 2;
  
  let viewBox = "0,0,20," + height * 2;
  let d1="M0 "+ height +"  A 10 10, 0, 0, 0, 10 "+ (height-10) + " L 10 "+(10 + margin)+" A 10 10, 0, 0, 1, 20 " + margin;
  let d2="M0 "+ height +"  A 10 10, 0, 0, 1, 10 "+ (height+10) + " L 10 "+(2 * height - 10 - margin)+" A 10 10, 0, 0, 0, 20 " + (2 * height - margin);
  return <svg viewBox={viewBox} >
  <path d={d1} 
  			stroke={color} stroke-width="2" fill="transparent"/>
  <path d={d2}
  			stroke={color} stroke-width="2" fill="transparent"/>
</svg>
}

const createLeftBraceFigure = function(height) {
  let color = 'black';
  let margin = 2;
  
  let viewBox = "0,0,20," + height * 2;
  let d1="M20 "+ height +"  A 10 10, 0, 0, 1, 10 "+ (height-10) + " L 10 "+(10 + margin)+" A 10 10, 0, 0, 0, 0 " + margin;
  let d2="M20 "+ height +"  A 10 10, 0, 0, 0, 10 "+ (height+10) + " L 10 "+(2 * height - 10 - margin)+" A 10 10, 0, 0, 1, 0 " + (2 * height - margin);
  return <svg viewBox={viewBox} >
  <path d={d1} 
  			stroke={color} stroke-width="2" fill="transparent"/>
  <path d={d2}
  			stroke={color} stroke-width="2" fill="transparent"/>
</svg>
}

const createTipContent = function(text,value) {
  return <Tooltip theme='light' size='small' canHover={true} position={{my:'left bottom', at:'left-0 top-2'}} showArrow={false}
    content={<div>
                <Icon size='18' color='black' className="ion-arrow-graph-up-right"/>&nbsp;&nbsp;
                <Icon size='18' color='black' onClick={()=>{alert(1)}} className="ion-information-circled"/>&nbsp;&nbsp;
                <Icon size='18' color='black' onClick={()=>{alert(1)}} className="ion-plus-round"/>
            </div>}>
            <div>
              {text}<br/>{value}
            </div>  
  </Tooltip>
}

export default class DataView extends BaseComponent {


  constructor(props) {
    super(props);
    //
    this.setState({loading: false,activeTab: 'main',instruments:[]});
    // this.refresh();
    this.line1StartRef = React.createRef();
    this.line1EndRef = React.createRef();
    this.line2StartRef = React.createRef();
    this.line2EndRef = React.createRef();
    this.line3StartRef = React.createRef();
    this.line3EndRef = this.line2EndRef;
    //
    this.m0Start1Ref = React.createRef();
    this.m0End0Ref = React.createRef();
    this.m0EndRef = React.createRef();
    this.m1Start1Ref = React.createRef();
    this.m1Start2Ref = React.createRef();
    this.m1Start3Ref = React.createRef();
    this.m1EndRef = React.createRef();
    this.m2Start1Ref = React.createRef();
    this.m2Start2Ref = React.createRef();
    this.m2Start3Ref = React.createRef();
    this.m2Start4Ref = React.createRef()
    this.m2EndRef = React.createRef();
  }

  componentDidMount() {
    this.line1Refer = new LeaderLine(
      LeaderLine.pointAnchor(this.line1StartRef.current, {x: 48, y: 28}) , 
      LeaderLine.pointAnchor(this.line1EndRef.current, {x: 45, y: 45}) , 
      {
        size: 1,
        color: '#5d06aa',
        path: 'grid', 
        startSocket: 'bottom',
        startSocketGravity: 20,
        endSocket: 'bottom',
        endPlugSize: 2,
        // dash: {animation: true

        // }
    })
    this.line2Refer = new LeaderLine(
      LeaderLine.pointAnchor(this.line2StartRef.current, {x: 88, y: 20}) , 
      LeaderLine.pointAnchor(this.line2EndRef.current, {x: 88, y: 20}) , 
      {
        size: 1,
        color: '#F4B084',
        path: 'grid', 
        startSocket: 'right',
        startSocketGravity: 200,
        endSocket: 'right',
        endPlugSize: 2
    })
    this.line3Refer = new LeaderLine(
      LeaderLine.pointAnchor(this.line3StartRef.current, {x: 88, y: 20}) , 
      LeaderLine.pointAnchor(this.line3EndRef.current, {x: 88, y: 20}) , 
      {
        size: 1,
        color: '#F4B084',
        path: 'grid', 
        startSocket: 'right',
        startSocketGravity: 200,
        endSocket: 'right',
        endPlugSize: 2,
    })
    //
    this.m0line1Refer = this.createDashLine(this.m0Start1Ref.current,this.m0EndRef.current,'left','right',0,0)  
    this.m0line2Refer = this.createDashLine(this.m0Start1Ref.current,this.m0End0Ref.current,'left','left',0,0)  
    //
    this.m1line1Refer = this.createDashLine(LeaderLine.pointAnchor(this.m1Start1Ref.current, {x: 30, y: 9}),this.m1EndRef.current,'left','right',0,10)  
    this.m1line2Refer = this.createDashLine(LeaderLine.pointAnchor(this.m1Start2Ref.current, {x: 30, y: 9}),this.m1EndRef.current,'left','right',0,10)
    this.m1line3Refer = this.createDashLine(this.m1Start3Ref.current,this.m1EndRef.current,'top','right',8,0)
    this.m1line4Refer = this.createDashLine(this.m0EndRef.current,this.m1EndRef.current,'right','right',0,8)
    //
    this.m2line1Refer = this.createDashLine(LeaderLine.pointAnchor(this.m2Start1Ref.current, {x: 30, y: 9}),this.m2EndRef.current,'left','right',0,10)  
    this.m2line2Refer = this.createDashLine(LeaderLine.pointAnchor(this.m2Start2Ref.current, {x: 30, y: 9}),this.m2EndRef.current,'left','right',0,10,'behind')
    this.m2line3Refer = this.createDashLine(this.m2Start3Ref.current, LeaderLine.pointAnchor(this.m2EndRef.current,{x: 70, y: 12}),'left','bottom',325,5, 'behind',LeaderLine.captionLabel('+34.30（去除货币基金存款等）', {color:'red',offset: [-240, -24]}))
    this.m2line4Refer = this.createDashLine(this.m2Start4Ref.current, LeaderLine.pointAnchor(this.m2EndRef.current,{x: 70, y: 12}),'bottom','bottom',10,100,'behind', LeaderLine.captionLabel('+5.35（非存款机构持有货币基金份额）', {color:'red',offset: [-240, 100]}))
    this.m2line5Refer = this.createDashLine(this.m1EndRef.current,LeaderLine.pointAnchor(this.m2EndRef.current,{x: 70, y: 12}),'right','top',8,0,'behind')
  }

  createDashLine(start, end, startSocket, endSocket, startSocketGravity, endSocketGravity, endPlug, label) {
    return  new LeaderLine(start,end, {
        startSocket: startSocket,
        endSocket: endSocket,
        startSocketGravity: startSocketGravity == 0 ? -1 : endSocketGravity,
        endSocketGravity: endSocketGravity == 0 ? -1 : endSocketGravity,
        size: 2,
        color: '#002060',
        path: 'grid', 
        endPlugSize: 1,
        dash: {animation: true},
        startLabel: label,
        hide: true,
        endPlug: endPlug ? endPlug : 'arrow1'
    })
  }

  showLines(lines) {
    for(let i = 0; i < lines.length; i++) {
      lines[i].show();
    }
  }

  hideLines(lines) {
    for(let i = 0; i < lines.length; i++) {
      lines[i].hide();
    }
  }

  beforeDispose() {
    if(this.line1Refer) {
      this.line1Refer.remove();
    }
    if(this.line2Refer) {
      this.line2Refer.remove();
    }
    if(this.line3Refer) {
      this.line3Refer.remove();
    }
  }

  refresh() {
    //this.setState({loading: true, instruments: []});
    // this.invoke("/tchannel.ctp/instruments", {exchangeId: this.state.activeTab}, (error, data) => {
    //   if(error) {
    //     Message.error(error.message);
    //     this.setState({loading: false});
    //   } else {
    //     this.setState({loading: false, instruments: data.instruments});
    //   }
    // });
  }
  
  render() {
    return (
      <div style={{height:"100%", display:"flex", flexFlow:"column", padding: 10}}>        
        <Tabs size="default" type="border-card" value={this.state.activeTab} on$change-value={(c, activeTab) => {
            this.setState({activeTab})
            this.refresh();
          }}>
                  <Tab value={'main'}>流动性框架（中国）</Tab> 
                  <Tab value={'CFFEX'}>流动性框架（美国）</Tab>
                  <Tab value={'SHFE'}>财政体系（中国）</Tab>
                  <Tab value={'CZCE'}>GDP概览（中国）</Tab>
                  <Tab value={'DCE'}>GDP（美国）</Tab>
                  <Tab value={'INE'}>全球制造业和供应链</Tab>
                  <Tab value={'Page1'}>房地产概览（中国）</Tab>
                  <Tab value={'Page2'}>国防概览（中国）</Tab>
                  <Tab value={'Page3'}>国防概览（美国）</Tab>
        </Tabs>           
        <div style={{marginBottom: 20}}></div>
        <div style={{flex:100, display:"flex", flexFlow:"row", overflow: 'auto'}}>
          <div style={{flex:100}}></div>
          <div>
            <div style={{height:14}}></div>
            <div style={{display:"flex", flexFlow:"row"}} onMouseOver={()=>{this.showLines([this.m0line1Refer,this.m0line2Refer])}} onMouseOut={()=>{this.hideLines([this.m0line1Refer,this.m0line2Refer])}}>
              <div style={styles(otherStyle1, {width:126})}>流通中的现金（M0）</div>
              <div style={styles(otherStyle2, {width:20})}>-</div>
              <div style={styles(otherStyle1, {width:50,fontStyle:'italic'})} ref={this.m0EndRef}>12.19&nbsp;&nbsp;</div>
            </div>
            <div style={{height:30}}></div>
            <div style={{display:"flex", flexFlow:"row"}} onMouseOver={()=>{this.showLines([this.m1line1Refer,this.m1line2Refer,this.m1line3Refer,this.m1line4Refer])}} onMouseOut={()=>{this.hideLines([this.m1line1Refer,this.m1line2Refer,this.m1line3Refer,this.m1line4Refer])}} >
              <div style={styles(otherStyle1, {width:126})}>货币（M1）</div>
              <div style={styles(otherStyle2, {width:20})}>-</div>
              <div style={styles(otherStyle1, {width:50,fontStyle:'italic'})} ref={this.m1EndRef}>105.51&nbsp;&nbsp;</div>
            </div>
            <div style={{height:30}}></div>
            <div style={{display:"flex", flexFlow:"row"}} onMouseOver={()=>{this.showLines([this.m2line1Refer,this.m2line2Refer,this.m2line3Refer,this.m2line4Refer,this.m2line5Refer])}} onMouseOut={()=>{this.hideLines([this.m2line1Refer,this.m2line2Refer,this.m2line3Refer,this.m2line4Refer,this.m2line5Refer])}}>
              <div style={styles(otherStyle1, {width:126})}>准货币（M2）</div>
              <div style={styles(otherStyle2, {width:20})}>-</div>
              <div style={styles(otherStyle1, {width:50,fontStyle:'italic'})} ref={this.m2EndRef}>311.91&nbsp;&nbsp;</div>
            </div>
            <div>
              <div style={{height:450}}></div>
              <div>&nbsp;&nbsp;数据来源：</div>
              <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;货币当局资产负债表</div>
              <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其他存款类机构资产负债表</div>
              <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;存款类金融机构信贷收支表</div>
              <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;社会融资规模存量统计表</div>
            </div>
          </div>
          <div style={{width:60}}></div>
          <div>
          <table style={{width:"100%", borderCollapse: "collapse"}}>
            <tr>
              <td width={80}></td><td width={80}></td><td width={80}></td><td width={80}></td>
              <td width={40}></td><td width={40}></td><td width={40}></td><td width={40}></td><td width={40}></td><td width={40}></td>
              <td width={80}></td><td width={80}></td><td width={80}></td><td width={80}></td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle1,border_3_3_1_1)} ref={this.m0Start1Ref}>{createTipContent('发行现金','12.70')}</td>
              <td style={styles(baseStyle1,border_0_3_1_1)}>{createTipContent('自有资金','0.02')}</td>
              <td style={styles(baseStyle1,border_0_3_1_1)}>{createTipContent('国外负债','1.75')}</td>
              <td style={styles(baseStyle1,border_0_3_1_1)}>{createTipContent('其他负债','0.17')}</td>
              <td style={styles(titleStyle1,border_0_3_0_0)} colSpan={6}rowspan={2} >中国人民银行</td>
              <td style={styles(baseStyle2,border_1_3_1_1)}>{createTipContent('国外资产','0.92')}</td>
              <td style={styles(baseStyle2,border_0_3_1_1)}>{createTipContent('外汇','22.10')}</td>
              <td style={styles(baseStyle2,border_0_3_1_1)}>{createTipContent('黄金','0.42')}</td>
              <td style={styles(baseStyle2,border_0_3_3_1)}>{createTipContent('其他资产','1.64')}</td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle0,border_3_0_0_0)} colSpan={4}></td>
              <td style={styles(baseStyle0,border_0_0_3_0)} colSpan={4}></td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle2,border_3_1_1_3)}>{createTipContent('发放贷款','15.64')}</td>
              <td style={styles(baseStyle1,border_0_1_1_3)}>{createTipContent('准备金存款','22.80')}</td>
              <td style={styles(baseStyle1,border_0_1_1_3)}>{createTipContent('债券发行','0.15')}</td>
              <td style={styles(baseStyle0,border_0_0_1_3)}></td>
              <td style={styles(baseStyle2,border_0_1_1_3)} colSpan={3}>{createTipContent('国债','2.87')}</td>
              <td style={styles(baseStyle1,border_0_1_1_3)} colSpan={3}>{createTipContent('财政存款','4.81')}</td>
              <td style={styles(baseStyle0,border_0_0_1_3)}></td>
              <td style={styles(baseStyle2,border_0_1_1_3)}>{createTipContent('发放贷款','0.67')}</td>
              <td style={styles(baseStyle1,border_0_1_1_3)}>{createTipContent('准备金存款','0.69')}</td>
              <td style={styles(baseStyle1,border_0_1_3_3)} ref={this.m1Start3Ref}>{createTipContent('准备金存款','2.42')}</td>
            </tr>
            <tr style={{height:40}}>
              <td align={"center"}>{createDownArrowLine()}</td>
              <td align={"center"}>{createUpArrowLine()}</td>
              <td align={"center"}>{createUpArrowLine()}</td>
              <td></td>
              <td></td>
              <td align={"center"}>{createDownArrowLine()}</td>
              <td></td>
              <td></td>
              <td align={"center"}>{createUpArrowLine()}</td>
              <td></td>
              <td></td>
              <td align={"center"}>{createDownArrowLine()}</td>
              <td align={"center"}>{createUpArrowLine()}</td>
              <td align={"center"}>{createUpArrowLine()}</td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle1,border_3_3_1_1)}>{createTipContent('对央行负债','15.93')}</td>
              <td style={styles(baseStyle2,border_0_3_1_1)}>{createTipContent('准备金','23.15')}</td>
              <td style={styles(baseStyle2,border_0_3_0_1)}>{createTipContent('央票','0.03')}</td>
              <td style={border_3_0_0_0}></td>
              <td style={styles(baseStyle1,border_3_3_1_1)} colspan={3}>政府债券发行</td>
              <td style={styles(baseStyle2,border_0_3_3_1)} colspan={3}>财政国库</td>
              <td style={border_0_0_3_0}></td>
              <td style={styles(baseStyle1,border_0_3_1_1)}>非银金融<br/>机构贷款</td>
              <td style={styles(baseStyle2,border_0_3_1_1)}>非银金融<br/>机构准备金</td>
              <td style={styles(baseStyle2,border_0_3_3_1)} ref={this.line1EndRef}>支付机构<br/>全额准备金</td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle1,border_3_0_1_1)}>{createTipContent('实收资本','5.78')}</td>
              <td style={styles(baseStyle2,border_0_0_1_1)}>{createTipContent('国外资产','8.15')}</td>
              <td style={styles(baseStyle2,border_0_0_0_1)}>{createTipContent('政府债券','60.86')}</td>
              <td style={border_3_0_0_0}>{createRightArrowLine()}</td>
              <td style={styles(baseStyle1,border_3_0_3_1)} colspan={6}>{createTipContent('政府债券发行','76.97')}</td>
              <td style={border_0_0_3_0}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_0_0_1_1)}>{createTipContent('政府债券','16.11')}</td>
              <td style={styles(baseStyle0,border_0_0_0_1)}></td>
              <td style={styles(baseStyle0,border_0_0_3_0)}></td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle1,border_3_0_1_1)}>{createTipContent('其他负债','50.88')}</td>
              <td style={styles(baseStyle2,border_0_0_0_1)} colspan={2} rowspan={2}>
                <div style={{display:'flex', flexDirection:'row'}}>
                  <div style={{width:74, display:'flex', justifyContent:'center', alignItems:'center'}}>对非金融<br/>机构债权<br/>180.28</div>
                  <div style={{width:10, display:'flex', justifyContent:'center', alignItems:'center'}}>{createRightBraceFigure(70)}</div>
                  <div style={{width:76}}>
                    <div>{createTipContent('企业债券','14.78')}</div>
                    <div>{createTipContent('发放贷款','165.50')}</div>
                  </div>
                </div>
              </td>
              <td style={border_3_0_0_0}>{createRightArrowLine()}</td>
              <td style={styles(baseStyle1,border_3_0_3_1)} colspan={6}>{createTipContent('企业债券发行','32.07')}</td>
              <td style={border_0_0_3_0}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_0_0_1_1)}>{createTipContent('企业债券','17.29')}</td>
              <td style={styles(baseStyle2,border_0_0_1_1)} rowspan={3}>
                <div style={{display:'flex', flexDirection:'row'}}>
                  <div style={{width:10, display:'flex', justifyContent:'center', alignItems:'center'}}>{createRightBraceFigure(100)}</div>
                  <div style={{width:3}}></div>
                  <div>
                    <div>{createTipContent('委托贷款','11.25')}</div>
                    <div>{createTipContent('信托贷款','4.26')}</div>
                    <div>{createTipContent('其他贷款','3.36')}</div>
                  </div>
                </div>
              </td>
              <td style={styles(baseStyle0,border_0_0_3_0)}></td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle1,border_3_0_1_1)}>{createTipContent('债券发行','15.40')}</td>
              <td style={border_3_0_0_0}>{createRightArrowLine()}</td>
              <td style={styles(baseStyle1,border_3_0_1_1)} colspan={2}>{createTipContent('贷款','165.50')}</td>
              <td style={styles(titleStyle2,border_0_0_1_1)} colspan={2}>政府和企业</td>
              <td style={styles(baseStyle1,border_0_0_3_1)} colspan={2}>{createTipContent('贷款','18.87')}</td>
              <td style={border_0_0_3_0}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_0_0_0_1)}>{createTipContent('发放贷款','18.96')}</td>
              <td style={styles(baseStyle0,border_0_0_3_0)}></td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle1,border_3_0_1_1)}>{createTipContent('国外负债','1.52')}</td>
              <td style={styles(baseStyle2,border_0_0_1_1)}>{createTipContent('其他资产','14.33')}</td>
              <td style={styles(baseStyle2,border_0_0_0_1)}>企业股权</td>
              <td style={border_3_0_0_0}>{createRightArrowLine()}</td>
              <td style={styles(baseStyle1,border_3_0_3_1)} colspan={6}>{createTipContent('股票发行','11.60')}</td>
              <td style={border_0_0_3_0}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_0_0_1_1)}>企业股权</td>
              <td style={styles(baseStyle0,border_0_0_3_0)}></td>
            </tr>
            <tr style={{height:40}}>
              <td style={styles(baseStyle3,border_3_0_0_1)} rowspan={4} colspan={2}>
                <div style={{display:'flex', flexDirection:'row'}}>
                  <div style={{width:100}}>
                    <div ref={this.m1Start1Ref}>单位活期：</div>
                    <div ref={this.m2Start1Ref}>单位定期：</div>
                    <div>不纳入M2：</div>
                    <div ref={this.m1Start2Ref}>个人活期：</div>
                    <div ref={this.m2Start2Ref}>个人定期：</div>
                  </div>
                  <div style={{width:40}}>
                    <div>50.64</div>
                    <div>57.29</div>
                    <div>5.16</div>
                    <div> 40.26</div>
                    <div>109.46</div>
                  </div>
                  <div style={{width:10}}></div>
                  <div style={{width:10, display:'flex', justifyContent:'center', alignItems:'center'}}>{createLeftBraceFigure(90)}</div>
                </div>
              </td>
              <td style={styles(baseStyle1,border_0_0_0_1)} rowspan={4}>{createTipContent('吸收存款','262.81')}</td>
              <td style={border_3_0_0_0}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_3_0_1_3)} colspan={2}>{createTipContent('存款','113.08')}</td>
              <td style={styles(baseStyle2,border_0_0_1_3)} colspan={2}>金融资产</td>
              <td style={styles(baseStyle2,border_0_0_3_3)} colspan={2} ref={this.line2StartRef}>投资资金</td>
              <td style={border_0_0_3_0}></td>
              <td style={styles(baseStyle0,border_0_0_3_0)} colspan={3}></td>
            </tr>

            <tr style={{height:40}}>
              <td colspan={8} style={border_3_0_3_0}></td>
              <td style={styles(baseStyle0,border_0_0_3_0)} colspan={3}></td>
            </tr>

            <tr style={{height:25}}>
              <td style={border_3_0_0_0} rowspan={2}>{createLeftArrowLine()}</td>
              <td colspan={2} style={styles(baseStyle2,border_3_3_1_1)} rowspan={2}>{createTipContent('存款','149.72')}</td>
              <td colspan={2} style={styles(baseStyle2,border_0_3_1_1)}>金融资产</td>
              <td colspan={2} style={styles(baseStyle2,border_0_3_3_1)} rowspan={2} ref={this.line3StartRef}>投资资金</td>
              <td style={border_0_0_3_0}></td>
              <td style={styles(baseStyle0,border_0_0_3_0)} colspan={3} rowspan={2}></td>
            </tr>

            <tr style={{height:15}}>
              <td colspan={2} style={styles(titleStyle2,border_0_0_1_1)} rowspan={2}>个人</td>
              <td style={border_0_0_3_0}></td>
            </tr>

            <tr style={{height:15}}>
              <td style={styles(baseStyle2,border_3_0_1_1)} rowspan={2} ref={this.m0End0Ref}>{createTipContent('库存现金','0.51')}</td>
              <td style={styles(baseStyle0,border_0_0_1_0)} rowspan={2}></td>
              <td style={styles(baseStyle2,border_0_0_0_1)} rowspan={2}>{createTipContent('发放贷款','80.17')}</td>
              <td style={border_3_0_0_0} rowspan={2}>{createRightArrowLine()} </td>
              <td colspan={2} style={styles(baseStyle1,border_3_0_1_3)} rowspan={2}>{createTipContent('贷款','80.16')}</td>
              <td colspan={2} style={styles(baseStyle1,border_0_0_3_3)} rowspan={2}>{createTipContent('贷款','1.88')}</td>
              <td style={border_0_0_3_0} rowspan={2}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_0_1_1_1)} rowspan={2}>{createTipContent('发放贷款','1.88')}</td>
              <td style={styles(baseStyle0,border_0_0_3_0)} rowspan={2} colspan={2}></td>
              <td></td>
            </tr>

            <tr style={{height:25}}>
              <td colspan={2} style={styles(baseStyle2,border_0_0_1_3)} ref={this.line1StartRef}>支付钱包</td>
            </tr>

            <tr style={{height:40}}>
              <td style={styles(baseStyle0,border_3_0_0_0)} colspan={3}></td>
              <td colspan={8} style={border_3_0_3_0}></td>
              <td style={styles(baseStyle0,border_0_0_3_0)} colspan={3}></td>
            </tr>

            <tr style={{height:40}}>
            <td style={styles(baseStyle4,border_3_1_1_1)}>同业交易</td>
              <td style={styles(baseStyle0,{})} colspan={2}></td>
              <td style={styles(baseStyle0,border_0_3_1_0)}></td>
              <td style={styles(baseStyle1,border_0_3_3_1)} colspan={2} rowspan={2} ref={this.m2Start3Ref}>{createTipContent('吸收存款','34.78')}<br/></td>
              <td colspan={2}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_3_3_1_1)} colspan={2} ref={this.line2EndRef}>三方存款</td>
              <td style={styles(baseStyle0,border_0_3_0_0)}></td>
              <td style={styles(baseStyle0,{})}></td>
              <td style={styles(baseStyle4,border_1_1_0_1)}>投资机构</td>
              <td style={styles(baseStyle4,border_1_1_3_1)} ref={this.m2Start4Ref}>货币基金</td>
            </tr>

            <tr style={{height:40}}>
              <td style={styles(titleStyle1,border_3_0_1_0)} colspan={4}>存款机构</td>
              <td colspan={2}>{createLeftArrowLine()}</td>
              <td style={styles(baseStyle2,border_3_0_1_1)} colspan={2}>其他资金</td>
              <td style={styles(titleStyle1,border_0_0_3_0)} colspan={4}>非存款机构</td>
            </tr>

            <tr style={{height:40}}>
              <td style={styles(baseStyle0,border_3_0_1_3)} colspan={4}></td>
              <td style={styles(baseStyle2,border_0_0_3_3)} colspan={2}>{createTipContent('非银机构债权','28.43')}</td>
              <td colspan={2}>{createRightArrowLine()}</td>
              <td style={styles(baseStyle1,border_3_0_1_3)} colspan={2}>{createTipContent('贷款','5.73')}</td>
              <td style={styles(baseStyle0,border_0_0_3_3)} colspan={4}></td>
            </tr>
          </table>
          </div>
          <div style={{width:60}}></div>
          <div>
            <div>
              <div style={{height:20}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>250.87</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>人民币贷款</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>76.97</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>政府债券</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>32.07</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>企业债券</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>11.25</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>委托贷款</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>4.26</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>信托贷款</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>11.60</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>非金融企业境内股票</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>1.43</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>外币贷款</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>2.34</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>未贴现银行承兑汇票</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>0.84</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>存款类机构ABS</div>
              </div>
              <div style={{height:30}}></div>
              <div style={{display:"flex", flexFlow:"row"}}>
                <div style={styles(otherStyle1, {width:50})}>9.53</div>
                <div style={styles(otherStyle2, {width:20})}>-</div>
                <div style={styles(otherStyle3, {width:150})}>贷款核销</div>
              </div>
            </div>
          </div>
          <div style={{flex:100}}></div>
        </div>
        {this.state.loading ? <Spin overlay /> : ""}
      </div>
    )
  }


}