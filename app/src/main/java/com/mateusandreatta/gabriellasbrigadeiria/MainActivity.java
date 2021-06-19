package com.mateusandreatta.gabriellasbrigadeiria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mateusandreatta.gabriellasbrigadeiria.Utils.CircleTransform;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "TAG-NewOrderActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        updateNavUser();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_order, R.id.nav_profile, R.id.nav_product, R.id.nav_delivery)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        TextView textViewFooter = binding.getRoot().findViewById(R.id.footer_item_app_version);
        textViewFooter.setText(getString(R.string.app_version_prefix) + BuildConfig.VERSION_NAME);

        updateFCMToken();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    public void updateNavUser(){
        Log.i("TAG-MainActivity", "updateNavUsername called");

        if(firebaseUser != null){
            if(firebaseUser.getDisplayName() != null){
                TextView textView = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.nav_header_title);
                textView.setText("Olá, " + firebaseUser.getDisplayName());
                Log.i("TAG-MainActivity", "Username changed");
            }
            if(firebaseUser.getPhotoUrl() != null){
                ImageView imageView = (ImageView) binding.navView.getHeaderView(0).findViewById(R.id.imageView);
                Picasso.get().load(firebaseUser.getPhotoUrl()).transform(new CircleTransform()).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).into(imageView);
               Log.i("TAG-MainActivity", "Photo changed");
            }
        }
    }

    private void updateFCMToken() {

        if (firebaseUser != null) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Map<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("token", token);

                    db.collection("tokens").document(firebaseUser.getUid()).set(objectHashMap);
                }
            });
        }

    }

}