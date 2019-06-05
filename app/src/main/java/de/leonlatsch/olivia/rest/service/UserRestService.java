package de.leonlatsch.olivia.rest.service;

import java.io.IOException;
import java.util.List;

import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.rest.repository.UserRestRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRestService {

    private UserRestRepository repository;

    public UserRestService(UserRestRepository repository) {
        this.repository = repository;
    }

    public List<UserDTO> getAll() {
        Call<List<UserDTO>> call = repository.getAll();
        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                System.out.println("SUCCESS");

                System.out.println(String.valueOf(response.code()));

                if (response.code() == 200) {
                    for (UserDTO user : response.body()) {
                        System.out.println(user.getUid());
                        System.out.println(user.getUsername());
                        System.out.println(user.getEmail());
                        System.out.println(user.getPassword());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                System.out.println("FAIL " + t);
            }
        });
        return null;
    }
}