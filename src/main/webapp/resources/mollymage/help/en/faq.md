## FAQ

* I wonder what will happen if two players will try to move their heroes
  on one cell simultaneously? Both mages will die?
  + Someone will move, the second one will stay on its previous place.
* What to do after cloning the javascript client?
  + Information how to start play you can find in the ReadMe.md file.
    You need to fill with your date this file `browser-0-settings.js`.
    And after that run file `browser-2-run.html`. Logic for your bot you need
    to implement in `codenjoy-javascript-client/games/mollymage/solver.js`. 
* What will I see on a client when dead ghost (created after destruction
  of a perk) meet treasure box on his way?
  + Destroyed perks just want to kill you. And he will try to get to you
    in the shortest way possible. If box appear on his way - he'll find
    another way to you.
* It seemed to me that they can destroy treasure boxes on their way
  + I see one thing in the server code, that if the treasure box appeared
    under the ghostHunter - he shows to us as opened treasure box
* Is there a way to get the information about what perks are active for
  the hero or should have to maintain this by ourselves?
  + Only maintain by yourself
* I am not able to find information about how long one perk is active;
  please share the link for the information in case I have missed this.
  + Sometimes it can changes. So here you can get server game settings
    [in json](/codenjoy-contest/rest/settings/player).
* So just wanted to double check - the finals - do we do rounds there? or
  the type of the game like indefinite free for all?
  + Final will be in Rounds with 7-9 heroes per room. The Heroes in the
    rooms will be constantly shuffling. There will be no fixed playgroups.
    Playing fields will also change from round to round.
* Now on map nothing moving - is it working now? Or still under maintenance?
  + Please check, maybe you in another game room. There are demo game flood
    room and event room. Please join in the event room.
* It would be good - if to have some sample code to have
  + Would be. Maybe some tips will help you. Please look into the classes
    which presented in your client. Class Board as usual has some methods
    that can help you analyze field state
* Fore some reason, flying GHOST_DEADs are shown as 'opening treasure boxes',
  is it an issue or I don't understand smth?
  + This is expected; blasting potions create flying ghosts which will find
    shortest path opening treasure to kill you.
* Is it possible for java client to load the local game in browser?
  + Java client could be loaded only from IDE or console. It does not have
    browser version. Only JS client has web version.
* I'm interested how the final would be conducted... Do we need to reconnect
  to the same server or other server? Or the score would be skipped and all
  connections would be saved?There is a risk that I could be absent at the
  moment, and would like to participate and look who's bot is the most
  successful. So I would like to plan my steps.
  + It will be in the same room. You need to play like you played before.
    The finale will begin with the room resetting points for all players. That's all.
* How to combine in JS both `ACT` and `LEFT`?
  + `return [Direction.ACT, Direction.LEFT];`