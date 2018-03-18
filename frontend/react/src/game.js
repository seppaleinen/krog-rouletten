import React from "react"
import Card from "./card"
import { inject, observer } from "mobx-react"


@inject("gameStore") @observer
export default class Game extends React.Component {
  gameOn = () => {
  	return (
  		<div>
          {this.props.gameStore.cards.map(card => (
            <Card
          	  key={card.id}
              card={card}/>
          ))}
        </div>
        )
  }

  gameWon = () => {
  	return (
  			<div>
  				Go home youre drunk! This many moves: 
  				{
  					this.props.gameStore.flipCount
  				}
  			</div>
  		)
  }

  render() {
    const { cards } = this.props.gameStore
    return (
      <div>
      	<h1>Card Game Yo</h1>
      	{this.gameOn()}
      	{cards.length === 0 && this.gameWon()}
        <div>
          <button onClick={this.props.gameStore.restart}>Restart game</button>
        </div>
      </div>
    )
  }

}