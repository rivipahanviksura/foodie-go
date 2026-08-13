package lk.foodie.foodiego;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFoodActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView ivFoodImage;
    private EditText etFoodName, etFoodDescription, etFoodPrice;
    private Spinner spinnerCategory;
    private CheckBox cbAvailable;
    private Button btnUploadImage, btnSaveFood;
    private String foodName,uid,imageUrl; // Store the food name for renaming
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            Log.d("Firebase Profile", "User UID: " + uid);
        }
        ivFoodImage = findViewById(R.id.ivFoodImage);
        etFoodName = findViewById(R.id.etFoodName);
        etFoodDescription = findViewById(R.id.etFoodDescription);
        etFoodPrice = findViewById(R.id.etFoodPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        cbAvailable = findViewById(R.id.cbAvailable);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveFood = findViewById(R.id.btnSaveFood);

        btnUploadImage.setOnClickListener(v -> openFileChooser());
        btnSaveFood.setOnClickListener(v -> saveFoodDataToFirestore());
        loadCategories();
        initCloudinary();

    }
    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        db.collection("category").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                categories.add(document.getString("name"));
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
//            ivFoodImage.setImageURI(imageUri);
            Glide.with(AddFoodActivity.this)
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(ivFoodImage);
            cloudinaryImageUpload(imageUri);
        }
    }

    private void uploadImageToServer() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getRealPathFromURI(imageUri));
        String newFileName = foodName.replaceAll("\\s+", "_") + ".jpg"; // Rename image with food name

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", newFileName, RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();

        Request request = new Request.Builder()
                .url("https://6c46-2402-d000-8104-ba7-c451-5e83-1348-d9c4.ngrok-free.app/FoodieGo/ImageuploadServlet") // Replace with your JavaEE server URL
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AddFoodActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(AddFoodActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dcmwplgyq");
        config.put("api_key","828345373568237");
        config.put("api_secret", "NdwbxMTs2zgui-OlbWws2VAS8Bs");
        MediaManager.init(this, config);
    }

    private void cloudinaryImageUpload(Uri imageUri){
        MediaManager.get().upload(imageUri).option("folder","foods")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {

                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {

                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        imageUrl = resultData.get("secure_url").toString();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {

                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                }).dispatch();
    }
    private void saveFoodDataToFirestore() {
        foodName = etFoodName.getText().toString(); // Get food name for renaming

        String description = etFoodDescription.getText().toString();
        String price = etFoodPrice.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        boolean available = cbAvailable.isChecked();

        if (foodName.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToServer(); // Upload image after saving food name

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> food = new HashMap<>();
        food.put("image",imageUrl);
        food.put("foodName", foodName);
        food.put("description", description);
        food.put("price", Double.parseDouble(price));
        food.put("category", category);
        food.put("available", available);

        db.collection("foods").add(food)
                .addOnSuccessListener(documentReference -> Toast.makeText(AddFoodActivity.this, "Food added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Error adding food", Toast.LENGTH_SHORT).show());
    }

}
