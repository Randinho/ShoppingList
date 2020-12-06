package com.example.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText addRecipeEditText;
    private Button addRecipeButton;

    private FirestoreRecyclerAdapter<Recipe, RecipeViewHolder> adapter;

    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_recipes);

        mAuth = FirebaseAuth.getInstance();
        addRecipeEditText = (EditText) findViewById(R.id.addRecipeEditText);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("recipes").whereEqualTo("userId", mAuth.getUid()).orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe model) {
                holder.setRecipeName(model.getName());
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                holder.setRecipeId(snapshot.getId());
            }

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
                return new RecipeViewHolder(view);
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

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void addRecipe(View view){
        String name = addRecipeEditText.getText().toString();

        if(!name.isEmpty()) {
            db.collection("recipes").add(new Recipe(name, mAuth.getUid()))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("TAG", "Document snapshot written with ID: " + documentReference.getId());
                            finish();
                            startActivity(getIntent());
                        }

                    });
        }
        else {
            Toast.makeText(RecipesActivity.this, "Please type in the recipe name", Toast.LENGTH_SHORT).show();
        }
    }

    private class RecipeViewHolder extends RecyclerView.ViewHolder {
        private View view;
        String recipeId;

        RecipeViewHolder(View itemView){
            super(itemView);
            view = itemView;

            //Button details = (Button) view.findViewById(R.id.recipeDetailsButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("id", recipeId);
                    context.startActivity(intent);

                }
            });
        }

        void setRecipeName(String recipeName){
            TextView textView = view.findViewById(R.id.text_view);
            textView.setText(recipeName);
        }
        void setRecipeId(String id){
            recipeId = id;
        }
    }
}


