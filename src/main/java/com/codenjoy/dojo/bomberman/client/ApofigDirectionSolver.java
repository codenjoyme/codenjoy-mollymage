package com.codenjoy.dojo.bomberman.client;

import com.codenjoy.dojo.bomberman.client.utils.Board;
import com.codenjoy.dojo.bomberman.client.utils.Dice;
import com.codenjoy.dojo.bomberman.client.utils.Point;
import static com.codenjoy.dojo.bomberman.client.utils.Point.*;

public class ApofigDirectionSolver implements DirectionSolver {

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

        boolean nearDestroyWall = board.isNear(bomberman.getX(), bomberman.getY(), Element.DESTROY_WALL);
        boolean nearBomberman = board.isNear(bomberman.getX(), bomberman.getY(), Element.OTHER_BOMBERMAN);
        boolean nearMeatchopper = board.isNear(bomberman.getX(), bomberman.getY(), Element.MEAT_CHOPPER);
        boolean bombNotDropped = !board.isAt(bomberman.getX(), bomberman.getY(), Element.BOMB_BOMBERMAN);

        bomb = null;
        if ((nearDestroyWall || nearBomberman || nearMeatchopper) && bombNotDropped) {
            bomb = new Point(bomberman);
        }

        direction = tryToMove(bomberman);

        return mergeCommands(bomb, direction);
    }

    private String mergeCommands(Point bomb, Direction direction) {
        if (Direction.STOP.equals(direction)) {
            bomb = null;
        }
        return "" + ((bomb!=null)? Direction.ACT+((direction!=null)?",":""):"") + ((direction!=null)?direction:"");
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
            boolean meatChopperNearWay = board.isNear(newX, newY, Element.MEAT_CHOPPER);

            if (blastAtWay && board.countNear(pt.getX(), pt.getY(), Element.NONE) == 1 && !board.isAt(pt.getX(), pt.getY(), Element.BOMB_BOMBERMAN)) {
                return Direction.STOP;
            }

            again = bombAtWay || barrierAtWay || meatChopperNearWay;

            boolean deadEndAtWay = board.countNear(newX, newY, Element.NONE) == 0 && bomb != null;  // TODO продолжить но с тестами
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
                        !board.isAt(result.changeX(pt.getX()), result.changeY(pt.getY()), Element.NONE)));
        if (count > 10) {
            return null;
        }
        return result;
    }
}
