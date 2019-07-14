package de.leonlatsch.olivia.main.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

        changeProfilePicFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        passwordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

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

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText oldPasswordEditText = view.findViewById(R.id.password_old_password_EditText);
                final EditText newPasswordEditText = view.findViewById(R.id.password_new_password_EditText);
                final EditText confirmPasswordEditText = view.findViewById(R.id.password_confirm_password_EditText);

                Call<StringDTO> call = userService.auth(new UserAuthDTO(userInterface.getUser().getEmail(), Hash.createHexHash(oldPasswordEditText.getText().toString())));
                call.enqueue(new Callback<StringDTO>() {
                    @Override
                    public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                        if (response.isSuccessful()) {
                            if (JsonRespose.OK.equals(response.body().getMessage())) {
                                //TODO checked icon
                                String password = newPasswordEditText.getText().toString();
                                String passwordConfirm = confirmPasswordEditText.getText().toString();
                                if (!password.isEmpty() || Pattern.matches(Regex.PASSWORD, password)) {
                                    //TODO passowrd validation
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StringDTO> call, Throwable t) {}
                });
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
        displayMessage(getString(R.string.account_saved));
        //TODO: save new user and update it
        final User user = mapViewToUser();
        UserDTO dto = DatabaseMapper.mapToDTO(user);

        if (profilePicChanged) {
            dto.setProfilePic(extractBase64());
        }
        // delete local password
        user.setPassword(null);

        Call<StringDTO> call = userService.update(dto);
        call.enqueue(new Callback<StringDTO>() {
            @Override
            public void onResponse(Call<StringDTO> call, Response<StringDTO> response) {
                if (response.isSuccessful()) {
                    if (JsonRespose.OK.equals(response.body().getMessage())) {
                        userInterface.saveUser(user);
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
        //TODO: implement image selection
        profilePicChanged = false; // TODO set true if a new picture is selected
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
        //TODO: add password popup

        return savedUser;
    }

    @Override
    public void entityChanged(User newEntity) {
        if (newEntity != null) {
            mapUserToView(newEntity);
        }
    }
}
