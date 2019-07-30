package com.williamlostfoundappnocopyright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddPostTemukan extends AppCompatActivity {

    EditText sTitle, sDescrEt;
    ImageView sPostTv;
    Button sUploadBtn;

    String sStoragePath = "barang_temukan/";
    String sDatabasePath = "Temukan";

    Uri sFilePathUri;

    StorageReference sStorageReference;
    DatabaseReference sDatabaseReference;

    ProgressDialog sProgressDialog;

    int IMAGE_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        sTitle=findViewById(R.id.pTitleEt);
        sDescrEt=findViewById(R.id.pDescrEt);
        sPostTv=findViewById(R.id.pImagetTv);
        sUploadBtn=findViewById(R.id.pUploadBtn);


        sPostTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST_CODE);
            }
        });

        sStorageReference = FirebaseStorage.getInstance().getReference(sStoragePath);
        sDatabaseReference = FirebaseDatabase.getInstance().getReference(sDatabasePath);

        sProgressDialog = new ProgressDialog(AddPostTemukan.this);

        sUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirebase();
            }
        });

    }



    private void uploadDataToFirebase(){
        if(sFilePathUri != null){
            sProgressDialog.setTitle("Image is Uploading...");
            sProgressDialog.show();
            StorageReference storageReference2nd = sStorageReference.child(sStoragePath + System.currentTimeMillis() + "." + getFileExtension(sFilePathUri));

            storageReference2nd.putFile(sFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String mPostTitle = sTitle.getText().toString().trim();
                            String mPostDescr = sDescrEt.getText().toString().trim();
                            sProgressDialog.dismiss();
                            Toast.makeText(AddPostTemukan.this, "Image Uploaded...", Toast.LENGTH_SHORT).show();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mPostTitle, mPostDescr, taskSnapshot.getDownloadUrl().toString(), mPostTitle.toUpperCase());
                            String imageUploadId = sDatabaseReference.push().getKey();
                            sDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    sProgressDialog.dismiss();
                    Toast.makeText(AddPostTemukan.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    sProgressDialog.setTitle("Image is Uploading");
                }
            });
        }
        else {
            Toast.makeText(this, "Please select image or add image name", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data){
        super.onActivityResult(requestcode, resultcode, data);
        if(requestcode == IMAGE_REQUEST_CODE
                && resultcode == RESULT_OK
                && data != null
                && data.getData() !=null){
            sFilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sFilePathUri);
                sPostTv.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
