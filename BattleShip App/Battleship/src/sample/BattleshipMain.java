/* Errors to  handle:

* work on the AI

Na ftiaksw ta errors:
* Gia invalid input x,y na petaei parathiro k oxi na kleinei
* Na petaei alert box gia ta try-catch

*/

package sample;

import javafx.scene.image.Image;
import java.io.*;
import java.lang.Exception;
import java.util.Random;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sample.CustomExceptions.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import sample.Board.Cell;
import sample.PopUp.*;

public class BattleshipMain extends Application {

    Media soundtrack = new Media(new File("src\\sample\\soundtrack.mp3").toURI().toString());
    MediaPlayer mp3 = new MediaPlayer(soundtrack);

    String id = new String("default");
    private boolean init = false;
    private int direction = -1;

    BorderPane root = new BorderPane();

    private Board enemyBoard, playerBoard;
    private HBox Top;
    private boolean hit = false;

    private int shipsToPlace = 5;

    private boolean enemyTurn = true;
    private boolean first;

    private Random random = new Random();

    private Attacker Player = new Attacker(true, enemyBoard);
    private Attacker Enemy = new Attacker(false, playerBoard);

    int[][] my_data = new int[5][4], enemy_data = new int[5][4];
    Media splash_sound = new Media(new File("src\\sample\\splash sound.mp3").toURI().toString());
    Media explosion_sound = new Media(new File("src\\sample\\explosion sound.mp3").toURI().toString());
    MediaPlayer splash_mp3 = new MediaPlayer(splash_sound);
    MediaPlayer explosion_mp3 = new MediaPlayer(explosion_sound);


    private void read_file(File mine, File enemy) throws IOException {

        FileReader read_mine = new FileReader(mine);
        FileReader read_enemy = new FileReader(enemy);
        int[][] my_ships = new int[5][7], enemy_ships = new int[5][7];

        try {

            /******
             * Read each line and check if it contains more info
             ******/

            for(int i = 0; i < 5; i++) {
                for (int k = 0; k < 7; k++) {
                    my_ships[i][k] = read_mine.read();
                }
                int temp = read_mine.read();
                if (read_mine.read() != '\n' && temp != -1)
                    throw new InputFileException("Invalid row size in file: ");
                for(int k = 0; k < 4; k++)
                    my_data[i][k] = Integer.parseInt(String.valueOf((char)my_ships[i][2*k]));
            }
            if (read_mine.read() != -1)
                throw new InputFileException("Invalid column size in file: "+mine);



            for(int i = 0; i < 5; i++) {
                for (int k = 0; k < 7; k++) {
                    enemy_ships[i][k] = read_enemy.read();
                }
                int temp = read_enemy.read();
                if (read_enemy.read() != '\n' && temp != -1)
                    throw new InputFileException("Invalid row size in file: " + enemy);
                for(int k = 0; k < 4; k++)
                    enemy_data[i][k] = Integer.parseInt(String.valueOf((char)enemy_ships[i][2*k]));
            }
            if (read_enemy.read() != -1)
                throw new InputFileException("Invalid column size in file: "+enemy);



            /******
             * Chech for correct types and orientation
             ******/
            int[] test_player = new int[5], test_enemy = new int[5];
            for (int i = 0; i < 5; i++){
                test_player[my_data[i][0]-1] = my_data[i][0];
                test_enemy[enemy_data[i][0]-1] = enemy_data[i][0];
            }
            for (int i = 0; i < 5; i++) {
                if (test_player[i] * test_enemy[i] == 0 || (my_data[i][3] < 1) || my_data[i][3] > 2 || enemy_data[i][3] < 1 || enemy_data[i][3] > 2)
                    throw new InvalidCountExeception("Invalid ship types or orientations were given");
            }
        }
        catch (Exception e){
            Alert Error = new Alert(Alert.AlertType.ERROR);
            Error.setTitle("Input File Error");
            Error.setHeaderText("Invalid input File");
            Error.setContentText(String.valueOf(e));
            Error.showAndWait();
            System.exit(-1);
        }
    }

    private Parent createContent() {
        HBox hbox = new HBox(80);
        VBox centerlBoard = new VBox(80);
        root.setPrefSize(500, 700);

        Image image = new Image("File:src\\sample\\battles.jpg");

        ImageView img = new ImageView(image);
        img.setFitWidth(100);
        img.setFitHeight(400);
        GridPane left = new GridPane();
        left.add(img,1,1);

        left.setPrefSize(100,200);
        left.setAlignment(Pos.CENTER);
        root.setLeft(left);
        Pane right = new Pane();
        right.setPrefSize(100,200);

        root.setLeft(left);
        root.setRight(right);

        enemyBoard = new Board(true, event -> {
            if (!init)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot)
                return;
        });

