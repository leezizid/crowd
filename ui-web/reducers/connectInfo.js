

const ACTION_UPDATE_CONNECT_STATUS = 'action_update_connect_status'
const ACTION_UPDATE_LASTMESSAGE_TIME = 'action_update_lastmessage_time'
const ACTION_UPDATE_LOGIN_INFO = 'action_update_login_info'

export function updateConnectStatusAction(status) {
    return { type: ACTION_UPDATE_CONNECT_STATUS,  status}
}

export function updateLastMessageTimeAction(time) {
    return { type: ACTION_UPDATE_LASTMESSAGE_TIME,  time}
}

export function updateLoginInfoAction(loginInfo) {
    return { type: ACTION_UPDATE_LOGIN_INFO, loginInfo}
}


export default function connectInfo(state = {status:0, lastMessageTime: 0, loginInfo: null}, action) {
    switch (action.type) {
        case ACTION_UPDATE_CONNECT_STATUS:
            return Object.assign({}, state, {status: action.status});
        case ACTION_UPDATE_LASTMESSAGE_TIME:
            return Object.assign({}, state, {lastMessageTime: action.time});
        case ACTION_UPDATE_LOGIN_INFO:
            return Object.assign({}, state, {loginInfo: action.loginInfo});
        default:
            return state
    }
}

