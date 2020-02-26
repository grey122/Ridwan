package edu.socialmedia.ridwan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.socialmedia.ridwan.firebaseDb.Product;

public class ProductListActivity extends AppCompatActivity {
    ArrayList<Product> mProductArrayList;
    private FirebaseDatabase mFirebaseDatabase ;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

       RecyclerView rvProduct = (RecyclerView) findViewById(R.id.rv_product_list);
       final ProductAdapter productAdapter = new ProductAdapter();
       rvProduct.setAdapter(productAdapter);
        LinearLayoutManager productLayoutManger =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(productLayoutManger);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_menu:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }
}
