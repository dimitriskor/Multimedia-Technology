package sample;

import sample.Board.Cell;

public class Attacker {
    public static String[] Names = {"Enemy", "Player"};

    public boolean player;
    public String name = Names[1];
    public Cell previous = null;
    public int enemy_ships = 5;
    public int points = 0;
    public int rounds = 40;
    public boolean turn;
    public int hits = 0;
    public Board other_board;
    public Ship [] ship = new Ship[5];
    public Cell [] history = new Cell[5];

    Attacker(boolean player, Board other_board){
        this.player = player;
        if (!player)
            this.name = Names[0];
        this.other_board = other_board;
    }

    public boolean Move(boolean turn, int x, int y, Attacker defender){
        Cell cell = other_board.getCell(x, y);
        if (cell.wasShot)
            return turn;

        if (!cell.shoot(this))
            turn = !turn;
        return turn;

    }

    public void update_history(Cell cell){
        if(rounds > 35){
            int last = 40 - rounds - 1;
            for(int i = last; i > 0; i--)
                history[i] = history[i-1];
            history[0] = cell;
        }
        else {
            for (int i = 4; i > 0; i--)
                history[i] = history[i-1];
            history[0] = cell;
        }
    }
}
