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
  private Action lastAction = Action.RIGHT;

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
  private final Elements[] walls = {Elements.WALL, Elements.DESTROYABLE_WALL, Elements.OTHER_BOMBERMAN
    //  Elements.DESTROYED_WALL,
  };

  public YourSolver(Dice dice) {
    this.dice = dice;
  }

  @Override
  public String get(Board board) {
    this.board = board;
    if (board.isMyBombermanDead()) return "";
    Point location = board.getBomberman();
    switch (lastAction) {
      case UP:
        return goUP(location);
      case DOWN:
        return goDown(location);
      case LEFT:
        return goLeft(location);
      case RIGHT:
        return goRight(location);
      default:
    }

    return STAY;

    //
  }

  public static void main(String[] args) {
    WebSocketRunner.runClient(
        // paste here board page url from browser after registration
        "http://codenjoy.com/codenjoy-contest/board/player/7liq7vxb4uns919pb9wp?code=6617171509894462948",
        // "http://codenjoy.com:80/codenjoy-contest/board/player/3edq63tw0bq4w4iem7nb?code=1234567890123456789",
        new YourSolver(new RandomDice()),
        new Board());
  }

  private String goRight(Point location) {
    // go right
    boolean safe = true;
    final String result;
    if (board.isAt(location.getX() + 1, location.getY(), walls)) {
      safe = false;
    } else {
      for (int i = 1; i < 8; i++) {
        if (board.isAt(location.getX() + i, location.getY(), danger)) {
          safe = false;
          break;
        }
      }
    }
    if (safe) {
      lastAction = Action.RIGHT;
      result = FIRST_BOMB_THEN_RIGHT_MOVE;
    } else {

      result = goDown(location);
    }
    return result;
  }

  private String goLeft(Point location) {
    boolean safe = true;
    final String result;
    if (board.isAt(location.getX() - 1, location.getY(), walls)) {
      safe = false;
    } else {
      for (int i = 1; i < 8; i++) {
        if (board.isAt(location.getX() - i, location.getY(), danger)) {
          safe = false;
          break;
        }
      }
    }

    if (safe) {
      lastAction = Action.LEFT;
      result = FIRST_BOMB_THEN_LEFT_MOVE;
    } else {
      result = goUP(location);
    }
    return result;
  }

  private String goUP(Point location) {
    boolean safe = true;
    final String result;
    // go up
    if (board.isAt(location.getX(), location.getY() + 1, walls)) {
      safe = false;
    } else {
      for (int i = 1; i < 8; i++) {
        if (board.isAt(location.getX(), location.getY() + i, danger)) {
          safe = false;
          break;
        }
      }
    }

    if (safe) {
      lastAction = Action.UP;
      result = FIRST_BOMB_THEN_UP_MOVE;
    } else {
      result = goRight(location);
    }
    return result;
  }

  private String goDown(Point location) {
    boolean safe = true;
    final String result;
    if (board.isAt(location.getX(), location.getY() - 1, walls)) {
      safe = false;
    } else {
      for (int i = 1; i < 8; i++) {
        if (board.isAt(location.getX(), location.getY() - i, danger)) {
          safe = false;
          break;
        }
      }
    }
    if (safe) {
      lastAction = Action.DOWN;
      result = FIRST_BOMB_THEN_DOWN_MOVE;

    } else {
      result = goLeft(location);
    }
    return result;
  }
}
