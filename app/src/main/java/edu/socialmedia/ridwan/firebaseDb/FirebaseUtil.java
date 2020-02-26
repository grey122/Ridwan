package edu.socialmedia.ridwan.firebaseDb;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    private static FirebaseUtil sFirebaseUtil;
    public static ArrayList<Product> sProducts;


    private FirebaseUtil(){

    }

    public static void openFbReference(String ref){
        if (sFirebaseUtil == null){
            sFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();


        }
        sProducts = new ArrayList<Product>();
        sDatabaseReference = mFirebaseDatabase.getReference().child(ref);



    }







}
