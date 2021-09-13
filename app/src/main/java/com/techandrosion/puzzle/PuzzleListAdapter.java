package com.techandrosion.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rp.puzzle.bean.Block;
import com.rp.puzzle.view.PuzzleView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mobiweb on 5/6/18.
 */

public class PuzzleListAdapter extends RecyclerView.Adapter<PuzzleListAdapter.PuzzleListViewHolder> {

    private HomeActivity mHomeActivity;

    public PuzzleListAdapter(HomeActivity mHomeActivity) {
        this.mHomeActivity = mHomeActivity;
    }

    @Override
    public PuzzleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PuzzleListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_puzzle_list, parent, false));

    }

    @Override
    public void onBindViewHolder(PuzzleListViewHolder holder, int position) {
        try {
            final JSONObject mJsonObject = mHomeActivity.getmJsonArrayData().getJSONObject(position);
            String puzzle_image = mJsonObject.getString("puzzle_image");
            int puzzle_size = mJsonObject.getInt("puzzle_size");
            JSONArray mJsonArray = mJsonObject.getJSONArray("positionArray");
            Block[][] mBlocks = new Block[puzzle_size][puzzle_size];
            int index = 0;
            for (int i = 0; i < puzzle_size; i++) {
                for (int j = 0; j < puzzle_size; j++) {
                    JSONObject blockJson = mJsonArray.getJSONObject(index);
                    Block mBlock = new Block(blockJson.getInt("realX"),
                            blockJson.getInt("realY"),
                            blockJson.getInt("currentX"),
                            blockJson.getInt("currentY"));
                    mBlocks[i][j] = mBlock;
                    index++;
                }
            }
            Bitmap bitmap = BitmapFactory.decodeFile(puzzle_image);
            holder.mImageView.setImageBitmap(bitmap);
            holder.mTextViewSize.setText(puzzle_size + " X " + puzzle_size);
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayPuzzleActivity.start(mHomeActivity, mJsonObject.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mHomeActivity.getmJsonArrayData().length();
    }

    public class PuzzleListViewHolder extends ViewHolder {


        public ImageView mImageView;
        public TextView mTextViewSize;

        public PuzzleListViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_puzzle_pic);
            mTextViewSize = (TextView) itemView.findViewById(R.id.tv_grid_size);
        }
    }
}
