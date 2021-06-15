/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

var util = require('util');
var WSocket = require('ws');

var browser = (browser !== undefined);

var log = function(string) {
    console.log(string);
    if (browser) {
        printLogOnTextArea(string);
    }
};

var printArray = function (array) {
   var result = [];
   for (var index in array) {
       var element = array[index];
       result.push(element.toString());
   }
   return "[" + result + "]";
};

var processBoard = function(boardString) {
    var board = new Board(boardString);
    if (browser) {
        printBoardOnTextArea(board.boardAsString());
    }

    var logMessage = board + "\n\n";
    var answer = new DirectionSolver(board).get().toString();
    logMessage += "Answer: " + answer + "\n";
    logMessage += "-----------------------------------\n";
    
    log(logMessage);

    return answer;
};

var parseBoard = function(message) {
    var pattern = new RegExp(/^board=(.*)$/);
    var parameters = message.match(pattern);
    var board = parameters[1];
    return board;
}

// you can get this code after registration on the server with your email
var url = "http://codenjoy.com:80/codenjoy-contest/board/player/3edq63tw0bq4w4iem7nb?code=12345678901234567890";

url = url.replace("http", "ws");
url = url.replace("board/player/", "ws?user=");
url = url.replace("?code=", "&code=");

function connect() {
    var socket = new WSocket(url);
    log('Opening...');

    socket.on('open', function() {
        log('Web socket client opened ' + url);
    });

    socket.on('close', function() {
        log('Web socket client closed');

        if (!browser) {
            setTimeout(function() {
                connect();
            }, 5000);
        }
    });

    socket.on('message', function(message) {
        var board = parseBoard(message);
        var answer = processBoard(board);
        socket.send(answer);
    });

    return socket;
}

if (!browser) {
    connect();
}

var Element = {
/// your Molly

    // This is what she usually looks like.
    HERO : '☺',

    // This is if she is sitting on own potion.
    POTION_HERO : '☻',

    // Oops, your Molly is dead (don't worry,
    // she will appear somewhere in next move).
    // You're getting penalty points for each death.
    DEAD_HERO : 'Ѡ',

/// other players heroes

    // This is what other heroes looks like.
    OTHER_HERO : '♥',

    // This is if player is sitting on own potion.
    OTHER_POTION_HERO : '♠',

    // Enemy corpse (it will disappear shortly,
    // right on the next move).
    // If you've done it you'll get score points.
    OTHER_DEAD_HERO : '♣',

/// the potions
    // After Molly set the potion, the timer starts (5 ticks).
    POTION_TIMER_5 : '5',

    // This will blow up after 4 ticks.
    POTION_TIMER_4 : '4',

    // This after 3...
    POTION_TIMER_3 : '3',

    // Two..
    POTION_TIMER_2 : '2',

    // One.
    POTION_TIMER_1 : '1',

    // Boom! this is what is potion does,
    // everything that is destroyable got destroyed.
    BOOM : '҉',

/// walls

    // Indestructible wall - it will not fall from potion.
    WALL : '☼',

    // this is a treasure box, it opens with an explosion.
    TREASURE_BOX : '#',

    // this is like a treasure box opens looks
    // like, it will disappear on next move.
    // if it's you did it - you'll get score
    // points. Perhaps a prize will appear.
    OPENING_TREASURE_BOX : 'H',

/// soulless creatures

    // This guys runs over the board randomly
    // and gets in the way all the time.
    // If it will touch Molly - she will die.
    // You'd better kill this piece of ... soul,
    // you'll get score points for it.
    GHOST : '&',

    // This is ghost corpse.
    DEAD_GHOST : 'x',

/// perks

    // Potion blast radius increase.
    // Applicable only to new potions.
    // The perk is temporary.
    POTION_BLAST_RADIUS_INCREASE : '+',

    // Increase available potions count.
    // Number of extra potions can be set
    // in settings. Temporary.
    POTION_COUNT_INCREASE : 'c',

    // Potion blast not by timer but by second act.
    // Number of RC triggers is limited and c
    // an be set in settings.
    POTION_REMOTE_CONTROL : 'r',

    // Do not die after potion blast
    // (own potion and others as well). Temporary.
    POTION_IMMUNE : 'i',

/// a void
    // This is the only place where you can move your Molly.
    NONE : ' '
};

