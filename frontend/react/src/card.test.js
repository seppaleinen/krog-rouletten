import React from 'react';
import ReactDOM from 'react-dom';
import Card from './card';
import GameStore from './stores/game-store'
import { Provider } from "mobx-react"


describe('Testing card', function() {
  var stores
  var App
  beforeEach(function() {
  	stores = {
      gameStore: new GameStore()
    }
    App = () => 
      <Provider {...stores}>
        <Card card={stores.gameStore.cards.filter(card => card.src = "/images/dog-1.jpg")[0]}/>
      </Provider>  
  })
  it('renders without crashing', () => {
    const div = document.createElement('div')
    ReactDOM.render(<App />, div)
  })
})
