package sample;

import javafx.scene.Parent;

public class Ship extends Parent {

    public static int [] size = {5, 4, 3, 3, 2};
    public static int [] ship_hit = {350, 250, 100, 100, 50};
    public static int [] sink = {1000, 500, 250, 0, 0};
    public static String [] Names = {"Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"};

    public boolean owner;
    public String name;
    public int hit_points;
    public int sink_points;
    public int health;
    public int type;
    public boolean vertical;
    public String state = "safe";

    public Ship(int type, boolean vertical, boolean owner) {
        this.type = type;
        this.vertical = vertical;
        this.owner = owner;
        health = size[type-1];
        hit_points = ship_hit[type-1];
        sink_points = sink[type-1];
        name = Names[type-1];

        /*VBox vbox = new VBox();
        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(30, 30);
            square.setFill(null);
            square.setStroke(Color.BLACK);
            vbox.getChildren().add(square);
        }

        getChildren().add(vbox);*/
    }

    public void hit() {
        health--;
        state = "hit";
        if (!this.isAlive())
            state = "sunk";

    }

    public boolean isAlive() {
        return health > 0;
    }
}