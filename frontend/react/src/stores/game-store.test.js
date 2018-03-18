import React from 'react';
import ReactDOM from 'react-dom';
import Card from './game-store'
import GameStore from './game-store'

describe('GameStore tests', function() {
  var gameStore
  beforeEach(function() {
    gameStore = new GameStore()
    jest.useFakeTimers()
  })
  it('duplicatedAndShuffledCards should return 10 results', () => {
    const result = gameStore.duplicatedAndShuffledCards()
    expect(result.length).toEqual(10)
  })
  it('Constructor should set correct values', () => {
    expect(gameStore.cards.length).toEqual(10)
    expect(gameStore.flippedCards.length).toEqual(0)
    expect(gameStore.flipCount).toEqual(0)
  })
  it('Attributes needs to be editable', () => {
    gameStore.cards.pop()
    gameStore.flippedCards.push(new Card(""))
    gameStore.flipCount = 1
    expect(gameStore.cards.length).toEqual(9)
    expect(gameStore.flippedCards.length).toEqual(1)
    expect(gameStore.flipCount).toEqual(1)
  })
  it('Attributes should be reset after gameStore.reset', () => {
    gameStore.cards.pop()
    gameStore.flippedCards.push(new Card(""))
    gameStore.flipCount = 1
    gameStore.restart()
    expect(gameStore.cards.length).toEqual(10)
    expect(gameStore.flippedCards.length).toEqual(0)
    expect(gameStore.flipCount).toEqual(0)
  })
  it('Card should be flipped after calling flip method', () => {
    const card = gameStore.cards[0]
    gameStore.flip(card)

    expect(card.flipped).toEqual(true)
    expect(gameStore.cards.length).toEqual(10)
    expect(gameStore.flippedCards.length).toEqual(1)
    expect(gameStore.flipCount).toEqual(1)
  })
  it('If same card flipped twice state should be returned', () => {
    const card = gameStore.cards[0]
    gameStore.flip(card)
    gameStore.flip(card)

    expect(card.flipped).toEqual(false)
    expect(gameStore.cards.length).toEqual(10)
    expect(gameStore.flippedCards.length).toEqual(0)
    expect(gameStore.flipCount).toEqual(2)
  })
  it('If same card but different ids, then remove from cards', () => {
    const src = "/images/dog-1.jpg"
    const card1 = gameStore.cards.filter(card => src === card.src)[0]
    const card2 = gameStore.cards.filter(card => src === card.src && card.id !== card1.id)[0]

    gameStore.flip(card1)
    gameStore.flip(card2)
    jest.runAllTimers()

    expect(gameStore.cards.length).toEqual(8)
    expect(gameStore.flippedCards.length).toEqual(0)
    expect(gameStore.flipCount).toEqual(2)
  })
  it('If different card then unflip and continue', () => {
    const card1 = gameStore.cards.filter(card => "/images/dog-1.jpg" === card.src)[0]
    const card2 = gameStore.cards.filter(card => "/images/dog-2.jpg" === card.src)[0]

    gameStore.flip(card1)
    gameStore.flip(card2)
    jest.runAllTimers()
    
    expect(gameStore.cards.length).toEqual(10)
    expect(gameStore.flippedCards.length).toEqual(0)
    expect(gameStore.flipCount).toEqual(2)
  })

})
