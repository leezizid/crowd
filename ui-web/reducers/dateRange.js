

const ACTION_UPDATE_DATE_RANGE = 'action_update_date_range'

export default function dateRange(state = {fromDate:new Date(), toDate: new Date()}, action) {
  switch (action.type) {
    case ACTION_UPDATE_DATE_RANGE:
      return {fromDate: action.fromDate, toDate: action.toDate}
    default:
      return state
  }
}


export function updateDateRange(fromDate, toDate) {
  return {type: ACTION_UPDATE_DATE_RANGE, fromDate, toDate}
}

export function subscribeDateRange(listener) {
  return {type: ACTION_UPDATE_DATE_RANGE, fromDate, toDate}
}

