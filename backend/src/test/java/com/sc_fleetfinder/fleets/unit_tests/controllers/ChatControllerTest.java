package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GenericPageRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.FindOrStartNewConversationDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.GetConvMessagesRqstDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.SendMessageDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.chat.UnMuteAndProvisionRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetConversationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.Chat.GetMessageDto;
import com.sc_fleetfinder.fleets.config.ActivityTracking.UserActivityCache;
import com.sc_fleetfinder.fleets.config.SecurityConfig;
import com.sc_fleetfinder.fleets.controllers.ChatController;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.services.chat_services.ChatService;
import com.sc_fleetfinder.fleets.utils.ConversationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private UserActivityCache userActivityCache;

    private Users mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new Users();
        mockUser.setUserId(1L);
        mockUser.setUsername("testUser");
        mockUser.setKeycloakId("test-kc-id");
        when(userService.verifyUser("test-kc-id")).thenReturn(mockUser);
    }

    // ─── getMyConversations ────────────────────────────────────────────────────

    @Test
    void getMyConversations_Authenticated_ReturnsPage() throws Exception {
        GetConversationDto convDto = new GetConversationDto();
        convDto.setConversationId(10L);
        Page<GetConversationDto> page = new PageImpl<>(List.of(convDto));
        when(chatService.findMyConversations(any(), any())).thenReturn(page);

        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        mockMvc.perform(post("/api/chat/my_conversations")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].conversationId").value(10));
    }

    @Test
    void getMyConversations_Unauthenticated_Returns401() throws Exception {
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        mockMvc.perform(post("/api/chat/my_conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isUnauthorized());
    }

    // ─── getConversationMessages ───────────────────────────────────────────────

    @Test
    void getConversationMessages_Authenticated_ReturnsPage() throws Exception {
        GetMessageDto msgDto = new GetMessageDto();
        msgDto.setMsgId(55L);
        Page<GetMessageDto> page = new PageImpl<>(List.of(msgDto));
        when(chatService.findConvMessages(any(), anyLong(), any())).thenReturn(page);

        GetConvMessagesRqstDto dto = new GetConvMessagesRqstDto();
        dto.setPageIdx(0);
        dto.setPageSize(20);
        dto.setConversationId(5L);

        mockMvc.perform(post("/api/chat/conv_messages")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].msgId").value(55));
    }

    @Test
    void getConversationMessages_Unauthenticated_Returns401() throws Exception {
        GetConvMessagesRqstDto dto = new GetConvMessagesRqstDto();
        dto.setPageIdx(0);
        dto.setPageSize(20);
        dto.setConversationId(5L);

        mockMvc.perform(post("/api/chat/conv_messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    // ─── conversationProvision ─────────────────────────────────────────────────

    @Test
    void conversationProvision_Authenticated_ReturnsConversationDto() throws Exception {
        GetConversationDto convDto = new GetConversationDto();
        convDto.setConversationId(7L);
        when(chatService.findOrStartNew(any(), any())).thenReturn(convDto);

        FindOrStartNewConversationDto dto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, "Chat with UserB", 2L);

        mockMvc.perform(post("/api/chat/conv_provision")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(7));
    }

    @Test
    void conversationProvision_Unauthenticated_Returns401() throws Exception {
        FindOrStartNewConversationDto dto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, "Chat", 2L);

        mockMvc.perform(post("/api/chat/conv_provision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    // ─── sendMessage ───────────────────────────────────────────────────────────

    @Test
    void sendMessage_Authenticated_Returns201WithCreatedMessage() throws Exception {
        GetMessageDto createdMsg = new GetMessageDto();
        createdMsg.setMsgId(99L);
        when(chatService.sendNewMessage(any(), any())).thenReturn(createdMsg);

        SendMessageDto dto = new SendMessageDto();
        dto.setConversationId(5L);
        dto.setMsgBody("Hello!");

        mockMvc.perform(post("/api/chat/send_message")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msgId").value(99));
    }

    @Test
    void sendMessage_Unauthenticated_Returns401() throws Exception {
        SendMessageDto dto = new SendMessageDto();
        dto.setConversationId(5L);
        dto.setMsgBody("Hello!");

        mockMvc.perform(post("/api/chat/send_message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    // ─── archiveConvAndReturnConvs ─────────────────────────────────────────────

    @Test
    void archiveConv_Authenticated_ArchivesAndReturnsConversationPage() throws Exception {
        Page<GetConversationDto> page = new PageImpl<>(List.of());
        when(chatService.findMyConversations(any(), any())).thenReturn(page);

        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        mockMvc.perform(patch("/api/chat/archive_conv/3")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isOk());
    }

    @Test
    void archiveConv_Unauthenticated_Returns401() throws Exception {
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        mockMvc.perform(patch("/api/chat/archive_conv/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isUnauthorized());
    }

    // ─── muteConvAndReturnConvs ────────────────────────────────────────────────

    @Test
    void muteConv_Authenticated_MutesAndReturnsConversationPage() throws Exception {
        Page<GetConversationDto> page = new PageImpl<>(List.of());
        when(chatService.findMyConversations(any(), any())).thenReturn(page);

        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        mockMvc.perform(patch("/api/chat/mute_conv/3")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isOk());
    }

    @Test
    void muteConv_Unauthenticated_Returns401() throws Exception {
        GenericPageRequestDto pageDto = new GenericPageRequestDto();
        pageDto.setPageIdx(0);
        pageDto.setPageSize(10);

        mockMvc.perform(patch("/api/chat/mute_conv/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageDto)))
                .andExpect(status().isUnauthorized());
    }

    // ─── unmuteAndReturnConv ───────────────────────────────────────────────────

    @Test
    void unmuteConv_Authenticated_UnmutesAndReturnsConversation() throws Exception {
        FindOrStartNewConversationDto provDto = new FindOrStartNewConversationDto(
                ConversationType.DIRECT, "Chat", 2L);
        GetConversationDto convDto = new GetConversationDto();
        convDto.setConversationId(3L);
        when(chatService.unMuteConversation(any(), any())).thenReturn(provDto);
        when(chatService.findOrStartNew(any(), any())).thenReturn(convDto);

        UnMuteAndProvisionRequestDto dto = new UnMuteAndProvisionRequestDto();
        dto.setConversationId(3L);
        dto.setRecipientId(2L);
        dto.setConvType(ConversationType.DIRECT);

        mockMvc.perform(patch("/api/chat/unmute_conv")
                        .with(jwt()
                                .jwt(j -> j.claim("sub", "test-kc-id"))
                                .authorities(new SimpleGrantedAuthority("ROLE_user")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(3));
    }

    @Test
    void unmuteConv_Unauthenticated_Returns401() throws Exception {
        UnMuteAndProvisionRequestDto dto = new UnMuteAndProvisionRequestDto();
        dto.setConversationId(3L);

        mockMvc.perform(patch("/api/chat/unmute_conv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
