package com.codenjoy.dojo.bomberman.client.ai;

import com.codenjoy.dojo.bomberman.client.Board;
import com.codenjoy.dojo.bomberman.model.Elements;
import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.DirectionSolver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import static com.codenjoy.dojo.services.PointImpl.*;

public class ApofigDirectionSolver implements DirectionSolver<Board> {

    private Direction direction;
    private Point bomb;
    private Dice dice;
    private Board board;

    public ApofigDirectionSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        Point bomberman = board.getBomberman();

        boolean nearDestroyWall = board.isNear(bomberman.getX(), bomberman.getY(), Elements.DESTROY_WALL);
        boolean nearBomberman = board.isNear(bomberman.getX(), bomberman.getY(), Elements.OTHER_BOMBERMAN);
        boolean nearMeatchopper = board.isNear(bomberman.getX(), bomberman.getY(), Elements.MEAT_CHOPPER);
        boolean bombNotDropped = !board.isAt(bomberman.getX(), bomberman.getY(), Elements.BOMB_BOMBERMAN);

        bomb = null;
        if ((nearDestroyWall || nearBomberman || nearMeatchopper) && bombNotDropped) {
            bomb = new PointImpl(bomberman);
        }

        direction = tryToMove(bomberman);

        return mergeCommands(bomb, direction);
    }

    private String mergeCommands(Point bomb, Direction direction) {
        if (Direction.STOP.equals(direction)) {
            bomb = null;
        }
        return "" + ((bomb!=null)? Direction.ACT+((direction!=null)?",":""):"") +
                ((direction!=null)?direction:"");
    }

    private Direction tryToMove(Point pt) {
        int count = 0;
        int newX = pt.getX();
        int newY = pt.getY();
        Direction result = null;
        boolean again = false;
        do {
            result = whereICAnGoFrom(pt);
            if (result == null) {
                return null;
            }

            newX = result.changeX(pt.getX());
            newY = result.changeY(pt.getY());

            boolean bombAtWay = bomb != null && bomb.equals(pt(newX, newY));
            boolean barrierAtWay = board.isBarrierAt(newX, newY);
            boolean blastAtWay = board.getFutureBlasts().contains(pt(newX, newY));
            boolean meatChopperNearWay = board.isNear(newX, newY, Elements.MEAT_CHOPPER);

            if (blastAtWay && board.countNear(pt.getX(), pt.getY(), Elements.NONE) == 1 &&
                    !board.isAt(pt.getX(), pt.getY(), Elements.BOMB_BOMBERMAN)) {
                return Direction.STOP;
            }

            again = bombAtWay || barrierAtWay || meatChopperNearWay;

            // TODO продолжить но с тестами
            boolean deadEndAtWay = board.countNear(newX, newY, Elements.NONE) == 0 && bomb != null;
            if (deadEndAtWay) {
                bomb = null;
            }
        } while (count++ < 20 && again);

        if (count < 20) {
            return result;
        }
        return Direction.ACT;
    }

    private Direction whereICAnGoFrom(Point pt) {
        Direction result;
        int count = 0;
        do {
            result = Direction.valueOf(dice.next(4));
        } while (count++ < 10 &&
                ((result.inverted() == direction && bomb == null) ||
                        !board.isAt(result.changeX(pt.getX()), result.changeY(pt.getY()), Elements.NONE)));
        if (count > 10) {
            return null;
        }
        return result;
    }
}
