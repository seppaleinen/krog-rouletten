import { observable, action } from "mobx"
import shuffle from "./shuffle"


const photos = [
  "/images/dog-1.jpg",
  "/images/dog-2.jpg",
  "/images/dog-3.jpg",
  "/images/dog-4.jpg",
  "/images/dog-5.jpg"
]

class Card {
	id
	src
	@observable flipped

	constructor(src) {
		this.id = Math.random()
		this.src = src
	}

	@action flip() {
		this.flipped = !this.flipped
	}
}

export default class GameStore {
	@observable cards = []
	@observable flippedCards = []
	@observable flipCount = 0

	constructor() {
		this.cards = this.duplicatedAndShuffledCards()
	}

	@action flip(card) {
		card.flip()
		this.flipCount = this.flipCount + 1
		this.flippedCards.push(card)

		if (this.flippedCards.length === 2) {
	      const flippedCards = this.flippedCards
          const firstCard = flippedCards[0]
          const secondCard = flippedCards[1]
          this.flippedCards = []
          if(firstCard.id !== secondCard.id) {
      	    setTimeout(() => {
      	      if(firstCard.src === secondCard.src) {
      	        this.cards = this.cards.filter(card => card.src !== firstCard.src)
              } else {
                flippedCards.forEach(card => {
                  card.flip()
                })
              }
      	    }, 1000)
        } 
      }
	}

	restart = () => {
		this.cards = this.duplicatedAndShuffledCards()
		this.flippedCards = []
		this.flipCount = 0
	}

	duplicatedAndShuffledCards = () => (
      shuffle([...photos, ...photos]).map(src => new Card(src))
    )
}