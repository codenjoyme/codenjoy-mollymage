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
#include "Board.h"

String pointListToString(PointList lst) {
	StringStream ss;
	for (Point i : lst) {
		ss << i.toString() << " ";
	}
	return ss.str();
}

Board::Board(String boardString) {
	for(auto i_str = boardString.find(LL('\n')); i_str != String::npos; i_str = boardString.find(LL('\n'))) {
		boardString.replace(i_str, 1, LL(""));
	}
	board = boardString;
	size = boardSize();
	xyl = LengthToXY(size);
}

Element Board::getAt(int x, int y) const {
	return Element(board.at(xyl.getLength(x, y)));
}

bool Board::isAt(int x, int y, Element el) const {
	if (Point(x, y).isBad(size)) return false;
	return getAt(x, y) == el;
}

bool Board::isAt(int x, int y, std::list<Element> els) const {
	for (auto i : els) {
		if (isAt(x, y, i)) return true;
	}
	return false;
}

PointList Board::findAll(Element el) const {
	PointList rslt;
	for (auto i = 0; i < size*size; ++i) {
		Point pt = xyl.getXY(i);
		if (isAt(pt.getX(), pt.getY(), el)) {
			rslt.push_back(pt);
		}
	}
	return rslt;
}

PointList Board::removeDuplicates(PointList lst) const {
	PointList res;
	for (auto pt : lst) {
		if (std::find(res.begin(), res.end(), pt) == res.end()) {
			res.push_back(pt);
		}
	}
	return res;
}

Point Board::getHero() const {
	PointList rslt;
	rslt.splice(rslt.end(), findAll(Element(LL("HERO"))));
	rslt.splice(rslt.end(), findAll(Element(LL("POTION_HERO"))));
	rslt.splice(rslt.end(), findAll(Element(LL("DEAD_HERO"))));
	return rslt.front();
}

PointList Board::getOtherHeroes() const {
	PointList rslt;
	rslt.splice(rslt.end(), findAll(Element(LL("OTHER_HERO"))));
	rslt.splice(rslt.end(), findAll(Element(LL("OTHER_POTION_HERO"))));
	rslt.splice(rslt.end(), findAll(Element(LL("OTHER_DEAD_HERO"))));
	return rslt;
}

bool Board::isMyHeroDead() const {
	return board.find(Element(LL("DEAD_HERO")).getChar()) != String::npos;
}

int Board::boardSize() const {
	return std::sqrt(board.length());
}

String Board::boardAsString() const {
	StringStream ss;
	for (auto i = 0; i < size; ++i) {
		ss << board.substr(i * size, (i + 1) * size) << LL('\n');
	}
	return ss.str();
}

PointList Board::getBarriers() const {
	PointList rslt = getGhosts();
	rslt.splice(rslt.end(), getWalls());
	rslt.splice(rslt.end(), getPotions());
	rslt.splice(rslt.end(), getDestoyWalls());
	rslt.splice(rslt.end(), getOtherHeroes());
	return removeDuplicates(rslt);
}

String Board::toString() const {
	StringStream ss;
	ss << LL("Board:\n ") << boardAsString()
		<< LL("Hero at: ") << getHero().toString() << LL("\n")
		<< LL("Other Heroes at: ") << pointListToString(getOtherHeroes()) << LL("\n")
		<< LL("Ghosts at: ") << pointListToString(getGhosts()) << LL("\n")
		<< LL("Treasure Boxes at: ") << pointListToString(getDestoyWalls()) << LL("\n")
		<< LL("Potions as: ") << pointListToString(getPotions()) << LL("\n")
		<< LL("Blasts: ") << pointListToString(getBlasts()) << LL("\n")
		<< LL("Expected blasts at: ") << pointListToString(getFutureBlasts()) << LL("\n");
	return ss.str();
}

PointList Board::getGhosts() const {
	return findAll(Element(LL("GHOST")));
}

PointList Board::getWalls() const {
	return findAll(Element(LL("WALL")));
}

PointList Board::getDestoyWalls() const {
	return findAll(Element(LL("TREASURE_BOX")));
}

PointList Board::getPotions() const {
	PointList rslt;
    rslt.splice(rslt.end(), findAll(Element(LL("POTION_TIMER_1"))));
    rslt.splice(rslt.end(), findAll(Element(LL("POTION_TIMER_2"))));
    rslt.splice(rslt.end(), findAll(Element(LL("POTION_TIMER_3"))));
    rslt.splice(rslt.end(), findAll(Element(LL("POTION_TIMER_4"))));
    rslt.splice(rslt.end(), findAll(Element(LL("POTION_TIMER_5"))));
    rslt.splice(rslt.end(), findAll(Element(LL("POTION_HERO"))));
    return rslt;
}

PointList Board::getBlasts() const {
	return findAll(Element(LL("BOOM")));
}

PointList Board::getFutureBlasts() const {
	PointList potions = getPotions();
	potions.splice(potions.end(), findAll(Element(LL("OTHER_POTION_HERO"))));
	potions.splice(potions.end(), findAll(Element(LL("POTION_HERO"))));

	PointList rslt;
	PointList walls = getWalls();
	for (auto bmb : potions) {
		rslt.push_back(bmb);
		PointList potionSurrs = bmb.getSurrounds(size);
		for (auto surr : potionSurrs) {
			if (std::find(walls.begin(), walls.end(), surr) == walls.end()) {
				rslt.push_back(surr);
			}
		}
	}

	return removeDuplicates(rslt);
}

bool Board::isNear(int x, int y, Element el) const {
	if (Point(x, y).isBad(size)) return false;
	PointList nears = Point(x, y).getSurrounds(size);
	bool res = false;
	for (auto pt : nears) {
		res = res || isAt(pt.getX(), pt.getY(), el);
	}
	return res;
}

int Board::countNear(int x, int y, Element el) const {
	PointList nearp = Point(x, y).getSurrounds(size);
	int count = 0;
	for (auto p : nearp) {
		if (isAt(p.getX(), p.getY(), el)) {
			++count;
		}
	}
	return count;
}

bool Board::isBarrierAt(int x, int y) const {
	Point p(x, y);
	if (p.isBad(size)) return false;

	PointList barriers = getBarriers();
	for (auto b : barriers) {
		if (b == p) return true;
	}
	return false;
}
