package com.example.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    TextView details;
    private RecyclerView recyclerView;
    private String id;

    private FirestoreRecyclerAdapter<Ingredient, DetailsActivity.IngredientViewHolder> adapter;


    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.ingredientsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        details = findViewById(R.id.details_text_view);

        Query query = db.collection("recipes").document(id).collection("ingredients").orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Ingredient> options = new FirestoreRecyclerOptions.Builder<Ingredient>()
                .setQuery(query, Ingredient.class)
                .build();

            DocumentReference recipeRef = db.collection("recipes").document(id);
            recipeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        Log.d("tag", documentSnapshot.getString("name"));
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        String txt = "Ingredients of: " + recipe.getName();
                        details.setText(txt);
                    }
                }
            });


        adapter = new FirestoreRecyclerAdapter<Ingredient, IngredientViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IngredientViewHolder holder, int position, @NonNull Ingredient model) {
                holder.setIngredientName(model.getName());
                holder.setPossession(model.isPossession());
            }

            @NonNull
            @Override
            public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
                return new IngredientViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    public void addIngredient(View view) {
        EditText name = (EditText) findViewById(R.id.addIngredientEditText);
        String txt = name.getText().toString();

        if(!txt.isEmpty()) {
            db.collection("recipes").document(id).collection("ingredients").add(new Ingredient(txt))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("tag", "Ingredient created successfully with ID: " + documentReference.getId());
                            ((EditText) findViewById(R.id.addIngredientEditText)).setText("");
                            //finish();
                            //startActivity(getIntent());
                        }
                    });
        }
        else{
            Toast.makeText(DetailsActivity.this, "Please type in the ingredient", Toast.LENGTH_SHORT).show();
        }
    }


    private class IngredientViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String recipeId;

        IngredientViewHolder(View itemView){
            super(itemView);
            view = itemView;
    }

        void setIngredientName(String recipeName){
            TextView textView = view.findViewById(R.id.ingredientItemTextView);
            textView.setText(recipeName);
        }

        void setPossession(boolean isChecked){
            CheckBox check = view.findViewById(R.id.ingredientItemCheckbox);
            check.setChecked(isChecked);
        }



    }


}
