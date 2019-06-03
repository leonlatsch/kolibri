package de.leonlatsch.olivia.rest.service;

import java.io.IOException;
import java.util.List;

import de.leonlatsch.olivia.dto.UserDTO;
import de.leonlatsch.olivia.rest.repository.UserRestRepository;
import retrofit2.Call;
import retrofit2.Response;

public class UserRestService {

    private UserRestRepository repository;

    public UserRestService(UserRestRepository repository) {
        this.repository = repository;
    }

    public List<UserDTO> getAll() {
        final Call<List<UserDTO>> call = repository.getAll();
        Runnable runnable = new Runnable() {
            private volatile List<UserDTO> result;

            @Override
            public void run() {
                Response<List<UserDTO>> response;
                try {
                     response = call.execute();
                } catch (IOException e) {
                    System.err.println(e);
                    response = null;
                }

                result = response.isSuccessful() ? response.body() : null;
            }

            public List<UserDTO> getResult() {
                return result;
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            //TODO
            return ((Runnable) runnable).getResult();
        } catch (InterruptedException e) {
            System.err.println(e);
            return null;
        }
    }
}