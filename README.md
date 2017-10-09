# Poker
Poker program written in Java, implemented using Java swing API

### Classes
```
class Card
{
 private:
   int suit;
   int value;
 
 public:
   Card(int userSuit, int userVal);
   int getSuit();
   int getVal();
   String getString();
   String getImage(int);
}
```
```
class Deck
{
 private:
   Vector<Card> d;
 
 public:
   Vector<Card> getDeck();
   void shuffle();
}
```
