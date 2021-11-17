

const ACTION_UPDATE_CURRENT_UNIT = 'action_update_current_unit'

export default function currentUnit(state = {id:'', code: '', name: ''}, action) {
  switch (action.type) {
    case ACTION_UPDATE_CURRENT_UNIT:
      return {id: action.id, code: action.code, name: action.name}
    default:
      return state
  }
}


export function updateCurrentUnit(id, code, name) {
  return {type: ACTION_UPDATE_CURRENT_UNIT, id, code, name}
}
