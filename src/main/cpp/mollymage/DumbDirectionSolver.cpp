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
#include "DumbDirectionSolver.h"


String DumbDirectionSolver::get(Board b) {
	board = b;
	Point hero = board.getHero();

    bool nearDestroyWall = board.isNear(hero.getX(), hero.getY(), Element(LL("TREASURE_BOX")));
    bool nearHero = board.isNear(hero.getX(), hero.getY(), Element(LL("OTHER_HERO")));
    bool nearGhost = board.isNear(hero.getX(), hero.getY(), Element(LL("GHOST")));
    bool potionNotDropped = !board.isAt(hero.getX(), hero.getY(), Element(LL("POTION_HERO")));

	potion.setNull(true);
    if ((nearDestroyWall || nearHero || nearGhost) && potionNotDropped) {
        potion = hero;
		potion.setNull(false);
    }

    direction = tryToMove(hero);

    return mergeCommands(potion, direction);
}

String DumbDirectionSolver::mergeCommands(Point potion, Direction direction) const {
	if (direction == Direction(LL("STOP"))) {
		potion.setNull(true);
	}
	StringStream ss;
	if (!potion.isNull()) {
		ss << Direction(LL("ACT")).toString();
		if (direction != Direction(LL("NULL"))) {
			ss << LL(",");
		}
	}
	ss << direction.toString();
	return ss.str();
}

Direction DumbDirectionSolver::tryToMove(Point pt) {
	int count = 0;
	int newX = pt.getX();
	int newY = pt.getY();
	Direction result(LL("NULL"));
	bool again = false;
	do {
			result = whereICanGoFrom(pt);
			if (result.isNull()) {
				return result;
			}

			newX = result.changeX(pt.getX());
			newY = result.changeY(pt.getY());

			bool potionAtWay = (!potion.isNull()) && (potion == Point(newX, newY));
			bool barrierAtWay = board.isBarrierAt(newX, newY);
			auto futBla = board.getFutureBlasts();
			bool blastAtWay = (std::find(futBla.begin(), futBla.end(), Point(newX, newY)) != futBla.end());
			bool ghostNearWay = board.isNear(newX, newY, Element(LL("GHOST")));

			if (blastAtWay &&
				board.countNear(pt.getX(), pt.getY(), Element(LL("NONE"))) == 1 &&
				!board.isAt(pt.getX(), pt.getY(), Element(LL("POTION_HERO")))) {
					result = Direction(LL("STOP"));
					return result;
			}

			again = potionAtWay || barrierAtWay || ghostNearWay;

			bool deadEndAtWay = (board.countNear(newX, newY, Element(LL("NONE"))) == 0 && !potion.isNull());
			if (deadEndAtWay) {
				potion.setNull(true);
			}
			if (result == Direction(LL("NULL"))) {
				again = true;
			}
	} while (count++ < 20 && again);
	if (count >= 20) {
		result = Direction(LL("ACT"));
    }
	return result;
}

Direction DumbDirectionSolver::whereICanGoFrom(Point pt) {
	Direction result;
	int count = 0;
	result = Direction(Direction::valueOf(dice->next(3)));
	while (count++ < 10 &&
			(
				(result.inverted() == direction && potion.isNull()) ||
				!board.isAt(result.changeX(pt.getX()), result.changeY(pt.getY()), Element(LL("NONE")))
			)
		   )  {
		result = Direction(Direction::valueOf(dice->next(3)));
	}

	if (count > 10) {
		result = Direction(LL("NULL"));
    }
    return result;
}
