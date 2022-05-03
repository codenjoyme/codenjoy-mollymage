"## Clients and API

The client code does not give a considerable handicap to gamers because
you should spend time to puzzle out the code. However, it is pleasant
to note that the logic of communication with the server plus some high
level API for working with the board are implemented already.

* `Solver`
  An empty class with one method — you'll have to fill it with smart logic.
* `Direcion`
  Possible commands for this game.
* `Point`
  `x`, `y` coordinates.
* `Element`
  Type of the element on the board.
* `Board`
  Еncapsulating the line with useful methods for searching
  elements on the board. The following methods can be found in the board:
* `int boardSize();`
  Size of the board
* `boolean isAt(Point point, Element element);`
  Whether the given element has given coordinate?
* `boolean isAt(Point point, Collection<Element>elements);`
  Whether any object from the given set is located in given coordinate?
* `boolean isNear(Point point, Element element);`
  Whether the given element is located near the cell with the given coordinate?
* `int countNear(Point point, Element element);`
  How many elements of the given type exist around the cell with given coordinate?
* `Element getAt(Point point);`
  Element in the current cell.
* `Point getHero();`
  Position of my hero on the board.
* `boolean isGameOver();`
  Whether my hero is alive?
* `Collection<Point> getOtherHeroes();`
  Positions of all other heroes (enemies) on the board.
* `Collection<Point> getBarriers();`
  Positions of all objects that hinder the movements.
* `boolean isBarrierAt(Point point);`
  Whether any obstacle in the cell with given coordinate exists?* 
* `Collection<Point> getGhosts();`
  Positions of all dangers that can destroy the hero.
* `Collection<Point> getWalls();`
  Positions of all concrete walls.
* `Collection<Point> getTreasureBoxes();`
  Positions of all treasure boxes (they can be opened).
* `Collection<Point> getPotions();`
  Positions of all potions.
* `Collection<Point> getFutureBlasts();`
  Positions of all potential hazardous places where the potion 
  can explode (the potion explodes on N {N will be arranged 
  before the game starts} cell to the directions: up, down, right, left)."
