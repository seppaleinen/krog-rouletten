import React from 'react';
import ReactDOM from 'react-dom';
import Game from './game';
import GameStore from './stores/game-store'
import { Provider } from "mobx-react"


describe("Test the game component", function() {
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
  it('Render with empty card array', () => {
  	stores.gameStore.cards = []
    const div = document.createElement('div')
    ReactDOM.render(<App />, div)
  })
})