        playerBoard = new Board(false, event -> {

            if (!init)
                return;
            Cell cell = (Cell) event.getSource();
        });

        Font font_button = Font.font("Courier New", FontWeight.BOLD, 36);
        Font font_text = Font.font("Arial", FontWeight.BOLD, 16);
        VBox empty2 = new VBox();
        Button start_button = new Button("Battle");
        Pane create_space = new Pane();
        create_space.setPrefSize(40,50);
        start_button.setFont(font_button);
        start_button.setAlignment(Pos.CENTER);
        empty2.getChildren().addAll(create_space,start_button);
        empty2.setAlignment(Pos.BOTTOM_CENTER);

        Text playerSide = new Text("Player's Board");
        Text enemySide = new Text("Enemy's Board");
        playerSide.setFont(font_text);
        enemySide.setFont(font_text);
        VBox playerTitle = new VBox(20,playerSide,playerBoard);
        VBox enemyTitle = new VBox(20,enemySide,enemyBoard);
        playerTitle.setAlignment(Pos.CENTER);
        enemyTitle.setAlignment(Pos.CENTER);

        hbox.getChildren().addAll(playerTitle, enemyTitle);
        hbox.setAlignment(Pos.CENTER);

        centerlBoard.getChildren().addAll(empty2,hbox);
        centerlBoard.setAlignment(Pos.TOP_CENTER);

