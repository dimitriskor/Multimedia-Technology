package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import sample.CustomExceptions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board extends Parent {
    private VBox rows = new VBox();
    private boolean enemy = false;
    public int ships = 5;

    Image exp = new Image("File:src\\sample\\exp.jpg");
    Image sea = new Image("File:src\\sample\\sea over.jpg");
    Image splash = new Image("File:src\\sample\\sea exp.jpg");
    Image carrier = new Image("File:src\\sample\\carrier.png");
    Image battleship = new Image("File:src\\sample\\battleship.png");
    Image cruiser = new Image("File:src\\sample\\cruiser.png");
    Image submarine = new Image("File:src\\sample\\submarine.png");
    Image destroyer = new Image("File:src\\sample\\destroyer.jpg");

    public Board(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 10; x++) {
                Cell c = new Cell(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }

            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }

    public ImagePattern ship_icon(Ship ship){
        if (ship.type == 1)
            return new ImagePattern(carrier);
        else if (ship.type == 2)
            return new ImagePattern(battleship);
        else if (ship.type == 3)
            return new ImagePattern(cruiser);
        else if (ship.type == 4)
            return new ImagePattern(submarine);
        else
            return new ImagePattern(destroyer);
    }

    public boolean placeShip(Ship ship, int x, int y) {
        if (canPlaceShip(ship, x, y)) {
            int length = ship.health;

            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    Cell cell = getCell(x, i);
                    cell.ship = ship;
                    if (!enemy) {
                        ImagePattern pattern;
                        pattern = ship_icon(cell.ship);
                        cell.setFill(pattern);
                        cell.setStroke(Color.BLACK);
                    }
                }
            }
            else {
                for (int i = x; i < x + length; i++) {
                    Cell cell = getCell(i, y);
                    cell.ship = ship;
                    if (!enemy) {
                        ImagePattern pattern;
                        pattern = ship_icon(cell.ship);
                        cell.setFill(pattern);
                        cell.setStroke(Color.BLACK);
                    }
                }
            }

            return true;
        }

        return false;
    }

    public Cell getCell(int x, int y) {
        return (Cell)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    private Cell[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Cell> neighbors = new ArrayList<Cell>();

        for (Point2D p : points) {
            if (isValidPoint(p)) {
                neighbors.add(getCell((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Cell[0]);
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.health;
        try {
            if (ship.vertical) {

                for (int i = y; i < y + length; i++) {
                    if (!isValidPoint(x, i))
                        throw new OversizeException("A ship has been placed outside the board");

                    Cell cell = getCell(x, i);
                    if (cell.ship != null)
                        throw new OverlapTilesException("A ship has been placed on top of another ship");

                    for (Cell neighbor : getNeighbors(x, i)) {
                        if (!isValidPoint(x, i))
                            return false;

                        if (neighbor.ship != null)
                            throw new OverlapTilesException("A ship has been placed next to another ship");
                    }
                }
            } else {
                for (int i = x; i < x + length; i++) {

                    if (!isValidPoint(i, y))
                        throw new OversizeException("A ship has been placed outside the board");

                    Cell cell = getCell(i, y);
                    if (cell.ship != null)
                        throw new OverlapTilesException("A ship has been placed on top of another ship");

                    for (Cell neighbor : getNeighbors(i, y)) {
                        if (!isValidPoint(i, y))
                            return false;

                        if (neighbor.ship != null)
                            throw new OverlapTilesException("A ship has been placed next to another ship");
                    }
                }
            }
        }
        catch (Exception e){
            Alert Error = new Alert(Alert.AlertType.ERROR);
            Error.setTitle("Ship Placement Error");
            Error.setHeaderText("Invalid ship placement");
            Error.setContentText(String.valueOf(e));
            Error.showAndWait();
            System.exit(-2);
        }

        return true;
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    public class Cell extends Rectangle {
        public int x, y;
        public Ship ship = null;
        public boolean wasShot = false;

        private Board board;


        /**
         * Class constructor. Initializes the Cell object that is created.
         * The x, y arguments indicate the coordinations of the Object.
         * The board argument specifies the Board that the object belongs to.
         * <p>
         * This is the Constructor of an object of Class Cell. The values x, y and board that are given
         * as arguments initialize the values of the object. x and y denote the coordinates of the Cell that is created
         * and the board arguments specifies the board that the object is a part of.
         *
         * @param x the x coordinate of the Cell created to the board it belongs to
         * @param y the y coordinate of the Cell created to the board it belongs to
         * @param board the Board which the Cell created belongs to
         * @see Cell
         * @see javafx.scene.shape.Shape#setFill(Paint)
         * @see javafx.scene.shape.Shape#setStroke(Paint)
         * @see #x
         * @see #y
         * @see #board
         * @see Board
         */
        public Cell(int x, int y, Board board) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.board = board;
            setFill(new ImagePattern(sea));
            setStroke(Color.BLACK);
        }

        /**
         * Returns the resault of the shoot. The shooter argument specifies the Attacker that is involved in the shoot.
         * <p>
         * This method returns true if the Cell object was already shot before the call of the method or if it was not shot
         * but the ship field of the object is not null, which means that a ship is on that specific object. Otherwise, it sets
         * the wasShot fiels as true and returns false. Moreover it reduces the round field of the shooter by one, updates its
         * history, by calling the update_history method of the Attacker Class and, if the Cell object contains a ship, the
         * shooters fields hits and points are updated while the ship calls the method hit() and ,if the ship gets sunk, the
         * object board of class Board (that is the field of the Cell object) updates the ships field, reducing it by one
         *
         * @param shooter the Attacker that has shot the given Cell
         * @see #wasShot
         * @see #ship
         * @see #board
         * @see javafx.scene.shape.Shape#setFill(Paint)
         * @see Attacker
         * @see Attacker#rounds
         * @see Attacker#update_history(Cell)
         * @see Ship#hit()
         * @see Attacker#hits
         * @see Attacker#points
         * @see Attacker#enemy_ships
         * @see Board#ships
         * @return The boolean resault of whether the object was already shot or the ship field is not null
         */
        public boolean shoot(Attacker shooter) {
            if (wasShot)
                return wasShot;
            wasShot = true;
            setFill(new ImagePattern(splash));
            shooter.rounds -= 1;
            shooter.update_history(this);
            if (ship != null) {

                ship.hit();
                shooter.hits += 1;
                shooter.points += ship.hit_points;
                setFill(new ImagePattern(exp));
                if (!ship.isAlive()) {
                    board.ships--;
                    shooter.points += ship.sink_points;
                    shooter.enemy_ships -= 1;
                }
                return true;
            }
            return false;
        }
    }
}