# Poker
Poker program written in Java, implemented using Java swing API

## Classes
### Card
```
class Card
{
 private:
   int suit;
   int value;
}
```
### Decks - Contains 52 Card objects of all possible suit/value combinations
```
class Deck
{
 private:
   Vector<Card> d;
}
```
### Player
```
class Player
{
 private:
   String name;
   int points;
   int handTotal;
   Vector<Card> hand;
}
```
### Dealer - subclass of Player class
```
class Dealer extends Player
{
 private:
   Deck d;
}
```
