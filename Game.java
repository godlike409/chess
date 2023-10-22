package com.dch;

import com.dch.board.Board;
import com.dch.board.Move;

import java.util.List;

public class Game {
    private final Board board;

    private final BoardConsoleRenderer renderer = new BoardConsoleRenderer();

    private final List<GameStateChecker> checkers = List.of(
            new StalemateGameStateChecker(),
            new CheckmateGameStateChecker()
    );

    public Game(Board board) {
        this.board = board;
    }

    public void gameLoop() {
        Color colorToMove = Color.WHITE;

        GameState state = determineGameState(board, colorToMove);

        while (state == GameState.ONGOING) {
            renderer.render(board);

            if (colorToMove == Color.WHITE) {
                System.out.println("Белые должны двигаться");
            } else {
                System.out.println("Черные должны двигаться");
            }

            Move move = InputCoordinates.inputMove(board, colorToMove, renderer);

            // make move
            board.makeMove(move);

            // pass move
            colorToMove = colorToMove.opposite();

            state = determineGameState(board, colorToMove);
        }

        renderer.render(board);
        System.out.println("Игра закончилась с состоянием = " + state);
    }

    private GameState determineGameState(Board board, Color color) {
        for (GameStateChecker checker : checkers) {
            GameState state = checker.check(board, color);

            if (state != GameState.ONGOING) {
                return state;
            }
        }

        return GameState.ONGOING;
    }
}
