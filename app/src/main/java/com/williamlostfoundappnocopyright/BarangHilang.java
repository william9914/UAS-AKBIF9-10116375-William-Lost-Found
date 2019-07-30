package com.williamlostfoundappnocopyright;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BarangHilang extends AppCompatActivity {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private Toolbar mTopToolbar;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang_hilang);

        btn = (Button)findViewById(R.id.btn_addpost);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        mRecyclerView = findViewById(R.id.recylclerview);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Data");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BarangHilang.this,AddPostActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        mRef
                ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {

                viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
