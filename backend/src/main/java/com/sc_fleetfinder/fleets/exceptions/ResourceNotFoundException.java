package com.sc_fleetfinder.fleets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    //generic resource not found
    public ResourceNotFoundException(String message) {
        super(message);
    }

    //searching by String and type LONG id resource not found
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id [%d] not found", resourceName, id));
    }

    //searching by String and type INTEGER(wrapper) id resource not found
    public ResourceNotFoundException(String resourceName, Integer id) {
        super(String.format("%s with id [%d] not found", resourceName, id));
    }

    //searching by type Integer id
    public ResourceNotFoundException(Integer id) {
        super(String.format("Resource with id: [%d] not found", id));
    }

    //searching by type Long id
    public ResourceNotFoundException(Long id) {
        super(String.format("Resource with id: [%d] not found", id));
    }

}