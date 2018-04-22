package com.example.dell.caro2;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout chessBoardLayout;
    Button buttonNewGame;
    final int rowBoard = 30;
    final int colBoard = 30;
    final int winTotal = 5;
    static Cell[][] arrayCell ;// = new Cell[rowBoard][colBoard];
    static int[][] cellBoard; //= new int[rowBoard][colBoard];
    Cell preCell;

    ArrayList<Cell> playerCell;
    ArrayList<Cell> machinceCell;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayCell = new Cell[rowBoard][colBoard];
        cellBoard = new int[rowBoard][colBoard];

        playerCell = new ArrayList<Cell>();
        machinceCell = new ArrayList<Cell>();

        for(int i=0;i<rowBoard;i++)
            for(int j=0;j<colBoard;j++){
              //  Cell c = new Cell(MainActivity.this,i,j,0);
              //  arrayCell[i][j] = c;
                cellBoard[i][j] = 0;
            }

        chessBoardLayout = (LinearLayout) findViewById(R.id.chess_board_layout);
        buttonNewGame = (Button) findViewById(R.id.button_new_game);

        for(int j=0;j<rowBoard;j++) {
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);
            
            for (int i = 0; i < colBoard; i++) {
                Cell c = new Cell(this,j,i,0);
                c.setBackground(this.getResources().getDrawable(R.drawable.cell_effect));
                c.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                c.setTextColor(Color.RED);

                c.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Cell thisCell = (Cell) view;
                        if(preCell != null)
                            preCell.setTextColor(Color.BLUE);
                        thisCell.setText("X");
                       // thisCell.player = 1;

                        arrayCell[thisCell.rowCell][thisCell.columnCell].player = 1;
                        cellBoard[thisCell.rowCell][thisCell.columnCell] = 1;

                        if(checkWinner(arrayCell[thisCell.rowCell][thisCell.columnCell])){
                            buttonNewGame.setText("Chien Thang");
                        } else {

                            Heuristic task = new Heuristic();
                            task.execute();
                            playerCell.add(new Cell(MainActivity.this, thisCell.rowCell, thisCell.columnCell, 1));
                            buttonNewGame.setText("OK");
                        }
                    }
                });
                arrayCell[j][i] = c;
                l.addView(arrayCell[j][i]);
            }
            chessBoardLayout.addView(l);
        }

      //  Heuristic task = new Heuristic();
      //  task.execute();

        
    }


    private class Heuristic extends AsyncTask<Void, Void, Cell> {


        protected Cell doInBackground(Void... voids) {
            int maxScore = -1000;
            int score;
            int defenseScore;
            int attackScore;
            int resultScore;
            Cell resultCell = arrayCell[0][0];
            for(int i=0;i<rowBoard;i++)
                for(int j=0;j<colBoard;j++){
                    if(arrayCell[i][j].player == 0) {
                        score = evaluateScore(arrayCell[i][j]);
                        defenseScore = score + extraScore(i,j,-1,1,1,winTotal-1) + extraScore(i,j,-1,1,0,winTotal-1) + extraScore(i,j,-1,1,-1,winTotal-1) + extraScore(i,j,-1,0,1,winTotal-1);
                        attackScore = score + extraScore(i,j,-1,1,1,1) + extraScore(i,j,-1,1,0,1) + extraScore(i,j,-1,1,-1,1) + extraScore(i,j,-1,0,1,1);
                      //  arrayCell[i][j].scoreCell = defenseScore >= attackScore ? defenseScore : attackScore;
                        resultScore = defenseScore >= attackScore ? defenseScore : attackScore;
                        if (resultScore >= maxScore) {
                            resultCell = arrayCell[i][j];
                            maxScore = resultScore;
                        }
                    }
                }
            resultCell.player = maxScore;
            return resultCell;
        }

        protected void onPostExecute(Cell c){

            arrayCell[c.rowCell][c.columnCell].player = -1;
            cellBoard[c.rowCell][c.columnCell] = -1;

            machinceCell.add(arrayCell[c.rowCell][c.columnCell]);
            arrayCell[c.rowCell][c.columnCell].setText("O");
            arrayCell[c.rowCell][c.columnCell].setTextColor(Color.GREEN);
            preCell = arrayCell[c.rowCell][c.columnCell];
            buttonNewGame.setText(c.player+" Ok");
            if(checkWinner(arrayCell[c.rowCell][c.columnCell]))
                buttonNewGame.setText("May Thang");
            /*
            for(int i=0;i<playerCell.size();i++)
                Log.d("Player",playerCell.get(i).rowCell+" - "+playerCell.get(i).columnCell);
            for(int i=0;i<machinceCell.size();i++)
                Log.d("Machine",machinceCell.get(i).rowCell+" - "+machinceCell.get(i).columnCell);
                */
        }


    }

    public int evaluateScore(Cell cell){
        int sumScore = 0;
        int minScore = 100;
        cellBoard[cell.rowCell][cell.columnCell] = -1;
        for(int i=0;i<rowBoard;i++)
            for(int j=0;j<colBoard;j++){
                if(cellBoard[i][j] == 0){
                    cellBoard[i][j] = 1;
                    sumScore = calculateScore(cell.rowCell,cell.columnCell,1,1,1) + calculateScore(cell.rowCell,cell.columnCell,1,1,0)
                        + calculateScore(cell.rowCell,cell.columnCell,1,1,-1) + calculateScore(cell.rowCell,cell.columnCell,1,0,1)
                        - calculateScore(i,j,-1,1,1) - calculateScore(i,j,-1,1,0) - calculateScore(i,j,-1,1,-1) - calculateScore(i,j,-1,0,1);



                        for(int p=0;p<machinceCell.size();p++){
                            Cell c = machinceCell.get(p);
                            sumScore += calculateScore(c.rowCell,c.columnCell,1,1,1) + calculateScore(c.rowCell,c.columnCell,1,1,0)
                                    + calculateScore(c.rowCell,c.columnCell,1,1,-1) + calculateScore(c.rowCell,c.columnCell,1,0,1);
                        }

                        for(int p=0;p<playerCell.size();p++){
                            Cell c = playerCell.get(p);
                            sumScore = sumScore - calculateScore(c.rowCell,c.columnCell,-1,1,1) - calculateScore(c.rowCell,c.columnCell,-1,1,0)
                                    - calculateScore(c.rowCell,c.columnCell,-1,1,-1) - calculateScore(c.rowCell,c.columnCell,-1,0,1);
                        }


                    if(sumScore <= minScore)
                        minScore = sumScore;
                    cellBoard[i][j] = 0;
                  //  arrayCell[i][j].setText(sumScore+"k");
                }
            }

        cellBoard[cell.rowCell][cell.columnCell] = 0;
        return minScore;
    }



    public int calculateScore(int rowTarget, int colTarget, int prohibit, int indexRowTarget,int indexColTarget){

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
        while ((cellBoard[indexRow][indexCol] != prohibit) && (score < winTotal)){
            score += 1;
            indexRow = indexRow + indexRowTarget;
            indexCol = indexCol + indexColTarget;
            if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                break;
        }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
        while ((cellBoard[indexRow][indexCol] != prohibit ) && (score < 2*winTotal)){
            score +=1;
            indexRow -=  indexRowTarget;
            indexCol -=  indexColTarget;
            if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                break;
        }

        int result = 0;
        if(score>=winTotal && score<2*winTotal)
            result = 1;
        else if (score==winTotal*2)
            result = 2;

        return result;

     //   return score;
    }


    public int extraScore(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget,int kWin) {

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 0;
        int numDefense = kWin;
        int numAttack = winTotal - kWin;
        int countDefense = 0;
        int countAttack = 1;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
        while((cellBoard[indexRow][indexCol] == player)||(cellBoard[indexRow][indexCol] == - player)){
            if(cellBoard[indexRow][indexCol] == -player){
                score += numDefense;
                countDefense ++;

            }
            else {
                score += numAttack;
                countAttack ++;
            }

            if(countDefense == winTotal - 2 || countAttack == winTotal -2)
                score += 2000;
            if(countDefense == winTotal -1 || countAttack == winTotal -1)
                score += 4000;

            if(countDefense == winTotal -2)
                score += numDefense;
            if(countDefense == winTotal -1)
                score += 2*numDefense;

            if(countAttack == winTotal)
                score += 10000;

            if(cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget] == -cellBoard[indexRow][indexCol]){
                if(countDefense == winTotal - 2 || countAttack == winTotal -2)
                    score -= 2000;
                break;
            }
            else {
                indexRow = indexRow + indexRowTarget;
                indexCol = indexCol + indexColTarget;
            }

            if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                break;
        }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;


        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while ((cellBoard[indexRow][indexCol] == player)||(cellBoard[indexRow][indexCol] == -player)){
                if(cellBoard[indexRow][indexCol] == -player){
                    score += numDefense;
                    countDefense ++;
                }
                else{
                    score += numAttack;
                    countAttack ++;
                }

                if(countDefense == winTotal -2 || countAttack == winTotal - 2 )
                    score += 2000;
                if(countDefense == winTotal -1 || countAttack == winTotal -1 )
                    score += 4000;

                if(countDefense == winTotal -2)
                    score += numDefense;
                if(countDefense == winTotal -1)
                    score += 2*numDefense;

                if(countAttack == winTotal)
                    score += 10000;

                if(cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget] == -cellBoard[indexRow][indexCol]){
                    if(countDefense == winTotal -2 || countAttack == winTotal - 2)
                        score -= 2000;
                    break;
                }
                else {
                    indexRow -= indexRowTarget;
                    indexCol -= indexColTarget;
                }
                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        return score;
    }

    public boolean checkWinner(Cell c){

        int[] scoreDirect;
        scoreDirect = new int[4];
        boolean result = false;
        scoreDirect[0] = getScore(c.rowCell,c.columnCell,c.player,1,1);
        scoreDirect[1] = getScore(c.rowCell,c.columnCell,c.player,1,0);
        scoreDirect[2] = getScore(c.rowCell,c.columnCell,c.player,1,-1);
        scoreDirect[3] = getScore(c.rowCell,c.columnCell,c.player,0,1);
        for(int i=0;i<4;i++)
            if(scoreDirect[i]>=winTotal){
                result = true;
                break;
            }

        return  result;
    }

    public int getScore(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget){

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while(cellBoard[indexRow][indexCol] == player){
                score++;
                indexRow = indexRow + indexRowTarget;
                indexCol = indexCol + indexColTarget;

                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;


        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while (cellBoard[indexRow][indexCol] == player){
                score += 1;
                indexRow -=  indexRowTarget;
                indexCol -=  indexColTarget;

                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        return score;



    }




















/*
    private  class Evaluation extends AsyncTask<Cell, Void, Cell> {

        protected void onPreExecute(){
            super.onPreExecute();
        }


        protected Cell doInBackground(Cell... cells) {
            Cell c = cells[0];
            Score[] score = new Score[4];
            score[0] = evaluateScore(c.rowCell, c.columnCell, 1, 1);
            score[1] = evaluateScore(c.rowCell, c.columnCell, 1, 0);
            score[2] = evaluateScore(c.rowCell, c.columnCell, 1, -1);
            score[3] = evaluateScore(c.rowCell, c.columnCell, 0, 1);

            Cell cellResult;
            int maxScore = score[0].scoreDirect;
            int jMax = 0;
            for (int i = 0; i < score.length; i++) {
                if (score[i].scoreDirect >= 5) {
                    break;
                } else
                    if(score[i].scoreDirect >= maxScore){
                        if(score[i].scoreDirect == maxScore){
                            if((arrayCell[score[jMax].cellStart.rowCell][score[jMax].cellStart.columnCell].player == 2)||(arrayCell[score[jMax].cellEnd.rowCell][score[jMax].cellEnd.columnCell].player == 2)) {
                                maxScore = score[i].scoreDirect;
                                jMax = i;
                            }
                        } else {
                            maxScore = score[i].scoreDirect;
                            jMax = i;
                        }
                }
            }

            if(arrayCell[score[jMax].cellStart.rowCell][score[jMax].cellStart.columnCell].player == 0)
                cellResult = score[jMax].cellStart;
            else
                cellResult = score[jMax].cellEnd;

            return cellResult;
        }

        protected  void  onPostExecute(Cell result){
            super.onPostExecute(result);
           /* if(result=="Chiến Thắng")
                arrayCell[0][0].setText("O");
            Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();

            buttonNewGame.setText(result);
            if(arrayCell[result.rowCell][result.columnCell].player != 2){
                arrayCell[result.rowCell][result.columnCell].player = 2;
            }
            arrayCell[result.rowCell][result.columnCell].setText("O");
        }


    }

    public Score evaluateScore2(int rowTarget,int colTarget, int indexRowTarget, int indexColTarget) {
        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
       // int score = 1;
        Score score = new Score(1);
        while (arrayCell[indexRow][indexCol].player == 1){
          //  score += 1;
            score.scoreDirect += 1;
        //  score.indexEnd += 1;
            indexRow = indexRow + indexRowTarget;
            indexCol = indexCol + indexColTarget;
        }

        score.cellEnd = new Cell(this,indexRow,indexCol,2);

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;
        while (arrayCell[indexRow][indexCol].player == 1){
           // score +=1;
            score.scoreDirect += 1;
         //   score.indexStart += 1;
            indexRow -=  indexRowTarget;
            indexCol -=  indexColTarget;
        }

        score.cellStart = new Cell(this,indexRow,indexCol,2);

        return score;
    }

*/


}
