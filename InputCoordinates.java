package com.dch;

import com.dch.board.Board;
import com.dch.board.BoardFactory;
import com.dch.board.Move;
import com.dch.piece.King;
import com.dch.piece.Piece;

import java.util.Scanner;
import java.util.Set;

public class InputCoordinates {

    private static final Scanner scanner = new Scanner(System.in);

    public static Coordinates input() {
        while (true) {
            System.out.println("Пожалуйста, введите фигуру для хода (a2)");

            // a1
            String line = scanner.nextLine();

            if (line.length() != 2) {
                System.out.println("Недопустимый формат");
                continue;
            }

            char fileChar = line.charAt(0);
            char rankChar = line.charAt(1);

            if (!Character.isLetter(fileChar)) {
                System.out.println("Недопустимый формат");
                continue;
            }

            if (!Character.isDigit(rankChar)) {
                System.out.println("Недопустимый формат");
                continue;
            }

            int rank = Character.getNumericValue(rankChar);
            if (rank < 1 || rank > 8) {
                System.out.println("Недопустимый формат");
                continue;
            }

            File file = File.fromChar(fileChar);
            if (file == null) {
                System.out.println("Недопустимый формат");
                continue;
            }

            return new Coordinates(file, rank);
        }
    }

    public static Coordinates inputPieceCoordinatesForColor(Color color, Board board) {
        while (true) {
            System.out.println("Введите координаты для перемещения фигуры");
            Coordinates coordinates = input();

            if (board.isSquareEmpty(coordinates)) {
                System.out.println("Пустой квадрат");
                continue;
            }

            Piece piece = board.getPiece(coordinates);
            if (piece.color != color) {
                System.out.println("Неправильный цвет");
                continue;
            }

            Set<Coordinates> availableMoveSquares = piece.getAvailableMoveSquares(board);
            if (availableMoveSquares.size() == 0) {
                System.out.println("Заблокированный фрагмент");
                continue;
            }

            return coordinates;
        }
    }

    public static Coordinates inputAvailableSquare(Set<Coordinates> coordinates) {
        while (true) {
            System.out.println("Введите свой ход для выбранной фигуры");
            Coordinates input = input();

            if (!coordinates.contains(input)) {
                System.out.println("Недоступный квадрат");
                continue;
            }

            return input;
        }
    }

    public static Move inputMove(Board board, Color color, BoardConsoleRenderer renderer) {
        while (true) {
            // input
            Coordinates sourceCoordinates = InputCoordinates.inputPieceCoordinatesForColor(
                    color, board
            );

            Piece piece = board.getPiece(sourceCoordinates);
            Set<Coordinates> availableMoveSquares = piece.getAvailableMoveSquares(board);

            renderer.render(board, piece);
            Coordinates targetCoordinates = InputCoordinates.inputAvailableSquare(availableMoveSquares);

            Move move = new Move(sourceCoordinates, targetCoordinates);

            if (validateIfKingInCheckAfterMove(board, color, move)) {
                System.out.println("Ваш король подвергся нападению!");
                continue;
            }

            return move;
        }
    }

    private static boolean validateIfKingInCheckAfterMove(Board board, Color color, Move move) {
        Board copy = (new BoardFactory()).copy(board);
        copy.makeMove(move);

        // we trust that there is king on the board
        Piece king = copy.getPiecesByColor(color).stream().filter(piece -> piece instanceof King).findFirst().get();
        return copy.isSquareAttackedByColor(king.coordinates, color.opposite());
    }
}
