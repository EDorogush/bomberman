package com.codenjoy.dojo.bomberman.client;

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

import com.codenjoy.dojo.bomberman.model.Elements;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import sun.jvm.hotspot.debugger.posix.elf.ELFException;

/** User: Elena Dorogush */
public class YourSolver implements Solver<Board> {
  private static final String UP_MOVE = Direction.UP.toString();
  private static final String DOWN_MOVE = Direction.DOWN.toString();
  private static final String LEFT_MOVE = Direction.LEFT.toString();
  private static final String RIGHT_MOVE = Direction.RIGHT.toString();
  private static final String STAY = Direction.STOP.toString();

  private static final String FIRST_BOMB_THEN_UP_MOVE =
      Direction.ACT.toString() + "," + Direction.UP.toString();
  private static final String FIRST_BOMB_THEN_DOWN_MOVE =
      Direction.ACT.toString() + "," + Direction.DOWN.toString();
  private static final String FIRST_BOMB_THEN_LEFT_MOVE =
      Direction.ACT.toString() + "," + Direction.LEFT.toString();
  private static final String FIRST_BOMB_THEN_RIGHT_MOVE =
      Direction.ACT.toString() + "," + Direction.RIGHT.toString();

  private static final String FIRST_UP_MOVE_THEN_BOMB =
      Direction.UP.toString() + "," + Direction.ACT.toString();
  private static final String FIRST_DOWN_MOVE_THEN_BOMB =
      Direction.DOWN.toString() + "," + Direction.ACT.toString();
  private static final String FIRST_LEFT_MOVE_THEN_BOMB =
      Direction.LEFT.toString() + "," + Direction.ACT.toString();
  private static final String FIRST_RIGHT_MOVE_THEN_BOMB =
      Direction.RIGHT.toString() + "," + Direction.ACT.toString();
  private static Action lastAction = Action.RIGHT;

  private static final int COUNT_BLOCK = 4;
  private Dice dice;
  private Board board;
  private final Elements[] danger = {
    Elements.MEAT_CHOPPER,
    Elements.BOMB_TIMER_1,
    Elements.BOMB_TIMER_2,
    Elements.BOMB_TIMER_3,
    Elements.BOMB_TIMER_4,
    Elements.BOMB_TIMER_5,
  };
  private final Elements[] walls = {
    Elements.WALL, Elements.DESTROYABLE_WALL, Elements.OTHER_BOMBERMAN
    //  Elements.DESTROYED_WALL,
  };
  private final Elements[] targets = {
    Elements.OTHER_BOMBERMAN, Elements.DESTROYABLE_WALL, Elements.MEAT_CHOPPER
  };

  public YourSolver(Dice dice) {
    this.dice = dice;
  }

  @Override
  public String get(Board board) {
    this.board = board;
    //    if (board.isMyBombermanDead()){
    //      lastAction = Action.RIGHT;
    //      return FIRST_BOMB_THEN_RIGHT_MOVE;
    //    }
    Point location = board.getBomberman();
    if (checkTrap(location)) {
      return Direction.ACT.toString();
    }
    boolean status = checkTarget(location);
    switch (lastAction) {
      case UP:
        return goUP(location, status);
      case DOWN:
        return goDown(location, status);
      case LEFT:
        return goLeft(location, status);
      case RIGHT:
        return goRight(location, status);
      default:
    }

    return STAY;

    //
  }

  public static void main(String[] args) {
    WebSocketRunner.runClient(
        // paste here board page url from browser after registration

        "http://10.6.219.126:80/codenjoy-contest/board/player/jle2fqe5bv9vn7dtfpwb?code=2902638375343900005",
        // elena personal
        // "http://codenjoy.com/codenjoy-contest/board/player/7liq7vxb4uns919pb9wp?code=6617171509894462948",

        new YourSolver(new RandomDice()),
        new Board());
  }

  private String goRight(Point location, boolean status) {
    // go right
    boolean safe = true;
    final String result;
    if (board.isAt(location.getX() + 1, location.getY(), walls)) {
      safe = false;
    } else {
      for (int i = 1; i < COUNT_BLOCK; i++) {
        if (board.isAt(location.getX() + i, location.getY(), danger)) {
          safe = false;
          break;
        }
      }
    }
    if (safe) {
      lastAction = Action.RIGHT;
      result = status ? FIRST_BOMB_THEN_RIGHT_MOVE : RIGHT_MOVE;
    } else {

      result = goDown(location, status);
    }
    return result;
  }

  private String goLeft(Point location, boolean status) {
    boolean safe = true;
    final String result;
    if (board.isAt(location.getX() - 1, location.getY(), walls)) {
      safe = false;
    } else {
      for (int i = 1; i < COUNT_BLOCK; i++) {
        if (board.isAt(location.getX() - i, location.getY(), danger)) {
          safe = false;
          break;
        }
      }
    }

    if (safe) {
      lastAction = Action.LEFT;
      result = status ? FIRST_BOMB_THEN_LEFT_MOVE : LEFT_MOVE;
    } else {
      result = goUP(location, status);
    }
    return result;
  }

  private String goUP(Point location, boolean status) {
    boolean safe = true;
    final String result;
    // go up
    if (board.isAt(location.getX(), location.getY() + 1, walls)) {
      safe = false;
    } else {
      for (int i = 1; i < COUNT_BLOCK; i++) {
        if (board.isAt(location.getX(), location.getY() + i, danger)) {
          safe = false;
          break;
        }
      }
    }

    if (safe) {
      lastAction = Action.UP;
      result = status ? FIRST_BOMB_THEN_UP_MOVE : UP_MOVE;
    } else {
      result = goRight(location, status);
    }
    return result;
  }

  private String goDown(Point location, boolean status) {
    boolean safe = true;
    final String result;
    if (board.isAt(location.getX(), location.getY() - 1, walls)) {
      safe = false;
    } else {
      for (int i = 1; i < COUNT_BLOCK; i++) {
        if (board.isAt(location.getX(), location.getY() - i, danger)) {
          safe = false;
          break;
        }
      }
    }
    if (safe) {
      lastAction = Action.DOWN;
      result = status ? FIRST_BOMB_THEN_DOWN_MOVE : DOWN_MOVE;

    } else {
      result = goLeft(location, status);
    }
    return result;
  }

  private boolean checkTarget(Point location) {

    for (int i = location.getX() - 4; i < location.getX() + 4; i++) {
      for (int j = location.getY() - 4; j < location.getY() + 4; j++) {
        if (board.isAt(i, j, targets)) {
          return true;
        }
      }
      //
    }
    return false;
  }

  private boolean checkTrap(Point location) {
    return board.isAt(
            location.getX() - 1, location.getY(), Elements.WALL, Elements.DESTROYABLE_WALL)
        && board.isAt(
            location.getX() + 1, location.getY(), Elements.WALL, Elements.DESTROYABLE_WALL)
        && board.isAt(
            location.getX(), location.getY() - 1, Elements.WALL, Elements.DESTROYABLE_WALL)
        && board.isAt(
            location.getX(), location.getY() + 1, Elements.WALL, Elements.DESTROYABLE_WALL);
  }
}
