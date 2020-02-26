package edu.socialmedia.ridwan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.socialmedia.ridwan.firebaseDb.FirebaseUtil;
import edu.socialmedia.ridwan.firebaseDb.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    ArrayList<Product> mProductArrayList;
    private FirebaseDatabase mFirebaseDatabase ;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public ProductAdapter(){
        FirebaseUtil.openFbReference("product");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mProductArrayList = FirebaseUtil.sProducts;


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Product product = dataSnapshot.getValue(Product.class);
                Log.d("product: ", product.getP_name());
                product.setId(dataSnapshot.getKey());
                mProductArrayList.add(product);
                notifyItemChanged(mProductArrayList.size() - 1);




            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);


    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_product_row, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = mProductArrayList.get(position);
        holder.bind(product);

    }

    @Override
    public int getItemCount() {
        return mProductArrayList.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvPname;
        TextView tvPCname;
        TextView tvPPhoneNumber;
        TextView tvWebsite;
        TextView tvManuDate;
        TextView tvExpDate;



        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPname = (TextView) itemView.findViewById(R.id.tvName);
            tvPCname = (TextView) itemView.findViewById(R.id.tvPCName);
            tvPPhoneNumber = (TextView) itemView.findViewById(R.id.tvMobileNumber);
            tvWebsite = (TextView) itemView.findViewById(R.id.tvwebsite);
            tvManuDate = (TextView) itemView.findViewById(R.id.tv_manu_date);
            tvExpDate = (TextView) itemView.findViewById(R.id.tv_expiration_date);
            itemView.setOnClickListener(this);

        }

        public void bind(Product product){
            tvPname.setText(product.getP_name());
            tvPCname.setText(product.getP_company_name());
            tvPPhoneNumber.setText(product.getP_company_phone_number());
            tvWebsite.setText(product.getP_website());
            tvManuDate.setText(product.getP_manu_date());
            tvExpDate.setText(product.getP_exp_date());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("click", String.valueOf(position));
            Product selectProduct = mProductArrayList.get(position);
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("product", selectProduct);
            v.getContext().startActivity(intent);

        }
    }

}
