<meta charset="UTF-8">

## Symbol breakdown
| Sprite | Code | Description |
| -------- | -------- | -------- |
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_timer_5.png" style="height:100%;" /> | `POTION_TIMER_5('5')` | After Molly set the potion, the timer starts (5 ticks). | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_timer_4.png" style="height:100%;" /> | `POTION_TIMER_4('4')` | This potion will blow up after 4 ticks. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_timer_3.png" style="height:100%;" /> | `POTION_TIMER_3('3')` | This after 3... | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_timer_2.png" style="height:100%;" /> | `POTION_TIMER_2('2')` | Two.. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_timer_1.png" style="height:100%;" /> | `POTION_TIMER_1('1')` | One. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/blast.png" style="height:100%;" /> | `BLAST('҉')` | Boom! this is what is potion does, everything that is destroyable got destroyed. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/wall.png" style="height:100%;" /> | `WALL('☼')` | Indestructible wall - it will not fall from potion. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/treasure_box.png" style="height:100%;" /> | `TREASURE_BOX('#')` | This is a treasure box, it opens with an explosion. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/treasure_box_opening.png" style="height:100%;" /> | `TREASURE_BOX_OPENING('H')` | This is like a treasure box opens looks like, it will disappear on next move. If it's you did it - you'll get score points. Perhaps a prize will appear. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/ghost.png" style="height:100%;" /> | `GHOST('&')` | This guys runs over the board randomly and gets in the way all the time. If it will touch Molly - she will die. You'd better kill this piece of ... soul, you'll get score points for it. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/ghost_dead.png" style="height:100%;" /> | `GHOST_DEAD('x')` | This is ghost corpse. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_blast_radius_increase.png" style="height:100%;" /> | `POTION_BLAST_RADIUS_INCREASE('+')` | Temporarily increase potion radius blast. Applicable only to new potions. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_count_increase.png" style="height:100%;" /> | `POTION_COUNT_INCREASE('c')` | Temporarily increase available potions count. Number of extra potions can be set in settings*. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_remote_control.png" style="height:100%;" /> | `POTION_REMOTE_CONTROL('r')` | Next several potions would be with remote control. Activating by command ACT. Number of RC triggers is limited and can be set in settings*. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_immune.png" style="height:100%;" /> | `POTION_IMMUNE('i')` | Temporarily gives you immunity from potion blasts (own potion and others as well). | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/poison_thrower.png" style="height:100%;" /> | `POISON_THROWER('T')` | Hero can shoot by poison cloud. Using: ACT(1)+Direction. Temporary. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/potion_exploder.png" style="height:100%;" /> | `POTION_EXPLODER('A')` | Hero can explode all potions on the field. Using: ACT(2). Temporary. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/none.png" style="height:100%;" /> | `NONE(' ')` | A void. This is the only place where you can move your Molly. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/hero.png" style="height:100%;" /> | `HERO('☺')` | This is what your Molly usually looks like. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/hero_potion.png" style="height:100%;" /> | `HERO_POTION('☻')` | This is if your Molly is sitting on own potion. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/hero_dead.png" style="height:100%;" /> | `HERO_DEAD('Ѡ')` | Oops, your Molly is dead (don't worry, she will appear somewhere in next move). You're getting penalty points for each death. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/other_hero.png" style="height:100%;" /> | `OTHER_HERO('♥')` | This is what other heroes looks like. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/other_hero_potion.png" style="height:100%;" /> | `OTHER_HERO_POTION('♠')` | This is if other hero is sitting on own potion. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/other_hero_dead.png" style="height:100%;" /> | `OTHER_HERO_DEAD('♣')` | Other hero corpse (it will disappear shortly, right on the next move). If you've done it you'll get score points. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/enemy_hero.png" style="height:100%;" /> | `ENEMY_HERO('ö')` | This is what enemy heroes looks like. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/enemy_hero_potion.png" style="height:100%;" /> | `ENEMY_HERO_POTION('Ö')` | This is if enemy hero is sitting on own potion. | 
|<img src="/codenjoy-contest/resources/mollymage/sprite/enemy_hero_dead.png" style="height:100%;" /> | `ENEMY_HERO_DEAD('ø')` | Enemy hero corpse (it will disappear shortly, right on the next move). If you've done it you'll get score points. | 
