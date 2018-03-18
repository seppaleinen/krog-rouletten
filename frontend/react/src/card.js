import React from "react"
import classNames from "class-names"
import "./card.css"
import { inject, observer } from "mobx-react"

@inject("gameStore") @observer
export default class Card extends React.Component {
  handleClick = () => {
    this.props.gameStore.flip(this.props.card)
  }

  render() {
    return (
      <div
        className="card"
        onClick={this.handleClick}>
        <div
          className={classNames("image", { flipped: this.props.card.flipped })}
          style={{ backgroundImage: `url(${this.props.card.src})` }} />
      </div>
    )
  }

}