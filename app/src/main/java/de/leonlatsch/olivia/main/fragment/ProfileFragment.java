package de.leonlatsch.olivia.main.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.regex.Pattern;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.constants.JsonRespose;
import de.leonlatsch.olivia.constants.Regex;
import de.leonlatsch.olivia.constants.Values;
import de.leonlatsch.olivia.database.DatabaseMapper;
import de.leonlatsch.olivia.database.EntityChangedListener;
import de.leonlatsch.olivia.database.interfaces.UserInterface;
import de.leonlatsch.olivia.dto.StringDTO;
import de.leonlatsch.olivia.dto.UserAuthDTO;
import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.entity.User;
import de.leonlatsch.olivia.main.MainActivity;
import de.leonlatsch.olivia.rest.service.RestServiceFactory;
import de.leonlatsch.olivia.rest.service.UserService;
import de.leonlatsch.olivia.security.Hash;
import de.leonlatsch.olivia.util.AndroidUtils;
import de.leonlatsch.olivia.util.ImageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements EntityChangedListener<User> {

    private boolean profilePicChanged = false;
    private String passwordCache;

    private UserInterface userInterface;
    private UserService userService;

    private MainActivity parent;
    private View view;
    private ImageView profilePicImageView;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView statusTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        parent = (MainActivity) getActivity();
        profilePicImageView = view.findViewById(R.id.profile_profile_pic_card).findViewById(R.id.profile_profile_pic);
        usernameEditText = view.findViewById(R.id.profile_username_editText);
        emailEditText = view.findViewById(R.id.profile_email_editText);
        passwordEditText = view.findViewById(R.id.profile_password_editText);
        FloatingActionButton changeProfilePicFab = view.findViewById(R.id.profile_profile_pic_change);
        Button saveBtn = view.findViewById(R.id.profile_saveBtn);
        TextView deleteAccount = view.findViewById(R.id.profile_deleteBtn);
        statusTextView = view.findViewById(R.id.profile_status_message);

        changeProfilePicFab.setOnClickListener(v -> changeProfilePic());

        saveBtn.setOnClickListener(v -> save());

        deleteAccount.setOnClickListener(v -> deleteAccount());

        passwordEditText.setOnClickListener(v -> changePassword());

        userInterface = UserInterface.getInstance();
        userInterface.addEntityChangedListener(this);

        userService = RestServiceFactory.getUserService();

        mapUserToView(userInterface.getUser());
        displayMessage(Values.EMPTY);

        return view;
    }

    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        builder.setTitle(getString(R.string.password));

        final View view = getLayoutInflater().inflate(R.layout.popup_password, null);
        builder.setView(view);

        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            // Just initialize this button
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            final EditText oldPasswordEditText = view.findViewById(R.id.password_old_password_EditText);
            final EditText newPasswordEditText = view.findViewById(R.id.password_new_password_EditText);
            final EditText confirmPasswordEditText = view.findViewById(R.id.password_confirm_password_EditText);

            Call<StringDTO> call = userService.auth(new UserAuthDTO(userInterface.getUser().getEmail(), Hash.createHexHash(oldPasswordEditText.getText().toString())));
            call.enqueue(new Callback<StringDTO>() {
                @Override
                public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                    if (response.isSuccessful()) {
                        if (JsonRespose.OK.equals(response.body().getMessage())) {
                            showStatusIcon(oldPasswordEditText, R.drawable.icons8_checked_48);
                            String password = newPasswordEditText.getText().toString();
                            String passwordConfirm = confirmPasswordEditText.getText().toString();

                            if (!password.isEmpty() || Pattern.matches(Regex.PASSWORD, password)) {
                                showStatusIcon(newPasswordEditText, R.drawable.icons8_checked_48);
                                if (password.equals(passwordConfirm)) {
                                    showStatusIcon(confirmPasswordEditText, R.drawable.icons8_checked_48);
                                    passwordCache = password;
                                    dialog.dismiss();
                                } else {
                                    showStatusIcon(confirmPasswordEditText, R.drawable.icons8_cancel_48);
                                }
                            } else {
                                showStatusIcon(newPasswordEditText, R.drawable.icons8_cancel_48);
                            }
                        } else {
                            showStatusIcon(oldPasswordEditText, R.drawable.icons8_cancel_48);
                        }
                    }
                }

                @Override
                public void onFailure(Call<StringDTO> call, Throwable t) {
                    parent.showDialog(getString(R.string.error), getString(R.string.error_no_internet));
                }
            });
        });


    }

    private void deleteAccount() {
        Call<StringDTO> call = userService.delete(userInterface.getUser().getUid());
        call.enqueue(new Callback<StringDTO>() {
            @Override
            public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                if (response.isSuccessful()) {
                    if (JsonRespose.OK.equals(response.body().getMessage())) {
                        parent.logout();
                    } else {
                        parent.showDialog(getString(R.string.error), getString(R.string.error_common));
                    }
                }
            }

            @Override
            public void onFailure(Call<StringDTO> call, Throwable t) {
                parent.showDialog(getString(R.string.error), getString(R.string.error_no_internet));
            }
        });
    }

    private void save() {
        final User user = mapViewToUser();
        UserDTO dto = DatabaseMapper.mapToDTO(user);

        if (profilePicChanged) {
            dto.setProfilePic(extractBase64());
        }

        // delete local password
        user.setPassword(null);
        passwordCache = null;

        Call<StringDTO> call = userService.update(dto);
        call.enqueue(new Callback<StringDTO>() {
            @Override
            public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                if (response.isSuccessful()) {
                    if (JsonRespose.OK.equals(response.body().getMessage())) {
                        userInterface.saveUserFromBackend(user.getUid());
                        displayMessage(getString(R.string.account_saved));
                    }
                } else {
                    parent.showDialog(getString(R.string.error), getString(R.string.error_common));
                }
            }

            @Override
            public void onFailure(Call<StringDTO> call, Throwable t) {
                parent.showDialog(getString(R.string.error), getString(R.string.error_no_internet));
            }
        });
    }

    private void displayMessage(String message) {
        statusTextView.setText(message);
    }

    private String extractBase64() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) profilePicImageView.getDrawable();
        return ImageUtil.createBase64(bitmapDrawable.getBitmap());
    }

    private void changeProfilePic() {
        AndroidUtils.createImagePicker(this).start();
    }

    private void mapUserToView(User user) {
        profilePicImageView.setImageBitmap(ImageUtil.createBitmap(user.getProfilePicTn()));
        usernameEditText.setText(user.getUsername());
        emailEditText.setText(user.getEmail());
    }

    private User mapViewToUser() {
        User savedUser = userInterface.getUser();
        savedUser.setUsername(usernameEditText.getText().toString());
        savedUser.setEmail(emailEditText.getText().toString());
        savedUser.setPassword(Hash.createHexHash(passwordCache));

        return savedUser;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getImages(data).get(0);
            profilePicImageView.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
            profilePicChanged = true;
            // https://github.com/ArthurHub/Android-Image-Cropper
        }
    }

    @Override
    public void entityChanged(User newEntity) {
        if (newEntity != null) {
            mapUserToView(newEntity);
        }
    }

    private void showStatusIcon(EditText editText, int drawable) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable,0 );
    }
}
