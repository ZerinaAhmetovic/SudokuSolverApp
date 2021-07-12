package com.example.sudoku;

import java.util.ArrayList;

public class Solver {
    int[][] board;
    ArrayList<ArrayList<Object>> emptyBoxIndex;
    //not private
    private int selectedRow;
    private int selectedColumn;

    Solver() {
        selectedRow = -1;
        selectedColumn = -1;
        board = new int[9][9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                board[r][c] = 0;
            }
        }
        emptyBoxIndex = new ArrayList<>();
    }

    private void getEmptyBoxIndexes() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (this.board[r][c] == 0) {
                    this.emptyBoxIndex.add(new ArrayList<>());
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size() - 1).add(r);
                    this.emptyBoxIndex.get(this.emptyBoxIndex.size() - 1).add(c);
                }
            }
        }
    }

    private boolean check(int row, int col) {
        //to check if user entered variables are correct (empty cells def.value is 0)
        if (this.board[row][col] > 0) { //hor. and vert. check
            for (int i = 0; i < 9; i++) {
                if (this.board[row][col] == this.board[i][col] && row != i) {
                    return false;
                }

                if (this.board[row][col] == this.board[row][i] && col != i) {
                    return false;
                }
            }
            //box 3x3 check
            int rowBox = row / 3;
            int colBox = col / 3;

            for (int r = rowBox * 3; r < rowBox * 3 + 3; r++) {
                for (int c = colBox * 3; c < colBox * 3 + 3; c++) {
                    if (this.board[r][c] == this.board[row][col] && row != r && col != c)
                        return false;
                }
            }
        }
        return true;
    }

    public boolean solve(SudokuBoard display) {
        int row = -1;
        int col = -1;

        //check for empty cell - get its col and row
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (this.board[r][c] == 0) {
                    row = r;
                    col = c;
                    break;
                }
            }
        }

        //if row -1 ---no empty cells its solved
        if (row == -1 || col == -1) {
            return true;
        }

        //start signing numbers 1-9 to row and col coordinate cell
        for (int i = 1; i < 10; i++) {
            this.board[row][col] = i;
            display.invalidate();

            //check if valid number, then check if solved otherwise set the number back to zero
            //ako nije legal i value, ide sljedeća u for loopu, 2,3,4...dok ne nadje odgovarajuću za check method
            if (check(row, col)) {
                /*ako je valid, ponovo se poziva metoda i rjesava tj prelazi se na sljedeću empty cell,itd tako prelazi
                sve dok ne dodje do cell gdje je nemoguće postaviti ikakvu vrijednost jer je svaka illegal,
                onda se vraća polje unazad.... Vraća se na prethodnu cell gdje je odgovaralo npr 6, ali onda
                vraća vrijednost na nulu pa u for loopu dodjeljuje sljedeću vrijednost pa se ona ponovo provjerava
                da li je validna itd itd sve dok ne rijesi
                 */

                if (solve(display))
                    return true;
            }
            this.board[row][col] = 0;
        }

        return false;
    }

    public void resetBoard(){
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                board[r][c] = 0;
            }
        }
        this.emptyBoxIndex = new ArrayList<>();
    }

    public void setNumberPos(int num) {
        if (this.selectedRow != -1 && this.selectedColumn != -1) {
            if (this.board[this.selectedRow - 1][this.selectedColumn - 1] == num) {
                this.board[this.selectedRow - 1][this.selectedColumn - 1] = 0;
            } else {
                this.board[this.selectedRow - 1][this.selectedColumn - 1] = num;
            }
        }
    }

    public int[][] getBoard() {
        return this.board;
    }

    public ArrayList<ArrayList<Object>> getEmptyBoxIndex() {
        return this.emptyBoxIndex;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int r) {
        selectedRow = r;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int c) {
        selectedColumn = c;
    }
}
