package com.chatapp.conversation.application.service;

import com.chatapp.conversation.domain.aggregate.Conversation;
import com.chatapp.conversation.domain.repository.ConversationRepository;
import com.chatapp.conversation.domain.vo.ConversationPublicId;
import com.chatapp.user.domain.vo.UserPublicId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class ConversationReaderService {

    private final ConversationRepository conversationRepository;

    public ConversationReaderService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Page<Conversation> getAllByUserPublicID(UserPublicId userPublicId, Pageable pageable) {
        return conversationRepository.getConversationByUserPublicId(userPublicId, pageable);
    }

    public Optional<Conversation> getOneByPublicId(ConversationPublicId conversationPublicId) {
        return conversationRepository.getOneByPublicId(conversationPublicId);
    }

    public Optional<Conversation> getOneByPublicIdAndUserId(ConversationPublicId conversationPublicId, UserPublicId userPublicId) {
        return conversationRepository.getConversationByUsersPublicIdAndPublicId(userPublicId, conversationPublicId);
    }
}