var D = function(index, dx, dy, name){

    var changeX = function(x) {
        return x + dx;
    };

    var changeY = function(y) {
        return y - dy;
    };

    var inverted = function() {
        switch (this) {
            case Direction.UP : return Direction.DOWN;
            case Direction.DOWN : return Direction.UP;
            case Direction.LEFT : return Direction.RIGHT;
            case Direction.RIGHT : return Direction.LEFT;
            default : return Direction.STOP;
        }
    };

    var toString = function() {
        return name;
    };

    return {
        changeX : changeX,

        changeY : changeY,

        inverted : inverted,

        toString : toString,

        getIndex : function() {
            return index;
        }
    };
};

var Direction = {
    UP : D(2, 0, 1, 'up'),                 // you can move
    DOWN : D(3, 0, -1, 'down'),
    LEFT : D(0, -1, 0, 'left'),
    RIGHT : D(1, 1, 0, 'right'),
    ACT : D(4, 0, 0, 'act'),                // drop potion
    STOP : D(5, 0, 0, '')                   // stay
};

Direction.values = function() {
   return [Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.ACT, Direction.STOP];
};

Direction.valueOf = function(index) {
    var directions = Direction.values();
    for (var i in directions) {
        var direction = directions[i];
        if (direction.getIndex() == index) {
             return direction;
        }
    }
    return Direction.STOP;
};

var Point = function (x, y) {
    return {
        equals : function (o) {
            return o.getX() == x && o.getY() == y;
        },

        toString : function() {
            return '[' + x + ',' + y + ']';
        },

        isOutOf : function(boardSize) {
            return x >= boardSize || y >= boardSize || x < 0 || y < 0;
        },

        getX : function() {
            return x;
        },

        getY : function() {
            return y;
        }
    }
};

var pt = function(x, y) {
    return new Point(x, y);
};

var LengthToXY = function(boardSize) {
    function inversionY(y) {
        return boardSize - 1 - y;
    }

    function inversionX(x) {
        return x;
    }

    return {
        getXY : function(length) {
            if (length == -1) {
                return null;
            }
            var x = inversionX(length % boardSize);
            var y = inversionY(Math.trunc(length / boardSize));
            return new Point(x, y);
        },

        getLength : function(x, y) {
            var xx = inversionX(x);
            var yy = inversionY(y);
            return yy*boardSize + xx;
        }
    };
};

