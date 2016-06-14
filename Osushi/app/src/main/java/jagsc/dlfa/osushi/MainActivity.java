package jagsc.dlfa.osushi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private enum Turn {
        NONE,
        WHITE,
        BLACK,
    }

    private JSONObject json;
    private JSONArray log;
    private Cell[][] brd;
    private Turn trn = Turn.BLACK;
    private int white = 2;
    private int black = 2;
    private final int[][] list = {
            {-1, 0, 0, -1}, // left
            {-1, -1, 0, 0}, // left up
            {0, -1, -1, 0}, // up
            {1, -1, 7, 0}, // right up
            {1, 0, 7, -1}, // right
            {1, 1, 7, 7}, // right down
            {0, 1, -1, 7}, // down
            {-1, 1, 0, 7}, // left down
    };

    private static class Cell extends TextView {
        private int j, i;
        private Turn t;

        public Cell(Context ctx, int j, int i) {
            super(ctx);
            this.i = i;
            this.j = j;
            this.t = Turn.NONE;
            this.setBackgroundColor(Color.GREEN);
        }

        public int getXInBoard() {
            return j;
        }

        public int getYInBoard() {
            return i;
        }

        public Turn getType() {
            return t;
        }

        public void setType(Turn t) {
            this.t = t;
            switch (this.t) {
                case NONE:
                    this.setBackgroundColor(Color.GREEN);
                    break;
                case WHITE:
                    this.setBackgroundColor(Color.WHITE);
                    break;
                case BLACK:
                    this.setBackgroundColor(Color.BLACK);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        json = new JSONObject();
        log = null;
        try {
            json.put("version", "1.0");
            json.put("data", new JSONArray());
            log = json.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GridLayout board = (GridLayout) findViewById(R.id.board);
        brd = new Cell[8][8];


        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                brd[j][i] = new Cell(this, j, i);
                if ((i == 3 && j == 3) || (i == 4 && j == 4)) {
                    brd[j][i].setType(Turn.WHITE);
                } else if ((i == 3 && j == 4) || (i == 4 && j == 3)) {
                    brd[j][i].setType(Turn.BLACK);
                }
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.columnSpec = GridLayout.spec(j, GridLayout.FILL, 1);
                params.rowSpec = GridLayout.spec(i, GridLayout.FILL, 1);
                params.setMargins(1, 1, 1, 1);
                brd[j][i].setLayoutParams(params);

                brd[j][i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cell c = (Cell) v;
                        int x = c.getXInBoard();
                        int y = c.getYInBoard();
                        if (brd[x][y].getType() == Turn.NONE) {
                            if (updateBoard(x, y)) {
                                if (white + black >= 64) {
                                    // 全マスおいたのでゲーム終了
                                    game_end();
                                } else {
                                    change_turn();
                                    if (!checkBoard()) {
                                        // どこにもおけないよ
                                        // メッセージだしてターン変えるよ
                                        change_turn();
                                        if (!checkBoard()) {
                                            // おたがいに置けないとかいうレアな状況
                                            // ゲーム終了
                                            game_end();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

                board.addView(brd[j][i]);
            }
        }

        TextView w = (TextView) findViewById(R.id.white);
        w.setText("2");
        TextView b = (TextView) findViewById(R.id.black);
        b.setText("2");
    }

    private void change_turn() {
        trn = trn == Turn.WHITE ? Turn.BLACK : Turn.WHITE;
    }

    private boolean updateBoardImpl(int px, int py, final int sx, final int sy, final int cx, final int cy) {
        final int ix = px;
        final int iy = py;
        int c = 0;
        while (px != cx && py != cy) {
            if (brd[px + sx][py + sy].getType() == Turn.NONE) {
                return false;
            } else if (brd[px + sx][py + sy].getType() == trn) {
                if (c == 0) {
                    return false;
                }
                while (px != ix || py != iy) {
                    brd[px][py].setType(trn);

                    if (px > ix) {
                        px -= 1;
                    } else if (px < ix) {
                        px += 1;
                    }
                    if (py > iy) {
                        py -= 1;
                    } else if (py < iy) {
                        py += 1;
                    }
                }
                if (trn == Turn.WHITE) {
                    white += c;
                    black -= c;
                } else {
                    black += c;
                    white -= c;
                }
                return true;
            } else {
                px += sx;
                py += sy;
                ++c;
            }
        }
        return false;
    }

    private boolean updateBoard(int x, int y) {
        boolean t = false;
        for (int[] i : list) {
            t |= updateBoardImpl(x, y, i[0], i[1], i[2], i[3]);
        }
        if (t) {
            if (trn == Turn.WHITE) {
                white += 1;
            } else {
                black += 1;
            }
            brd[x][y].setType(trn);
            TextView b = (TextView) findViewById(R.id.black);
            b.setText("" + black);
            TextView w = (TextView) findViewById(R.id.white);
            w.setText("" + white);

            JSONArray tmp = new JSONArray();
            tmp.put(trn == Turn.WHITE ? 1 : 2);
            tmp.put(x);
            tmp.put(y);
            log.put(tmp);
        }
        return t;
    }

    private boolean checkBoardImpl(int px, int py, final int sx, final int sy, final int cx, final int cy) {
        if (brd[px][py].getType() != Turn.NONE) {
            return false;
        }
        int c = 0;
        while (px != cx && py != cy) {
            if (brd[px + sx][py + sy].getType() == Turn.NONE) {
                return false;
            } else if (brd[px + sx][py + sy].getType() == trn) {
                if (c == 0) {
                    return false;
                }
                return true;
            } else {
                px += sx;
                py += sy;
                ++c;
            }
        }
        return false;
    }

    private boolean checkBoard() {
        boolean t;
        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                for (int[] i : list) {
                    t = checkBoardImpl(x, y, i[0], i[1], i[2], i[3]);
                    if (t) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void game_end() {
        new HttpResponsAsync().execute(json.toString());
        Snackbar.make(
                findViewById(R.id.main_root),
                "ゲーム終了！" + (white == black ? "引き分け" : (white > black ? "白の勝ち" : "黒の勝ち")),
                Snackbar.LENGTH_LONG)
                .show();
    }
}
