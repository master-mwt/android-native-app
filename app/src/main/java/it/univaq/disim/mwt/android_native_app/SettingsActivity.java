package it.univaq.disim.mwt.android_native_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import it.univaq.disim.mwt.android_native_app.dialogs.StoragePermissionDeniedDialogFragment;
import it.univaq.disim.mwt.android_native_app.services.UserCollectionService;
import it.univaq.disim.mwt.android_native_app.utils.FileHandler;
import it.univaq.disim.mwt.android_native_app.utils.StoragePermission;

public class SettingsActivity extends AppCompatActivity {

    private MaterialButton exportDBButton;
    private MaterialButton importDBButton;
    private MaterialButton exportDBToFirestoreButton;
    private MaterialButton importDBFromFirestoreButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        exportDBButton = findViewById(R.id.export_button);
        exportDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StoragePermission.isStoragePermissionGranted(SettingsActivity.this)){
                    FileHandler.saveBackup(SettingsActivity.this);
                } else {
                    StoragePermission.requestStoragePermission(SettingsActivity.this);
                }
            }
        });

        importDBButton = findViewById(R.id.import_button);
        importDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: needed permissions ?
                FileHandler.pickBackup(SettingsActivity.this);
            }
        });

        exportDBToFirestoreButton = findViewById(R.id.export_firestore_button);
        exportDBToFirestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(getApplicationContext(), UserCollectionService.class);
                    intent.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_DB_FIRESTORE_EXPORT);
                    intent.putExtra(UserCollectionService.KEY_DATA, mAuth.getCurrentUser().getEmail());
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "exporting to Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        importDBFromFirestoreButton = findViewById(R.id.import_firestore_button);
        importDBFromFirestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(getApplicationContext(), UserCollectionService.class);
                    intent.putExtra(UserCollectionService.KEY_ACTION, UserCollectionService.ACTION_DB_FIRESTORE_IMPORT);
                    intent.putExtra(UserCollectionService.KEY_DATA, mAuth.getCurrentUser().getEmail());
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "importing from Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Firestore change database rule on console
        if(mAuth.getCurrentUser() == null){
            exportDBToFirestoreButton.setEnabled(false);
            importDBFromFirestoreButton.setEnabled(false);
        } else {
            exportDBToFirestoreButton.setEnabled(true);
            importDBFromFirestoreButton.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileHandler.onActivityResultFileHandler(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == StoragePermission.REQUEST_PERMISSION_CODE){
            if(StoragePermission.isStoragePermissionGranted(this)){
                FileHandler.saveBackup(this);
            } else {
                showPermissionNotGrantedDialog();
            }
        }
    }

    private void showPermissionNotGrantedDialog(){
        StoragePermissionDeniedDialogFragment dialog = new StoragePermissionDeniedDialogFragment();
        dialog.show(getSupportFragmentManager(), "storage_denied_dialog");
    }
}
