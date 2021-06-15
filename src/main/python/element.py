#! /usr/bin/env python3

###
# #%L
# Codenjoy - it's a dojo-like platform from developers to developers.
# %%
# Copyright (C) 2018 Codenjoy
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.
# #L%
###

from argparse import ArgumentError

_ELEMENTS = dict(

## your Molly

    # This is what she usually looks like.
    HERO = b'\xe2\x98\xba'.decode(), # '☺'

    # This is if she is sitting on own potion.
    POTION_HERO = b'\xe2\x98\xbb'.decode(), # '☻'

    # Oops, your Molly is dead (don't worry,
    # she will appear somewhere in next move).
    # You're getting penalty points for each death.
    DEAD_HERO = b'\xd1\xa0'.decode(), # 'Ѡ'

## other players heroes

    # This is what other heroes looks like.
    OTHER_HERO = b'\xe2\x99\xa5'.decode(), # '♥'

    # This is if player is sitting on own potion.
    OTHER_POTION_HERO = b'\xe2\x99\xa0'.decode(), # '♠'

    # Enemy corpse (it will disappear shortly,
    # right on the next move).
    # If you've done it you'll get score points.
    OTHER_DEAD_HERO = b'\xe2\x99\xa3'.decode(), # '♣'

## the potions
    # After Molly set the potion, the timer starts (5 ticks).
    POTION_TIMER_5 = '5',

    # This will blow up after 4 ticks.
    POTION_TIMER_4 = '4',

    # This after 3...
    POTION_TIMER_3 = '3',

    # Two..
    POTION_TIMER_2 = '2',

    # One.
    POTION_TIMER_1 = '1',

    # Boom! this is what is potion does,
    # everything that is destroyable got destroyed.
    BOOM = b'\xd2\x89'.decode(), # '҉'

## walls

    # Indestructible wall - it will not fall from potion.
    WALL = b'\xe2\x98\xbc'.decode(), # '☼'

    # this is a treasure box, it opens with an explosion.
    TREASURE_BOX = '#',

    # this is like a treasure box opens looks
    # like, it will disappear on next move.
    # if it's you did it - you'll get score
    # points. Perhaps a prize will appear.
    OPENING_TREASURE_BOX = 'H',

## soulless creatures

    # This guys runs over the board randomly
    # and gets in the way all the time.
    # If it will touch Molly - she will die.
    # You'd better kill this piece of ... soul,
    # you'll get score points for it.
    GHOST = '&',

    # This is ghost corpse.
    DEAD_GHOST = 'x',

## perks

    # Potion blast radius increase.
    # Applicable only to new potions.
    # The perk is temporary.
    POTION_BLAST_RADIUS_INCREASE = '+',

    # Increase available potions count.
    # Number of extra potions can be set
    # in settings. Temporary.
    POTION_COUNT_INCREASE = 'c',

    # Potion blast not by timer but by second act.
    # Number of RC triggers is limited and c
    # an be set in settings.
    POTION_REMOTE_CONTROL = 'r',

    # Do not die after potion blast
    # (own potion and others as well). Temporary.
    POTION_IMMUNE = 'i',

## a void
    # This is the only place where you can move your Molly.
    NONE = ' '
)


def value_of(char):
    """ Test whether the char is valid Element and return it's name."""
    for value, c in _ELEMENTS.items():
        if char == c:
            return value
    else:
        raise ArgumentError("No such Element: {}".format(char))


class Element:
    """ Class describes the Element objects for Hero game."""
    def __init__(self, n_or_c):
        """ Construct an Element object from given name or char."""
        for n, c in _ELEMENTS.items():
            if n_or_c == n or n_or_c == c:
                self._name = n
                self._char = c
                break
        else:
            raise ArgumentError("No such Element: {}".format(n_or_c))
            
    def get_char(self):
        """ Return the Element's character."""
        return self._char
    
    def __eq__(self, otherElement):
        return (self._name == otherElement._name and
                self._char == otherElement._char)


if __name__ == '__main__':
    raise RuntimeError("This module is not intended to be ran from CLI")