        HBox bottomBoard = new HBox();
        Text coordinates = new Text("Coordinates:");
        Text emp = new Text();
        coordinates.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label label1 = new Label("X: ");
        Label label2 = new Label("Y: ");
        TextField x_coord = new TextField();
        TextField y_coord = new TextField();
        x_coord.setPrefWidth(30);
        y_coord.setPrefWidth(30);
        HBox box = new HBox(5);
        box.setPadding(new Insets(15, 5 , 5, 40));
        Button fire_button = new Button("Fire");
        fire_button.setOnAction(event -> {
            boolean cont = true;
            int x=0, y=0;
            if (init && Player.rounds < 1)
                enemyMove();
            if (init && !enemyTurn && Player.rounds >= 1) {
                enemyTurn = !enemyTurn;
                try {
                    x = Integer.parseInt(x_coord.getText()) - 1;
                    y = Integer.parseInt(y_coord.getText()) - 1;
                    if (x < 0 || x > 9 || y < 0 || y > 9) {
                        Alert invalid = new Alert(Alert.AlertType.WARNING);
                        invalid.setTitle("Shot");
                        invalid.setHeaderText("Invalid Coordinates");
                        invalid.setContentText("The selected coordinates are outside the board!");
                        invalid.showAndWait();
                        cont = false;
                        enemyTurn = !enemyTurn;
                    }
                } catch (Exception e){
                    Alert wrong_inp = new Alert(Alert.AlertType.WARNING);
                    wrong_inp.setTitle("Shot");
                    wrong_inp.setHeaderText("Invalid Coordinates");
                    wrong_inp.setContentText("Coordinates should not be empty and must be a number!");
                    wrong_inp.showAndWait();
                    cont = false;
                    enemyTurn = !enemyTurn;
                }
                if(cont) {
                    Cell cell = enemyBoard.getCell(x, y);
                    if (cell.wasShot) {
                        enemyTurn = !enemyTurn;
                        Alert already_shot = new Alert(Alert.AlertType.INFORMATION);
                        already_shot.setTitle("Shot");
                        already_shot.setHeaderText("Repeated Coordinates");
                        already_shot.setContentText("This box has already been shot!");
                        already_shot.showAndWait();
                    } else {
                        hit = cell.shoot(Player);
                    }
                }
                Top = createTop();
                Top.setAlignment(Pos.CENTER);
                centerlBoard.getChildren().clear();
                centerlBoard.getChildren().addAll(Top, hbox);
                Top = enemyMove();
                Top.setAlignment(Pos.CENTER);
                centerlBoard.getChildren().clear();
                centerlBoard.getChildren().addAll(Top, hbox);
            }
            if (hit) {
                explosion_mp3.seek(Duration.ZERO);
                explosion_mp3.play();
            } else{
                splash_mp3.seek(Duration.ZERO);
                splash_mp3.play();
            }
            if ((Player.rounds < 1 && Enemy.rounds < 1) || Player.enemy_ships == 0 || Enemy.enemy_ships == 0){
                Text ann = new Text();
                Player.rounds = 0;
                Enemy.rounds = 0;
                if (Player.points >= Enemy.points)
                    ann.setText("YOU WIN FOR "+Integer.toString(Player.points - Enemy.points)+" POINTS");
                else
                    ann.setText("YOU LOST FOR "+Integer.toString(-Player.points + Enemy.points)+" POINTS");
                ann.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.ITALIC, 36));
                ann.setFill(Color.BLACK);
                VBox result = new VBox(40, ann);
                result.setPadding(new Insets(20,20,20,20));
                HBox fin = new HBox(result);
                Top = fin;
                Top.setAlignment(Pos.CENTER);
                centerlBoard.getChildren().clear();
                centerlBoard.getChildren().addAll(Top, hbox);
            }
        });
        fire_button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        box.getChildren().addAll(coordinates, label1, x_coord, label2, y_coord, fire_button, emp);
        box.setAlignment(Pos.CENTER);
        VBox empty = new VBox();
        VBox temp = new VBox(70,box,empty);
        HBox ret = new HBox(temp);

        bottomBoard = ret;
        bottomBoard.setAlignment(Pos.TOP_CENTER);

        root.setTop(menu());
        root.setCenter(centerlBoard);
        root.setBottom(bottomBoard);

        start_button.setOnAction( action ->{
            startGame(id);
            start_button.setVisible(false);
            enemyTurn = random.nextInt(10) > 4.5;
            first = enemyTurn;
            Top = createTop();
            if (enemyTurn) {
                Top = enemyMove();
            }
            Top.setAlignment(Pos.CENTER);
            centerlBoard.getChildren().clear();
            centerlBoard.getChildren().addAll(Top,hbox);
        });




        return root;
    }

    private HBox createTop() {
        Text empty = new Text();
        Text playerName = new Text("Player:");
        Text enemyName = new Text("Enemy:");
        Text ActiveShips = new Text("Active Ships");
        Text ships_player = new Text(Integer.toString(Enemy.enemy_ships));
        Text ships_enemy = new Text(Integer.toString(Player.enemy_ships));
        Text Points = new Text("Points");
        Text Player_points = new Text(Integer.toString(Player.points));
        Text Enemy_points = new Text(Integer.toString(Enemy.points));
        Text Rate = new Text("Firing Rate");
        float player_rate = 0, enemy_rate = 0;
        if (Player.rounds != 40)
            player_rate = (float) (1.0*Player.hits/(40- Player.rounds));
        if (Enemy.rounds != 40)
            enemy_rate = (float) (1.0*Enemy.hits/(40- Enemy.rounds));
        Text player_fire = new Text(Float.toString(player_rate));
        Text enemy_fire = new Text(Float.toString(enemy_rate));
        VBox row1 = new VBox(10,empty,playerName,enemyName);
        VBox row2 = new VBox(10,ActiveShips,ships_player,ships_enemy);
        VBox row3 = new VBox(10,Points,Player_points,Enemy_points);
        VBox row4 = new VBox(10,Rate,player_fire,enemy_fire);
        row1.setPadding(new Insets(25, 5 , 5, 50));
        row2.setPadding(new Insets(25, 5 , 5, 50));
        row3.setPadding(new Insets(25, 5 , 5, 50));
        row4.setPadding(new Insets(25, 5 , 5, 50));
        HBox connect = new HBox( row1, row2, row3, row4);
        return connect;
    }

    private MenuBar menu(){
        Menu application = new Menu("Application");
        MenuItem start = new MenuItem("Start");
        start.setOnAction(event ->{
            init = false;
            this.Player = new Attacker(true, enemyBoard);
            this.Enemy = new Attacker(false, playerBoard);
            createContent();
        });
        MenuItem load = new MenuItem("Load...");
        load.setOnAction(event -> {
            TextInputDialog scenario = new TextInputDialog("Scenario id:");
            scenario.showAndWait();
            id = scenario.getEditor().getText();
            init = false;
            this.Player = new Attacker(true, enemyBoard);
            this.Enemy = new Attacker(false, playerBoard);
            createContent();
        });
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e-> System.exit(0));
        application.getItems().addAll(start, load, new SeparatorMenuItem(), exit);

        Menu details = new Menu("Details");
        MenuItem enemy_ships = new MenuItem("Enemy Ships...");
        enemy_ships.setOnAction(event ->{
            if(init) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Enemy Information");
                alert.setHeaderText("Enemy's Ships state:");
                String print = "";
                for (int i = 0; i < 5; i++) {
                    print += Enemy.ship[i].name + ":  " + Enemy.ship[i].state + "\n";
                }
                alert.setContentText(print);
                alert.showAndWait();
            }
        });
        MenuItem player_shots = new MenuItem("Player shots...");
        player_shots.setOnAction(event -> {
            PopUp.History.history(Player);
        });

        MenuItem enemy_shots = new MenuItem("Enemy shots...");
        enemy_shots.setOnAction(event -> {
            PopUp.History.history(Enemy);
        });
        details.getItems().addAll(enemy_ships, new SeparatorMenuItem(), player_shots, enemy_shots);
        MenuBar ret = new MenuBar();
        ret.getMenus().addAll(application, details);
        return ret;
    }

    private HBox enemyMove() {
        while (enemyTurn && Enemy.rounds > 0) {
            Cell cell = choose_next();
            if(cell.wasShot)
                continue;
            hit = cell.shoot(Enemy) || hit;
            if(first) {
                if (hit) {
                    explosion_mp3.seek(Duration.ZERO);
                    explosion_mp3.play();
                } else {
                    splash_mp3.seek(Duration.ZERO);
                    splash_mp3.play();
                }
                first = false;
            };
            if (cell.ship != null)
                Enemy.previous = cell;
            enemyTurn = false;
            if (Player.rounds < 1)
                enemyTurn = true;
            Top = createTop();
        }
        if(Enemy.rounds <= 0)
            enemyTurn = false;
        return Top;
    }

    private Cell choose_next(){
        Cell cell = Enemy.previous, temp = null;
        int x, y;
        if (Enemy.previous == null){
             while (true){
                x = random.nextInt(10);
                y = random.nextInt(10);
                if (playerBoard.getCell(x, y).wasShot)
                    continue;
                return playerBoard.getCell(x, y);
            }
        }
        x = cell.x;
        y = cell.y;
        if (x < 9 && (direction == 0 || direction == -1)){
            temp = playerBoard.getCell(x+1,y);
            if (temp.wasShot){
                if (temp.ship != null){
                    Enemy.previous = temp;
                    direction = 0;
                    return choose_next();
                }
            } else {
                if (temp.ship != null)
                    direction = 0;
                return temp;
            }
        }if (y < 9 && (direction == 1 || direction == -1)){

            temp = playerBoard.getCell(x,y+1);
            if (temp.wasShot){
                if (temp.ship != null){
                    Enemy.previous = temp;
                    direction = 1;
                    return choose_next();
                }
            } else {
                if (temp.ship != null)
                    direction = 1;
                return temp;
            }
        }if (x > 0 && (direction == 2 || direction == 0 || direction == -1)){

            temp = playerBoard.getCell(x-1,y);
            if (temp.wasShot){
                if (temp.ship != null){
                    Enemy.previous = temp;
                    direction = 2;
                    return choose_next();
                }
            } else {
                if (temp.ship != null)
                    direction = 2;
                return temp;
            }
        }if (y > 0 && (direction == 3 || direction == 1 || direction == -1)){

            temp = playerBoard.getCell(x,y-1);
            if (temp.wasShot){
                if (temp.ship != null){
                    Enemy.previous = temp;
                    direction = 3;
                    return choose_next();
                }
            } else {
                if (temp.ship != null)
                    direction = 3;
                return temp;
            }
        }
        x = random.nextInt(10);
        y = random.nextInt(10);
        direction = -1;
        Enemy.previous = null;
        return playerBoard.getCell(x, y);
    }

    private void startGame(String id) {
        File mine = new File("src\\sample\\medialab\\player_"+id+".txt");
        File enemy = new File("src\\sample\\medialab\\enemy_"+id+".txt");
        // place ships
        try {
            read_file(mine, enemy);
        } catch (IOException e) {
            Alert Error = new Alert(Alert.AlertType.ERROR);
            Error.setTitle("File Error");
            Error.setHeaderText("File not found");
            Error.setContentText(String.valueOf(e));
            Error.showAndWait();
            System.exit(1);
        }
        for(int i = 0; i < 5; i++) {
            int type = my_data[i][0];
            int orientation = my_data[i][3]-1;
            int y = my_data[i][1];
            int x = my_data[i][2];
            Ship ship = new Ship(type, orientation==1, true);
            Player.ship[i] = ship;
            playerBoard.placeShip(ship, x, y);
        }

        for(int i = 0; i < 5; i++) {
            int type = enemy_data[i][0];
            int orientation = enemy_data[i][3]-1;
            int y = enemy_data[i][1];
            int x = enemy_data[i][2];
            Ship ship = new Ship(type, orientation==1, false);
            Enemy.ship[i] = ship;
            enemyBoard.placeShip(ship, x, y);
        }

        init = true;
    }

    public void endGame(boolean winner){
        /* To be implemented
        maybe a new window that gives you the change to choose game
         */
    }
    public Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = createContent();
        root.setId("pane");
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(this.getClass().getResource("Sea.css").toExternalForm());
        primaryStage.setTitle("MediaLab Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        mp3.setVolume(0.6);
        mp3.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mp3.seek(Duration.ZERO);
            }
        });
        mp3.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
