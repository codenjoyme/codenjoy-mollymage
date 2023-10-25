## What to do

The game is turn-based: Each second, the server sends the updated state 
of the field to the client and waits for response. Within the next 
second the player must give the molly a command. If no command is 
given, the molly will stand still.

Your goal is to make the molly move according to your algorithm. The 
algorithm must earn points as much as possible. The ultimate goal is 
winning the game.

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
  `{value: +2, timeout: 30}`[(?)](#ask)
* `POTION_COUNT_INCREASE` - Temporarily increase count of settable potions.
  `{count: +4, timeout: 30}`[(?)](#ask)
* `POTION_REMOTE_CONTROL` - Next several potions would be with 
  remote control. Activating by command `ACT`. `{value:  3}`[(?)](#ask)
* `POTION_IMMUNE` - Temporarily gives you immunity from potion blasts.
  `{timeout: 30}`[(?)](#ask)
* `POISON_THROWER`  Hero can shoot by poison cloud. 
  Using: `ACT(1),<DIRECTION>`. `{timeout: 30}`[(?)](#ask)
* `POTION_EXPLODER`  Hero can explode all potions on the field. 
  Using: `ACT(2)`. `{number of  use: +1, timeout: 30}`[(?)](#ask)

## Cases

* you can combine perks
* who earn points after using `POTION_EXPLODER` - decides Sensei[(?)](#ask).
* please be careful with perks on the field.

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