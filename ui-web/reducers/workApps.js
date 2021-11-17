

const ACTION_OPEN_WORKAPP = 'action_open_workapp'
const ACTION_CLOSE_WORKAPP = 'action_close_workapp'
const ACTION_ACTIVE_WORKAPP = 'action_active_workapp'

export default function workApps(state = {opened:[], active : ''}, action) {
  // console.log(state)
  switch (action.type) {
    case ACTION_OPEN_WORKAPP:
      return handleOpen(state, action)
    case ACTION_CLOSE_WORKAPP:
      return handleClose(state, action)
    case ACTION_ACTIVE_WORKAPP:
      return handleActive(state, action)
    default:
      return state
  }
}

function handleOpen (state, action) {
  let newState = {};
  Object.assign(newState, state);
  let opened = newState.opened;
  let index = -1;
  for(let i = 0; i < opened.length; i++) {
    let appInfo = opened[i];
    if(appInfo.appId == action.appId) {
      index = i;
      break;
    }
  }
  if(index == -1) {
    opened.push({appId: action.appId, appName: action.appName, appTitle: action.appTitle, appParams: action.appParams});
  }
  newState.active = action.appId;
  return newState;
}

function handleClose (state, action) {
  let newState = {};
  Object.assign(newState, state);
  let opened = newState.opened;
  let index = -1;
  for(let i = 0; i < opened.length; i++) {
    let appInfo = opened[i];
    if(appInfo.appId == action.appId) {
      index = i;
      break;
    }
  }
  if(index == -1) {
    return state; //如果不存在，则直接返回旧状态
  }
  opened.splice(index, 1); //删除指定App
  //如果关闭的功能是活动的功能，则需要确认当前活动功能
  if(newState.active == action.appId) {
    if(index == opened.length) {
      index = opened.length - 1;
    }
    if(index >= 0) {
      newState.active = opened[index].appId;
    } else {
      newState.active = '';
    }
  }
  return newState;
}

function handleActive (state, action) {
  let newState = {};
  Object.assign(newState, state);
  newState.active = action.appId;
  return newState;
}


export function openWorkApp(appId, appName, appTitle, appParams) {
  return {type: ACTION_OPEN_WORKAPP, appId, appName, appTitle, appParams}
}

export function closeWorkApp(appId) {
  return {type: ACTION_CLOSE_WORKAPP, appId}
}

export function activeWorkApp(appId) {
  return {type: ACTION_ACTIVE_WORKAPP, appId}
}
