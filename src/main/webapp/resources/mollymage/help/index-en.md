<meta charset="UTF-8">

## Intro

The game server is available for familiarization reasons
[http://codenjoy.com/codenjoy-contest](http://codenjoy.com/codenjoy-contest).

This is the open source game. To realize your game, correct errors in the current
version and make the other corrections, you should
[fork the project](https://github.com/codenjoyme/codenjoy) at first.
There is the description in the Readme.md file in the repository root.
It is specified in the description what to do next.

If any questions, please write in [skype:alexander.baglay](skype:alexander.baglay)
or Email [apofig@gmail.com](mailto:apofig@gmail.com).

Game project (for writing your bot) can be 
found [here](../../../resources/snakebattle/user/clients.zip)

## What is the game about

Keep in mind: when writing your bot you should stick to its movement logic.
The rest of the game is ready for you.

You should write your bot for the hero who will beat the other bots 
by the score it gets. All bots play on the same field of play. The 
hero can move by idle cells to all four directions.

The hero can plant a potion. The potion will explode in 5 ticks (seconds). 
The blast wave can affect inhabitants of the field. All affected by 
the blast wave disappear. You can decline by both your and someone 
else's potion.

On her/his way, the hero can meet a ghost that destroys all heroes on 
its way.

Each destroyed object on the field (hero, ghost, destroyed walls) is 
restored in an instant in the other place. If the hero is damaged, 
the penalty points[*](index-en.md#ask) are allocated to him.

The hero whose potion destroyed something on the map receives bonus 
points[*](index-en.md#ask) as follows: for the destroyed walls, 
for the ghost, for the enemy hero.

All points are summed up. The player with the largest number of points 
is considered to be a winner (prior to the due date).

## How to play
So, the player registers on the server and joining the game. Then you 
should connect from client code to the server via Web Sockets.

Address to connect the game on the server looks like this (you can 
copy it from your game room):

`http://codenjoy.com/codenjoy-contest/board/player/your-player?code=123456789012345678`

Here 'your-player' is your player id and 'code' is your security token. 
Make sure you keep the code safe from prying eyes. Any participant, 
knowing your code, can play on your behalf.

## Board parsing
After connection, the client will regularly (every second) receive 
a line of characters with the encoded state of the field. The format:

`^board=(.*)$`

With the help of regexp you can obtain a board line. Example of the 
line from the server:

<pre>board=☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼ #   # #  #♥#  #  #  &        #☼☼♥☼♥☼♥☼#☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼#☼#☼♥☼#☼#☼☼#♥♥  ♥#   # #♥   # ♥#          ☼☼ ☼ ☼#☼ ☼♥☼ ☼ ☼#☼ ☼ ☼ ☼ ☼&☼ ☼ ☼ ☼☼     ♥          # #            ☼☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼♥☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼☼#       # #       ☺& 2  #  #  #☼☼#☼♥☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼☼#  # ♥#               # ♥   #  ☼☼ ☼ ☼#☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼☼   #♥ #      #                 ☼☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼☼     ## #     #   # #   ♥      ☼☼ ☼ ☼♥☼ ☼ ☼#☼ ☼#☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼☼       #♥       #      ## # ###☼☼ ☼ ☼ ☼#☼ ☼ ☼#☼ ☼ ☼#☼#☼&☼ ☼ ☼ ☼ ☼☼       #       #    ♣# #     ♥ ☼☼ ☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼☼        ## ## ♥             # #☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼☼                   &    ###  ##☼☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼☼                   ♥ ##        ☼☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼♥☼#☼ ☼ ☼ ☼☼     ##         &#         #   ☼☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼☼   #   #         #     # &     ☼☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼☼  #                    ##   &  ☼☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼☼ #    # &        #       #     ☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼</pre>

The line length is equal to the field square. If to insert a wrapping 
character (carriage return) every `sqrt(length(string))` characters, 
you obtain the readable image of the field.

Field example:

<pre>☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼
☼ #   # #  #♥#  #  #  &        #☼
☼♥☼♥☼♥☼#☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼#☼#☼♥☼#☼#☼
☼#♥♥  ♥#   # #♥   # ♥#          ☼
☼ ☼ ☼#☼ ☼♥☼ ☼ ☼#☼ ☼ ☼ ☼ ☼&☼ ☼ ☼ ☼
☼     ♥          # #            ☼
☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼♥☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼
☼#       # #       ☺& 2  #  #  #☼
☼#☼♥☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼
☼#  # ♥#               # ♥   #  ☼
☼ ☼ ☼#☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼
☼   #♥ #      #                 ☼
☼ ☼ ☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼
☼     ## #     #   # #   ♥      ☼
☼ ☼ ☼♥☼ ☼ ☼#☼ ☼#☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼
☼       #♥       #      ## # ###☼
☼ ☼ ☼ ☼#☼ ☼ ☼#☼ ☼ ☼#☼#☼&☼ ☼ ☼ ☼ ☼
☼       #       #    ♣# #     ♥ ☼
☼ ☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼
☼        ## ## ♥             # #☼
☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼
☼                   &    ###  ##☼
☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼
☼                   ♥ ##        ☼
☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼♥☼#☼ ☼ ☼ ☼
☼     ##         &#         #   ☼
☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼
☼   #   #         #     # &     ☼
☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼#☼ ☼
☼  #                    ##   &  ☼
☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼#☼ ☼
☼ #    # &        #       #     ☼
☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼</pre>

Sprite UI

![](board.png)

The first character of the line corresponds to a cell located on the 
left-top corner and has the `[0, 32]` coordinate. The following example 
shows the position of the hero (the `☺` character) – `[19,25]`. left-bottom 
corner has the `[0, 0]` coordinate.

The game is turn-based: Each second, the server sends the updated state 
of the field to the client and waits for response. Within the next 
second the player must give the molly a command. If no command is 
given, the molly will stand still.

Your goal is to make the molly move according to your algorithm. The 
algorithm must earn points as much as possible. The ultimate goal is 
winning the game.

## Symbol breakdown
Please [check it here](elements.md).

## Commands
* `UP`, `DOWN`, `LEFT`, `RIGHT` - move your hero in the specified direction.
* `ACT` - set a potion.  Also, if you have perk `POTION_REMOTE_CONTROL` - 
  you explode yours RC-potions by second command `ACT` when she needs it. 
* `АСТ,<DIRECTION>`,`<DIRECTION>,АСТ` - movement and `ACT` 
  commands can be combined, separating them by comma. During one game cycle 
  the Molly will set a potion and move. Combinations `LEFT,ACT` or `ACT,LEFT` 
  are different: either we move to the left and plant a potion there, or 
  we plant a potion and run away to the left.
* `ACT(1),<DIRECTION>` - works only with perk `POISON_THROWER`. 
  Molly throws a blast of poison in the specified direction. Order 
  `LEFT,ACT(1)` or `ACT(1),LEFT` - does not matter. 
  Without direction, just command `ACT(1)` nothing will happen - hero just 
  will stay on place.
* `ACT(2)` -  works only with perk `POTION_EXPLODER`. All points on 
  the field are exploding in the next tick. Works on all potions (own, 
  team, enemy, RC). Can be used as a single command and can be combined 
  with Direction. Example: `RIGHT,ACT(2)` - in this case Molly will try 
  to move right and all potions on the field explode. 

## Perks
* `POTION_BLAST_RADIUS_INCREASE` - Increase potion radius blast. 
  `{value: +2, timeout: 30}`[*](index-en.md#ask)
* `POTION_COUNT_INCREASE` - Temporarily increase count of settable potions.
  `{count: +4, timeout: 30}`[*](index-en.md#ask)
* `POTION_REMOTE_CONTROL` - Next several potions would be with 
  remote control. Activating by command `ACT`. `{value:  3}`[*](index-en.md#ask)
* `POTION_IMMUNE` - Temporarily gives you immunity from potion blasts.
  `{timeout: 30}`[*](index-en.md#ask)
* `POISON_THROWER`  Hero can shoot by poison cloud. 
  Using: `ACT(1),<DIRECTION>`. `{timeout: 30}`[*](index-en.md#ask)
* `POTION_EXPLODER`  Hero can explode all potions on the field. 
  Using: `ACT(2)`. `{number of  use: +1, timeout: 30}`[*](index-en.md#ask)
 
## Points
* open chests by explode: `1`[*](index-en.md#ask)
* kills ghosts: `10`[*](index-en.md#ask)
* kill other heroes: `20`[*](index-en.md#ask)
* kill enemy heroes: `100`[*](index-en.md#ask)
* catch perk: `5`[*](index-en.md#ask)
* win round: `30`[*](index-en.md#ask)
* death penalty: `-30`[*](index-en.md#ask)
  
## Cases
* you can combine perks
* who earn points after using `POTION_EXPLODER` - decides Sensei[*](index-en.md#ask).
* please be careful with perks on the field. 

## <a id="ask"></a> Ask Sensei
Please ask Sensei about current game settings. You can find Sensei in 
the chat that the organizers have provided to discuss issues.

## Hints
The first task is to run a client’s WebSocket which will connect to 
the server. Then you should “force” the hero to listen to the commands. 
This is the way prepare yourself to the game. The main goal is to 
play meaningfully and win. 

If you are not sure what to do try to implement the following algorithms:

* Move to a random empty adjacent cell.
* Move to a free cell in the direction of the nearest chest.
* Try to hide from future blasts.
* Avoid ghost and other heroes.
* Try to set the bomb so that it explode the box, ghosts or another heroes.

## Clients and API
The client code does not give a considerable handicap to gamers because 
you should spend time to puzzle out the code. However, it is pleasant 
to note that the logic of communication with the server plus some high 
level API for working with the board are implemented already:

* `Point`
  `x`, `y` coordinate.
* `Collection` 
  A set of several objects.
* `Element` 
  Type of the element on the board.
* `int boardSize();`
  Size of the board
* `boolean isAt(Point point, Element element);`
  Whether the given element has given coordinate?
* `boolean isAt(Point point, Collection<Element>elements);`
  Whether any object from the given set is located in given coordinate?
* `boolean isNear(Point point, Element element);`
  Whether the given element is located near the cell with the given coordinate?
* `boolean isBarrierAt(Point point);`
  Whether any obstacle in the cell with given coordinate exists?
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
  before the game starts} cell to the directions: up, down, right, left).

Good luck and may the best win!