package com.example.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SudokuBoard extends View {
    private final int boardColor;
    private final int cellFillColor;
    private final int cellHighlightColor;
    private final int letterColor;
    private final int letterColorSolve;

    private final Paint cellFillColorPaint = new Paint();
    private final Paint cellsHighlightColorPaint = new Paint();
    private final Paint boardColorPaint = new Paint();

    private Paint letterPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();

    private final Solver solver = new Solver();

    private int cellSize;

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,
                0, 0);

        try {
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor, 0);
            cellFillColor = a.getInteger(R.styleable.SudokuBoard_cellFillColor, 0);
            cellHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellHighlightColor, 0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterColorSolve = a.getInteger(R.styleable.SudokuBoard_letterColorSolve, 0);
        } finally {
            a.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = dimension / 9;
        setMeasuredDimension(dimension, dimension);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isValid;

        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {

            solver.setSelectedRow((int) Math.ceil(y / cellSize));
            solver.setSelectedColumn((int) Math.ceil(x / cellSize));

            isValid = true;
        } else {
            isValid = false;
        }

        return isValid;
    }


    private void cellColor(Canvas canvas, int r, int c) {
        if (solver.getSelectedRow() != -1 && solver.getSelectedColumn() != -1) {
            //horizontal
            canvas.drawRect(0, (r - 1) * cellSize,
                    9 * cellSize, r * cellSize, cellsHighlightColorPaint);
            //vertical
            canvas.drawRect((c - 1) * cellSize, 0,
                    c * cellSize, 9 * cellSize, cellsHighlightColorPaint);
            //specific square user clicked
            canvas.drawRect((c - 1) * cellSize, (r - 1) * cellSize,
                    c * cellSize, r * cellSize, cellsHighlightColorPaint);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        cellFillColorPaint.setStyle(Paint.Style.FILL);
        cellFillColorPaint.setColor(cellFillColor);
        cellFillColorPaint.setAntiAlias(true);

        cellsHighlightColorPaint.setStyle(Paint.Style.FILL);
        cellsHighlightColorPaint.setColor(cellHighlightColor);
        cellsHighlightColorPaint.setAntiAlias(true);

        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setColor(letterColor);
        letterPaint.setAntiAlias(true);


        cellColor(canvas, solver.getSelectedRow(), solver.getSelectedColumn());
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), boardColorPaint);
        drawBoard(canvas);
        drawNumber(canvas);
    }

    private void drawNumber(Canvas canvas) {

        letterPaint.setTextSize(cellSize);
        //user placement
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (solver.getBoard()[r][c] != 0) {
                    String text = Integer.toString(solver.getBoard()[r][c]);
                    //dim of cellTxt
                    float height, width;
                    //here rect gets dimensions - before it was empty
                    letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
                    width = letterPaint.measureText(text);
                    height = letterPaintBounds.height();

                    //TODO (r*cellSize)+(cellSize-height)/2
                    canvas.drawText(text, (c * cellSize) + ((cellSize - width) / 2),
                            (r * cellSize + cellSize) - ((cellSize - height) / 2), letterPaint);
                }
            }
        }
//comp
        letterPaint.setColor(letterColorSolve);
        for (ArrayList<Object> letter : solver.getEmptyBoxIndex()) {
            int r = (int) letter.get(0);
            int c = (int) letter.get(1);

            String text = Integer.toString(solver.getBoard()[r][c]);
            //dim of cellTxt
            float height, width;
            //here rect gets dimensions - before it was empty
            letterPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
            width = letterPaint.measureText(text);
            height = letterPaintBounds.height();

            //TODO (r*cellSize)+(cellSize-height)/2
            letterPaint.setColor(letterColorSolve);
            canvas.drawText(text, (c * cellSize) + (cellSize - width) / 2,
                    (r * cellSize + cellSize) - (cellSize - height) / 2, letterPaint);
        }
    }

    private void drawThickBoard() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);
    }

    private void drawThinBoard() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);
    }

    private void drawBoard(Canvas canvas) {
        for (int r = 0; r < 9; r++) {
            if ((r + 1) % 3 == 0)
                drawThickBoard();
            else
                drawThinBoard();

            canvas.drawLine(0, cellSize * (r + 1),
                    9 * cellSize, cellSize * (r + 1), boardColorPaint);
        }
        for (int c = 0; c < 9; c++) {
            if ((c + 1) % 3 == 0)
                drawThickBoard();
            else
                drawThinBoard();

            canvas.drawLine((c + 1) * cellSize, 0,
                    (c + 1) * cellSize, 9 * cellSize, boardColorPaint);
        }
    }

    public Solver getSolver() {
        return this.solver;
    }
}
