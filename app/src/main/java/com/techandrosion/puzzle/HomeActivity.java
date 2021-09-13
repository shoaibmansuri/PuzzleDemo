package com.techandrosion.puzzle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 2;
    public static final int REQUEST_CODE_CREATE_PUZZLE = 3;

    private Context mContext;
    LinearLayout mLinearLayoutErrorLayout;
    RecyclerView mRecyclerViewPuzzles;
    private JSONArray mJsonArrayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        mLinearLayoutErrorLayout = (LinearLayout) findViewById(R.id.ll_error_layout);
        mRecyclerViewPuzzles = (RecyclerView) findViewById(R.id.rv_puzzles);
        findViewById(R.id.fab_create_puzzle).setOnClickListener(this);
        setAdapter();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_create_puzzle) {
            openGallery();
        }
    }


    public JSONArray getmJsonArrayData() {
        return mJsonArrayData;
    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
                return;
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_CODE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(mContext, "Sorry storage permission denied!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            Intent mIntent = new Intent(mContext, CreatePuzzleActivity.class);
            mIntent.putExtra("imagePath", imagePath);
            startActivityForResult(mIntent, REQUEST_CODE_CREATE_PUZZLE);
        } else if (requestCode == REQUEST_CODE_CREATE_PUZZLE && resultCode == RESULT_OK) {
            setAdapter();
        }
    }

    private void setAdapter() {
        mJsonArrayData = Utils.getSavedPuzzles(this);
        mRecyclerViewPuzzles.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerViewPuzzles.setAdapter(new PuzzleListAdapter(this));
        if (mJsonArrayData.length() == 0) {
            mLinearLayoutErrorLayout.setVisibility(View.VISIBLE);
        } else {
            mLinearLayoutErrorLayout.setVisibility(View.GONE);
        }
    }
}
