

const ACTION_UPDATE_SERVER_INFO = 'action_update_server_info'

export function updateServerInfoAction(name, url) {
    return { type: ACTION_UPDATE_SERVER_INFO,  name, url}
}


export default function serverInfo(state = {name:'', url: ''}, action) {
    switch (action.type) {
        case ACTION_UPDATE_SERVER_INFO:
            return {name: action.name, url: action.url};
        default:
            return state
    }
}

