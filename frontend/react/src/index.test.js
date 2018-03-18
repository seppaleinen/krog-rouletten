import React from 'react';
import ReactDOM from 'react-dom';
import Index from './index';
import Game from './game';
import GameStore from './stores/game-store'
import { Provider } from "mobx-react"


describe("Testing index", function() {
  var stores
  var App
  beforeEach(function() {
  	stores = {
      gameStore: new GameStore()
    }
    App = () => 
      <Provider {...stores}>
        <Game />
      </Provider>  
  })
  it('renders without crashing', () => {
    const div = document.createElement('div')
    ReactDOM.render(<App />, div)
  })
})

