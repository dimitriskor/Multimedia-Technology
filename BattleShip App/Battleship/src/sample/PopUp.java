package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.scene.*;
import sample.Board.Cell;

public class PopUp {

    public static class History{

        public static void history(Attacker player){
            Stage window = new Stage();
            window.setTitle(player.name+"'s History");
            VBox box = new VBox();
            int repeat = 5;
            if(player.rounds >= 36)
                repeat = 40 - player.rounds;
            for(int i=0; i<repeat; i++) {
                String temp;
                if (player.history[i].ship != null) {
                    temp = "Move " + Integer.toString(40 - player.rounds - i) + " :   Coordination (x,y) = (" + Integer.toString(player.history[i].x + 1)
                            + "," + Integer.toString(player.history[i].y + 1) + ")  Target:  Succesfull -> " + player.history[i].ship.name;
                }
                else
                    temp = "Move " + Integer.toString(40 - player.rounds - i) + " :   Coordination (x,y) = (" + Integer.toString(player.history[i].x + 1)
                            + "," + Integer.toString(player.history[i].y + 1) + ")  Target:  Missed!";
                Label info = new Label(temp);
                info.setAlignment(Pos.BASELINE_LEFT);
                box.getChildren().add(info);
            }
            Button Go = new Button("OK");
            Go.setOnAction(event -> {
                window.close();
            });
            Go.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            box.setPadding(new Insets(25, 25, 25, 25));
            box.setAlignment(Pos.CENTER);
            VBox mid = new VBox(15, box, Go);
            VBox box2 = new VBox();
            box2.getChildren().add(mid);
            box2.setPadding(new Insets(10,10,10,10));
            Scene scene= new Scene(box2);
            window.setScene(scene);
            window.show();
        }

    }
}
