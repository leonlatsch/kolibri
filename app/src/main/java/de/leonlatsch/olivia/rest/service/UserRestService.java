package de.leonlatsch.olivia.rest.service;

import java.util.List;

import de.leonlatsch.olivia.dto.UserAuthDTO;
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
        call.enqueue(this.<List<UserDTO>>createCallback());
    }

    public void loadByUid(int uid) {
        Call<UserDTO> call = repository.getbyUid(uid);
        call.enqueue(this.<UserDTO>createCallback());
    }

    public void loadByEmail(String email) {
        Call<UserDTO> call = repository.getByEmail(email);
        call.enqueue(this.<UserDTO>createCallback());
    }

    public void loadByUsername(String username) {
        Call<UserDTO> call = repository.getByUsername(username);
        call.enqueue(this.<UserDTO>createCallback());
    }

    public void create(UserDTO userDTO) {
        Call<String> call = repository.create(userDTO);
        call.enqueue(this.<String>createCallback());
    }

    public void update(UserDTO userDTO) {
        Call<String> call = repository.update(userDTO);
        call.enqueue(this.<String>createCallback());
    }

    public void delete(int uid) {
        Call<String> call = repository.delete(uid);
        call.enqueue(this.<String>createCallback());
    }

    public void checkUsername(String username) {
        Call<String> call = repository.checkUsername(username);
        call.enqueue(this.<String>createCallback());
    }

    public void checkEmail(String email) {
        Call<String> call = repository.checkEmail(email);
        call.enqueue(this.<String>createCallback());
    }

    public void auth(UserAuthDTO userAuthDTO) {
        Call<String> call = repository.auth(userAuthDTO);
        call.enqueue(this.<String>createCallback());
    }

    private <T> Callback<T> createCallback() {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                requestListener.requestSucceeded(response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                requestListener.requestFailed(t);
            }
        };
    }
}