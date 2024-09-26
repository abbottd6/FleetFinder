package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.GroupStatusDto;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.services.GroupStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/groupStatuses")
public class GroupStatusController {

    @Autowired
    private GroupStatusService groupStatusService;

    @GetMapping
    public List<GroupStatusDto> getGroupStatuses() {
        return groupStatusService.getAllGroupStatuses();
    }

    @GetMapping("/{id}")
    public GroupStatusDto getGroupStatus(@PathVariable int id) {
        return groupStatusService.getGroupStatusById(id);
    }
}
