package com.example.sam.findme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOnline extends AppCompatActivity {

    DatabaseReference onlineRef, currentUserRef, counterRef;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;

    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        listOnline = findViewById(R.id.listOnline);
        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("Find Me");
        setSupportActionBar(toolbar);

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        setupSystem();
        updateList();
    }

    private void updateList() {
        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(
                User.class,
                R.layout.user_layout,
                ListOnlineViewHolder.class,
                counterRef
        ) {
            @Override
            protected void populateViewHolder(ListOnlineViewHolder viewHolder, User model, int position) {
                viewHolder.txtEmail.setText(model.getEmail());
            }
        };
        adapter.notifyDataSetChanged();
        listOnline.setAdapter(adapter);
    }

    private void setupSystem(){
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)){
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    Log.d("LOG", "" + user.getEmail() + " is " + user.getStatus());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                break;
            case R.id.action_logout:
                currentUserRef.removeValue();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
