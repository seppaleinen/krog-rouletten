import React from 'react'
import ReactDOM from 'react-dom'
import './index.css'
import Game from "./game"
import { Provider } from "mobx-react"
import GameStore from "./stores/game-store"

const stores  = {
  gameStore: new GameStore()
}

const App = () => 
  <Provider {...stores}>
    <Game />
  </Provider>

export default ReactDOM.render(<App/>, document.getElementById('root') || document.createElement('div'))
