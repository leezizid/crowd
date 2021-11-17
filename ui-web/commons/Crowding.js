
import { createStore, combineReducers } from 'redux'


const listeners = [];
let store;


export function initStore(reducers) {
  store = createStore(combineReducers(reducers))
}

export function commitState() {
  for(let i = 0; i < arguments.length; i++) {
    store.dispatch(arguments[i])
  }
  for(let i = 0; i < listeners.length; i++) {
    listeners[i].notify()
  }
}

class Listener {

  constructor(stateNames,func) {
    this.stateNames = stateNames;
    this.func = func;
  }

  notify() {
    let newState = store.getState();
    let stateObjects = [];
    let changed = false;
    for(let i = 0; i < this.stateNames.length; i++) {
      let newObj = newState[this.stateNames[i]];
      stateObjects.push(newObj);
      if(!this.oldState || this.oldState[this.stateNames[i]] !== newObj) {
        changed = true;
      }
    }
    this.oldState = newState;
    if(changed) {
      this.func(stateObjects)
    }
  }

}

export function subscribeStates(stateNames, func) {
  let listener = new Listener(stateNames, func)
  listeners.push(listener);
  listener.notify();
  return function() {
    let index = listeners.indexOf(listener);
    if(index >= 0) {
      listeners.splice(index, 1);
    }
  }
}
