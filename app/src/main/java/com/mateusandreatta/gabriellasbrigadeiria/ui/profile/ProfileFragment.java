package com.mateusandreatta.gabriellasbrigadeiria.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mateusandreatta.gabriellasbrigadeiria.MainActivity;
import com.mateusandreatta.gabriellasbrigadeiria.R;
import com.mateusandreatta.gabriellasbrigadeiria.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private final String TAG = "TAG-GalleryFragment";

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    StorageReference storageReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    ProgressBar loadingProgressBar;
    Uri imageUri = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadingProgressBar = binding.loading;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        if(firebaseUser != null){
            binding.editTextUserName.setText(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "");
            if(firebaseUser.getPhotoUrl() != null){
                Picasso.get().load(firebaseUser.getPhotoUrl()).error(R.mipmap.ic_launcher_round).into(binding.imageViewUserProfile);
            }
        }

        binding.imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileFragment.this)
                        .cropSquare()  			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        binding.buttonSaveUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.editTextUserName.getText().toString();
                Log.d(TAG, "onclick");
                if(!name.isEmpty()){
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    if (imageUri == null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        updateFirebaseUser(profileUpdates);
                    }else{

                        uploadImageToFirebase(imageUri).addOnCompleteListener(taskUploadImage -> {
                            if(taskUploadImage.isSuccessful()){
                                taskUploadImage.getResult().getStorage().getDownloadUrl().addOnCompleteListener(taskGetDownloadUrl -> {
                                    imageUri = taskGetDownloadUrl.getResult();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .setPhotoUri(imageUri)
                                            .build();
                                    updateFirebaseUser(profileUpdates);
                                });
                            }else{
                                Log.d(TAG, "buttonSaveUserProfile - fail");
                                loadingProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), R.string.toast_info_saved_fail, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else{
                    Toast.makeText(getActivity(), R.string.toast_erro_fill_all_inputs, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateFirebaseUser(UserProfileChangeRequest profileUpdates){
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task1 -> {
                    Log.d(TAG, "updateFirebaseUser - completou");
                    loadingProgressBar.setVisibility(View.GONE);
                    imageUri = null;
                    if(task1.isSuccessful()){
                        Log.d(TAG, "sucesso");
                        ((MainActivity) getActivity()).updateNavUser();
                        Toast.makeText(getActivity(), R.string.toast_info_saved_success, Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d(TAG, "fail");
                        Toast.makeText(getActivity(), R.string.toast_info_saved_fail, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onSaveButtonClick(View view){

        String name = binding.editTextUserName.getText().toString();

        if(!name.isEmpty()){

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task1 -> {

                    });
        }else{
//            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }

    }

    private UploadTask uploadImageToFirebase(Uri imageUri){
        StorageReference fileRef = storageReference.child("profile_"+ firebaseUser.getUid() +".jpg");
        return fileRef.putFile(imageUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult - Called");
        Log.d(TAG, "onActivityResult - requestCode: " + requestCode);
        if(requestCode == ImagePicker.REQUEST_CODE){ // ImagePicker
            if(resultCode == RESULT_OK){
                imageUri = data.getData();
                binding.imageViewUserProfile.setImageURI(imageUri);
            }
        }

    }

}