var Board = function(board){
    var contains  = function(a, obj) {
        var i = a.length;
        while (i--) {
           if (a[i].equals(obj)) {
               return true;
           }
        }
        return false;
    };

    var removeDuplicates = function(all) {
        var result = [];
        for (var index in all) {
            var point = all[index];
            if (!contains(result, point)) {
                result.push(point);
            }
        }
        return result;
    };

    var boardSize = function() {
        return Math.sqrt(board.length);
    };

    var size = boardSize();
    var xyl = new LengthToXY(size);

    var getHero = function() {
        var result = [];
        result = result.concat(findAll(Element.HERO));
        result = result.concat(findAll(Element.POTION_HERO));
        result = result.concat(findAll(Element.DEAD_HERO));
        return result[0];
    };

    var getOtherHeroes = function() {
        var result = [];
        result = result.concat(findAll(Element.OTHER_HERO));
        result = result.concat(findAll(Element.OTHER_POTION_HERO));
        result = result.concat(findAll(Element.OTHER_DEAD_HERO));
        return result;
    };

    var isMyHeroDead = function() {
        return board.indexOf(Element.DEAD_HERO) != -1;
    };

    var isAt = function(x, y, element) {
       if (pt(x, y).isOutOf(size)) {
           return false;
       }
       return getAt(x, y) == element;
    };

    var getAt = function(x, y) {
        if (pt(x, y).isOutOf(size)) {
           return Element.WALL;
        }
        return board.charAt(xyl.getLength(x, y));
    };

    var boardAsString = function() {
        var result = "";
        for (var i = 0; i < size; i++) {
            result += board.substring(i * size, (i + 1) * size);
            result += "\n";
        }
        return result;
    };

    var getBarriers = function() {
        var all = getGhosts();
        all = all.concat(getWalls());
        all = all.concat(getPotions());
        all = all.concat(getTreasureBoxes());
        all = all.concat(getOtherHeroes());
        all = all.concat(getFutureBlasts());
        return removeDuplicates(all);
    };

    var toString = function() {
        return util.format("%s\n" +
            "Hero at: %s\n" +
            "Other heroes at: %s\n" +
            "Ghosts at: %s\n" +
            "Treasure Boxes at: %s\n" +
            "Potions as: %s\n" +
            "Blasts: %s\n" +
            "Expected blasts at: %s\n" +
            "Perks at: %s",
                boardAsString(),
                getHero(),
                printArray(getOtherHeroes()),
                printArray(getGhosts()),
                printArray(getTreasureBoxes()),
                printArray(getPotions()),
                printArray(getBlasts()),
                printArray(getFutureBlasts()),
                printArray(getPerks())
                );
    };

    var getGhosts = function() {
       return findAll(Element.GHOST);
    };

    var findAll = function(element) {
       var result = [];
       for (var i = 0; i < size*size; i++) {
           var point = xyl.getXY(i);
           if (isAt(point.getX(), point.getY(), element)) {
               result.push(point);
           }
       }
       return result;
   };

   var getWalls = function() {
       return findAll(Element.WALL);
   };

   var getTreasureBoxes = function() {
       return findAll(Element.TREASURE_BOX);
   };

   var getPotions = function() {
       var result = [];
       result = result.concat(findAll(Element.POTION_TIMER_1));
       result = result.concat(findAll(Element.POTION_TIMER_2));
       result = result.concat(findAll(Element.POTION_TIMER_3));
       result = result.concat(findAll(Element.POTION_TIMER_4));
       result = result.concat(findAll(Element.POTION_TIMER_5));
       result = result.concat(findAll(Element.POTION_HERO));
       result = result.concat(findAll(Element.OTHER_POTION_HERO));       
       return result;
   };

   var getPerks = function() {
        var result = [];
        result = result.concat(findAll(Element.POTION_BLAST_RADIUS_INCREASE));
        result = result.concat(findAll(Element.POTION_COUNT_INCREASE));
        result = result.concat(findAll(Element.POTION_REMOTE_CONTROL));
        result = result.concat(findAll(Element.POTION_IMMUNE));
        return result;
   }

   var getBlasts = function() {
       return findAll(Element.BOOM);
   };

   var getFutureBlasts = function() {
       var potions = getPotions();
       var result = [];
       for (var index in potions) {
           var potion = potions[index];
           result.push(potion);
           result.push(new Point(potion.getX() - 1, potion.getY())); // TODO to remove duplicate
           result.push(new Point(potion.getX() + 1, potion.getY()));
           result.push(new Point(potion.getX()    , potion.getY() - 1));
           result.push(new Point(potion.getX()    , potion.getY() + 1));
       }
       var result2 = [];
       for (var index in result) {
           var blast = result[index];
           if (blast.isOutOf(size) || contains(getWalls(), blast)) {
               continue;
           }
           result2.push(blast);
       }
       return removeDuplicates(result2);
   };

   var isAnyOfAt = function(x, y, elements) {
       for (var index in elements) {
           var element = elements[index];
           if (isAt(x, y,element)) {
               return true;
           }
       }
       return false;
   };

   var isNear = function(x, y, element) {
       if (pt(x, y).isOutOf(size)) {
           return false;
       }
       return isAt(x + 1, y, element) || // TODO to remove duplicate
              isAt(x - 1, y, element) || 
              isAt(x, y + 1, element) || 
              isAt(x, y - 1, element);
   };

   var isBarrierAt = function(x, y) {
       return contains(getBarriers(), pt(x, y));
   };

   var countNear = function(x, y, element) {
       if (pt(x, y).isOutOf(size)) {
           return 0;
       }
       var count = 0;
       if (isAt(x - 1, y    , element)) count ++; // TODO to remove duplicate
       if (isAt(x + 1, y    , element)) count ++;
       if (isAt(x    , y - 1, element)) count ++;
       if (isAt(x    , y + 1, element)) count ++;
       return count;
   };

   return {
        size : boardSize,
        getHero : getHero,
        getOtherHeroes : getOtherHeroes,
        isMyHeroDead : isMyHeroDead,
        isAt : isAt,
        boardAsString : boardAsString,
        getBarriers : getBarriers,
        toString : toString,
        getGhosts : getGhosts,
        findAll : findAll,
        getWalls : getWalls,
        getTreasureBoxes : getTreasureBoxes,
        getPotions : getPotions,
        getBlasts : getBlasts,
        getFutureBlasts : getFutureBlasts,
        isAnyOfAt : isAnyOfAt,
        isNear : isNear,
        isBarrierAt : isBarrierAt,
        countNear : countNear,
        getAt : getAt,
        getPerks: getPerks
   };
};

var random = function(n){
    return Math.floor(Math.random()*n);
};

var direction;

var DirectionSolver = function(board){

    return {
        /**
         * @return next hero action
         */
        get : function() {
            var hero = board.getHero();

            // TODO your code here

            return Direction.ACT;
        }
    };
};

