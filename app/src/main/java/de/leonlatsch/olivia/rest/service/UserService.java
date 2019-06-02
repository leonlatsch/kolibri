package de.leonlatsch.olivia.rest.service;

import de.leonlatsch.olivia.rest.repository.UserRestRepository;

public class UserService {

    private UserRestRepository repository;

    public UserService(UserRestRepository repository) {
        this.repository = repository;
    }
}