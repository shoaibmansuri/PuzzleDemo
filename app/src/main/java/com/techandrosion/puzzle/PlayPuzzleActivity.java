package com.techandrosion.puzzle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.rp.puzzle.bean.Block;
import com.rp.puzzle.util.BitmapUtil;
import com.rp.puzzle.view.PuzzleView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlayPuzzleActivity extends AppCompatActivity {

    private PuzzleView mPuzzleView;
    private int puzzleSize;
    private String puzzleImage;
    private Block[][] mBlocksState;
    private Bitmap mBitmap;


    public static void start(Context context, String data) {
        Intent starter = new Intent(context, PlayPuzzleActivity.class);
        starter.putExtra("data", data);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_puzzle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mPuzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        mPuzzleView.setOnCompleteListener(new PuzzleView.OnCompleteListener() {
            @Override
            public void onComplete() {
                Toast.makeText(PlayPuzzleActivity.this, "Congratulations!!!", Toast.LENGTH_SHORT).show();
            }
        });
        fetchData();
        mBitmap = BitmapFactory.decodeFile(puzzleImage);
        try {
            mPuzzleView.playPuzzle(mBitmap, mBlocksState);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchData() {
        try {
            JSONObject mJsonObject = new JSONObject(getIntent().getExtras().getString("data"));
            puzzleImage = mJsonObject.getString("puzzle_image");
            puzzleSize = mJsonObject.getInt("puzzle_size");
            mBlocksState = new Block[puzzleSize][puzzleSize];
            JSONArray mJsonArray = mJsonObject.getJSONArray("positionArray");
            int index = 0;
            for (int i = 0; i < puzzleSize; i++) {
                for (int j = 0; j < puzzleSize; j++) {
                    JSONObject blockJson = mJsonArray.getJSONObject(index);
                    Block mBlock = new Block(blockJson.getInt("realX"),
                            blockJson.getInt("realY"),
                            blockJson.getInt("currentX"),
                            blockJson.getInt("currentY"));
                    mBlocksState[i][j] = mBlock;
                    index++;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);

    }
}
