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
﻿#include "Element.h"

Element::Element(Char el) {
	elem.first = valueOf(el);
	elem.second = el;
}

Element::Element(String name) {
	elem.second = Elements.at(name);
	elem.first = name;
}

Char Element::getChar() const {
	return elem.second;
}

String Element::valueOf(Char ch) const {
	for (auto i : Elements) {
		if (i.second == ch) return i.first;
	}
	throw std::invalid_argument("Element::valueOf(Char ch): No such Elment for " + ch);
}

bool Element::operator==(const Element& el) const {
	return elem == el.elem;
}

ElementMap Element::initialiseElements() {
	ElementMap mapOfElements;

/// your Molly

    // This is what she usually looks like.
    mapOfElements[LL("HERO")] = LL('☺');

    // This is if she is sitting on own potion.
     mapOfElements[LL("POTION_HERO")] = LL('☻');

    // Oops, your Molly is dead (don't worry,
    // she will appear somewhere in next move).
    // You're getting penalty points for each death.
    mapOfElements[LL("DEAD_HERO")] = LL('Ѡ');

/// other players heroes

    // This is what other heroes looks like.
    mapOfElements[LL("OTHER_HERO")] = LL('♥');

    // This is if player is sitting on own potion.
    mapOfElements[LL("OTHER_POTION_HERO")] = LL('♠');

    // Enemy corpse (it will disappear shortly,
    // right on the next move).
    // If you've done it you'll get score points.
    mapOfElements[LL("OTHER_DEAD_HERO")] = LL('♣');

/// the potions
    // After Molly set the potion, the timer starts (5 ticks).
    mapOfElements[LL("POTION_TIMER_5")] = LL('5');

    // This will blow up after 4 ticks.
    mapOfElements[LL("POTION_TIMER_4")] = LL('4');

    // This after 3...
    mapOfElements[LL("POTION_TIMER_3")] = LL('3');

    // Two..
    mapOfElements[LL("POTION_TIMER_2")] = LL('2');

    // One.
    mapOfElements[LL("POTION_TIMER_1")] = LL('1');

    // Boom! this is what is potion does,
    // everything that is destroyable got destroyed.
    mapOfElements[LL("BOOM")] = LL('҉');

/// walls

    // Indestructible wall - it will not fall from potion.
    mapOfElements[LL("WALL")] = LL('☼');

    // this is a treasure box, it opens with an explosion.
    mapOfElements[LL("TREASURE_BOX")] = LL('#');

    // this is like a treasure box opens looks
    // like, it will disappear on next move.
    // if it's you did it - you'll get score
    // points. Perhaps a prize will appear.
    mapOfElements[LL("OPENING_TREASURE_BOX")] = LL('H');

/// soulless creatures

    // This guys runs over the board randomly
    // and gets in the way all the time.
    // If it will touch Molly - she will die.
    // You'd better kill this piece of ... soul,
    // you'll get score points for it.
    mapOfElements[LL("GHOST")] = LL('&');

    // This is ghost corpse.
    mapOfElements[LL("DEAD_GHOST")] = LL('x');

/// perks

    // Potion blast radius increase.
    // Applicable only to new potions.
    // The perk is temporary.
    mapOfElements[LL("POTION_BLAST_RADIUS_INCREASE")] = LL('+');

    // Increase available potions count.
    // Number of extra potions can be set
    // in settings. Temporary.
    mapOfElements[LL("POTION_COUNT_INCREASE")] = LL('c');

    // Potion blast not by timer but by second act.
    // Number of RC triggers is limited and c
    // an be set in settings.
    mapOfElements[LL("POTION_REMOTE_CONTROL")] = LL('r');

    // Do not die after potion blast
    // (own potion and others as well). Temporary.
    mapOfElements[LL("POTION_IMMUNE")] = LL('i');

    // Poison Thrower. Allows the hero to throw poisonous liquid.
    mapOfElements[LL("POISON_THROWER")] = LL('T');

/// a void
    // This is the only place where you can move your Molly.
    mapOfElements[LL("NONE")] = LL(' ');

	return mapOfElements;
};

const ElementMap Element::Elements = Element::initialiseElements();
