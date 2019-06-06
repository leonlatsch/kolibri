package de.leonlatsch.olivia.rest.service;

import java.util.List;

import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.rest.event.RequestListener;
import de.leonlatsch.olivia.rest.repository.UserRestRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRestService {

    private UserRestRepository repository;
    private RequestListener requestListener;

    public UserRestService(UserRestRepository repository) {
        this.repository = repository;
    }

    public void setRequestListener(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    public void loadAll() {
        Call<List<UserDTO>> call = repository.getAll();
        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                requestListener.requestSucceeded(response);
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                requestListener.requestFailed(t);
            }
        });
    }

    public void loadByUid(int uid) {
        Call<UserDTO> call = repository.getbyUid(uid);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                requestListener.requestSucceeded(response);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                requestListener.requestFailed(t);
            }
        });
    }

    public void loadByEmail(String email) {
        Call<UserDTO> call = repository.getByEmail(email);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                requestListener.requestSucceeded(response);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                requestListener.requestFailed(t);
            }
        });
    }

    public void loadByUsername(String username) {
        Call<UserDTO> call = repository.getByUsername(username);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                requestListener.requestSucceeded(response);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                requestListener.requestFailed(t);
            }
        });
    }
}