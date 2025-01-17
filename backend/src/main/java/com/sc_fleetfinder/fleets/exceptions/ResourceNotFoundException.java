package com.sc_fleetfinder.fleets.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    //generic resource not found
    public ResourceNotFoundException(String message) {
        super(message);
    }

    //searching by type LONG id resource not found
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id [%d] not found", resourceName, id));
    }

    //searching by type INTEGER(wrapper) id resource not found
    public ResourceNotFoundException(String resourceName, Integer id) {
        super(String.format("%s with id [%d] not found", resourceName, id));
    }

}