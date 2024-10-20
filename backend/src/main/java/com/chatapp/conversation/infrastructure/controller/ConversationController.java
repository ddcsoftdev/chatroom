package com.chatapp.conversation.infrastructure.controller;

import com.chatapp.conversation.application.service.ConversationManagementService;
import com.chatapp.conversation.domain.aggregate.Conversation;
import com.chatapp.conversation.infrastructure.dto.ConversationCreationDto;
import com.chatapp.conversation.infrastructure.dto.ConversationDto;
import com.chatapp.messaging.domain.message.aggregate.ConversationToCreate;
import com.chatapp.conversation.domain.vo.ConversationPublicId;
import com.chatapp.shared.service.State;
import com.chatapp.shared.service.StatusNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationManagementService conversationsApplicationService;

    public ConversationController(ConversationManagementService conversationsApplicationService) {
        this.conversationsApplicationService = conversationsApplicationService;
    }

    @GetMapping
    ResponseEntity<List<ConversationDto>> getAll(Pageable pageable) {
        List<ConversationDto> restConversations = conversationsApplicationService.getAllByConnectedUser(pageable)
                .stream().map(ConversationDto::from).toList();
        return ResponseEntity.ok(restConversations);
    }

    @PostMapping
    ResponseEntity<ConversationDto> create(@RequestBody
                                           ConversationCreationDto restConversationToCreate) {
        ConversationToCreate newConversation = ConversationCreationDto.toDomain(restConversationToCreate);
        State<Conversation, String> conversationState = conversationsApplicationService.create(newConversation);
        if (conversationState.getStatus().equals(StatusNotification.OK)) {
            ConversationDto restConversations = ConversationDto.from(conversationState.getValue());
            return ResponseEntity.ok(restConversations);
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not allowed to create conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<UUID> delete(@RequestParam UUID id) {
        State<ConversationPublicId, String> restConversation = conversationsApplicationService
                .delete(new ConversationPublicId(id));
        if (restConversation.getStatus().equals(StatusNotification.OK)) {
            return ResponseEntity.ok(id);
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not allowed to delete conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<ConversationDto> getOneById(@RequestParam UUID id) {
        Optional<ConversationDto> restConversation = conversationsApplicationService
                .getOneByConversationId(new ConversationPublicId(id))
                .map(ConversationDto::from);
        if (restConversation.isPresent()) {
            return ResponseEntity.ok(restConversation.get());
        } else {
            ProblemDetail problemDetail = ProblemDetail
                    .forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not able to find this conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @PatchMapping("/{id}/read")
    ResponseEntity<Integer> markConversationAsRead(@RequestParam UUID id) {
        State<Integer, String> readUpdateState = conversationsApplicationService.markConversationAsRead(
                new ConversationPublicId(id));
        return ResponseEntity.ok(readUpdateState.getValue());
    }
}
