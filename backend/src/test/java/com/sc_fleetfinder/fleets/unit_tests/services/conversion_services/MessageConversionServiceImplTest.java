package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import com.sc_fleetfinder.fleets.services.conversion_services.ChatDataConversions.MessageConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageConversionServiceImplTest {

    @Mock
    private ModelMapper messageMapper;

    @InjectMocks
    private MessageConversionServiceImpl service;

    @Test
    void convertToDto_DelegatesToModelMapper() {
        // Message no-arg constructor is available via @NoArgsConstructor
        Message entity = mock(Message.class);
        GetMessageDto expectedDto = new GetMessageDto();
        when(messageMapper.map(entity, GetMessageDto.class)).thenReturn(expectedDto);

        GetMessageDto result = service.convertToDto(entity);

        assertThat(result).isEqualTo(expectedDto);
        verify(messageMapper).map(entity, GetMessageDto.class);
    }
}
