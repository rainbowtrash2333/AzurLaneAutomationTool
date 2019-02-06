package unit;

import java.util.Arrays;

/**
 * @author Twikura
 * @create 2019/2/2 14:37
 */
public class TwoDArray {
    private int row, col;
    private int[] array;

    public TwoDArray (int row, int col) {
        col++;
        row++;
        this.col = col;
        this.row = row;
        array = new int[row * col];
    }
    public TwoDArray(int row,int col,int[]array){
        this.array = Arrays.copyOf(array,array.length);
        this.col = col;
        this.row = row;
    }

    public int get (int row, int col) {

        return array[this.col * row + col];
    }

    public void setArray (int[] array) {
        this.array = Arrays.copyOf(array,array.length);
    }

    public void set (int row, int col, int e) {
        array[this.col * row + col] = e;
    }

    @Override
    public String toString () {
        StringBuilder s = new StringBuilder("[ \n");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                s.append(get(i, j)).append(" ");
            }
            s.append("\n");
        }
        s.append("]");
        return s.toString();
    }

    public int getRow () {
        return row;
    }

    public int getCol () {
        return col;
    }

}
