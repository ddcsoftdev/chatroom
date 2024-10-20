package com.chatapp.messaging.domain.message.service;

import com.chatapp.conversation.application.service.ConversationReaderService;
import com.chatapp.conversation.domain.aggregate.Conversation;
import com.chatapp.message.domain.aggregate.Message;
import com.chatapp.message.domain.aggregate.MessageBuilder;
import com.chatapp.messaging.domain.message.aggregate.MessageSendNew;
import com.chatapp.messaging.domain.message.repository.MessageRepository;
import com.chatapp.message.domain.vo.MessagePublicId;
import com.chatapp.message.domain.vo.MessageSendState;
import com.chatapp.message.domain.vo.MessageSentTime;
import com.chatapp.shared.service.State;
import com.chatapp.user.domain.aggregate.User;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class MessageCreator {

    private final MessageRepository messageRepository;
    private final MessageChangeNotifier messageChangeNotifier;
    private final ConversationReaderService conversationReader;

    public MessageCreator(MessageRepository messageRepository, MessageChangeNotifier messageChangeNotifier,
                          ConversationReaderService conversationReader) {
        this.messageRepository = messageRepository;
        this.messageChangeNotifier = messageChangeNotifier;
        this.conversationReader = conversationReader;
    }


    public State<Message, String> create(MessageSendNew messageSendNew, User sender) {
            Message newMessage = MessageBuilder.message()
                .content(messageSendNew.messageContent())
                .publicId(new MessagePublicId(UUID.randomUUID()))
                .sendState(MessageSendState.RECEIVED)
                .sentTime(new MessageSentTime(Instant.now()))
                .conversationId(messageSendNew.conversationPublicId())
                .sender(sender.getUserPublicId())
                .build();

        State<Message, String> creationState;
        Optional<Conversation> conversationToLink = conversationReader.getOneByPublicId(messageSendNew.conversationPublicId());
        if (conversationToLink.isPresent()) {
            Message messageSaved = messageRepository.save(newMessage, sender, conversationToLink.get());
            messageChangeNotifier.send(newMessage, conversationToLink.get().getMembers().stream()
                    .map(User::getUserPublicId).toList());
            creationState = State.<Message, String>builder().forSuccess(messageSaved);
        } else {
            creationState = State.<Message, String>builder().forError(
                    String.format("Unable to find the conversation to link the message with the " +
                            "following publicId %s", messageSendNew.conversationPublicId().value())
            );
        }
        return creationState;
    }

}
