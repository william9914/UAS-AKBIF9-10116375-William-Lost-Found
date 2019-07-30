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

public class AddPostActivity extends AppCompatActivity {

    EditText mTitle, mDescrEt;
    ImageView mPostTv;
    Button mUploadBtn;

    String mStoragePath = "barang_hilang/";
    String mDatabasePath = "Data";

    Uri mFilePathUri;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    ProgressDialog mProgressDialog;

    int IMAGE_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mTitle=findViewById(R.id.pTitleEt);
        mDescrEt=findViewById(R.id.pDescrEt);
        mPostTv=findViewById(R.id.pImagetTv);
        mUploadBtn=findViewById(R.id.pUploadBtn);


        mPostTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST_CODE);
            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReference(mStoragePath);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(AddPostActivity.this);

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirebase();
            }
        });

        }



    private void uploadDataToFirebase(){
        if(mFilePathUri != null){
            mProgressDialog.setTitle("Image is Uploading...");
            mProgressDialog.show();
            StorageReference storageReference2nd = mStorageReference.child(mStoragePath + System.currentTimeMillis() + "." + getFileExtension(mFilePathUri));

            storageReference2nd.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String mPostTitle = mTitle.getText().toString().trim();
                            String mPostDescr = mDescrEt.getText().toString().trim();
                            mProgressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Image Uploaded...", Toast.LENGTH_SHORT).show();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mPostTitle, mPostDescr, taskSnapshot.getDownloadUrl().toString(), mPostTitle.toLowerCase());
                            String imageUploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.setTitle("Image is Uploading");
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
            mFilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePathUri);
                mPostTv.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
