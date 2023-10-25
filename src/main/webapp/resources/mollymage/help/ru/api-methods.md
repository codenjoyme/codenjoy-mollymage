* `Solver`
  Пустой класс с одним методом — ты должен(должна) наполнить его умной логикой.
* `Direcion`
  Возможные направления движения для этой игры.
* `Point`
  `x`, `y` координаты.
* `Element`
  Тип элемента на доске.
* `Board`
  Содержит логику для удобного поиска и манипуляции элементами на поле.
  Ты можешь найти следующие методы в Board классе:
* `int boardSize();` 
  Размер доски.
* `boolean isAt(Point point, Element element);` 
  Находится ли в позиции point заданный элемент?
* `boolean isAt(Point point, Collection<Element> elements);` 
  Находится ли в позиции point что-нибудь из заданного набора?
* `boolean isNear(Point point, Element element);` 
  Есть ли вокруг клеточки с координатой point заданный элемент?
* `int countNear(Point point, Element element);` 
  Сколько элементов заданного типа есть вокруг клетки с point?
* `Element getAt(Point point);` 
  Элемент в текущей клетке.
* `Point getHero();` 
  Позиция моего героя на доске.
* `boolean isGameOver();` 
  Жив ли мой герой?
* `Collection<Point> getOtherHeroes();` 
  Позиции всех остальных героев (из твоей команды) на доске.
* `Collection<Point> getEnemyHeroes();` 
  Позиции героев противника на доске.
* `Collection<Point> getBarriers();` 
  Позиции всех объектов препятствующих движению.
* `boolean isBarrierAt(Point point);`
  Есть ли препятствие в клеточке point? 
* `Collection<Point> getGhosts();` 
  Позиции всех призраков.
* `Collection<Point> getWalls();` 
  Позиции всех бетонных стен.
* `Collection<Point> getTreasureBoxes();` 
  Позиции всех сундуков с сокровищами (их можно открывать).
* `Collection<Point> getBombs();` 
  Позиции всех зелий.
* `Collection<Point> getFutureBlasts();` 
  Позиции потенциально опасных мест, где зелье может взорваться. 
  (зелье распространяется на N {решим перед началом игры} клеточек 
  в стороны: вверх, вниз, вправо, влево).
* и так далее... 