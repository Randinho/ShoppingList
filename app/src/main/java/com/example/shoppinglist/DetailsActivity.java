package com.example.shoppinglist;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseFirestore db;
    TextView details;
    private RecyclerView recyclerView;
    private String id;
    private ImageView imageView;
    private String currentPhotoPath;

    private FirestoreRecyclerAdapter<Ingredient, DetailsActivity.IngredientViewHolder> adapter;


    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.ingredientsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = (ImageView) findViewById(R.id.recipeImageView);


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

                        if(documentSnapshot.get("path") != null) {
                            File imgFile = new File(documentSnapshot.getString("path"));
                            if(imgFile.exists()){
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                imageView.setImageBitmap(myBitmap);

                            }
                        }

                    }
                }
            });


        adapter = new FirestoreRecyclerAdapter<Ingredient, IngredientViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IngredientViewHolder holder, int position, @NonNull Ingredient model) {
                holder.setIngredientName(model.getName());
                holder.setPossession(model.isPossession());
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                holder.setIngredientId(snapshot.getId());
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

    public void resetPossessionOfIngredients(View view) {
        db.collection("recipes").document(id).collection("ingredients").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                db.collection("recipes").document(id).collection("ingredients").document(snapshot.getId())
                                        .update("possession", false);
                            }
                        }
                    }
                });
    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch(IOException ex) {
                Log.d("EXTAG", ex.getMessage());
            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.shoppinglist.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("IMAGETAG", "Image saved in: " +photoURI.toString());
                Log.d("IMAGETAG",currentPhotoPath);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           db.collection("recipes").document(id).update("path", currentPhotoPath);
           finish();
           startActivity(getIntent());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private class IngredientViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private String ingredientId;


        IngredientViewHolder(View itemView){
            super(itemView);
            view = itemView;

            CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.ingredientItemCheckbox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox) v).isChecked())
                    {
                        db.collection("recipes").document(id).collection("ingredients").document(ingredientId)
                                .update("possession", true);
                    }
                    else {
                        db.collection("recipes").document(id).collection("ingredients").document(ingredientId)
                                .update("possession", false);
                    }
                }
            });
    }

        void setIngredientName(String recipeName){
            TextView textView = view.findViewById(R.id.ingredientItemTextView);
            textView.setText(recipeName);
        }

        void setPossession(boolean isChecked){
            CheckBox check = view.findViewById(R.id.ingredientItemCheckbox);
            check.setChecked(isChecked);
        }

        void setIngredientId(String id) {
            ingredientId = id;
        }



    }


}
