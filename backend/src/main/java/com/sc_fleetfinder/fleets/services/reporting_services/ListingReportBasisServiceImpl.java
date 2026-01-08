package com.sc_fleetfinder.fleets.services.reporting_services;

import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportBasisRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReportBasisDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ListingReportBasisServiceImpl implements ListingReportBasisService {

    private final ListingReportBasisRepository lrbr;
    private final ModelMapper modelMapper;

    ListingReportBasisServiceImpl(ListingReportBasisRepository lrbr, ModelMapper modelMapper) {
        this.lrbr = lrbr;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ListingReportBasisDto> getAll() {
        return lrbr.findAll().stream()
                .map(b -> modelMapper.map(b, ListingReportBasisDto.class))
                .collect(Collectors.toList());
    }
}
