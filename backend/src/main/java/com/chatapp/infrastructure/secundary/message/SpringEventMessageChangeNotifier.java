package com.chatapp.infrastructure.secundary.message;

import com.chatapp.conversation.domain.vo.ConversationIdWithUsers;
import com.chatapp.conversation.domain.vo.ConversationViewedForNotification;
import com.chatapp.infrastructure.primary.message.RestMessage;
import com.chatapp.message.domain.aggregate.Message;
import com.chatapp.messaging.domain.message.service.MessageChangeNotifier;
import com.chatapp.conversation.domain.vo.ConversationPublicId;
import com.chatapp.shared.service.State;
import com.chatapp.user.domain.vo.UserPublicId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpringEventMessageChangeNotifier implements MessageChangeNotifier {

    private final NotificationService notificationService;
    private ApplicationEventPublisher applicationEventPublisher;

    public SpringEventMessageChangeNotifier(ApplicationEventPublisher applicationEventPublisher, NotificationService notificationService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.notificationService = notificationService;
    }

    @Override
    public State<Void, String> send(Message message, List<UserPublicId> userToNotify) {
        MessageWithUsers messageWithUsers = new MessageWithUsers(message, userToNotify);
        applicationEventPublisher.publishEvent(messageWithUsers);
        return State.<Void, String>builder().forSuccess();
    }

    @Override
    public State<Void, String> delete(ConversationPublicId conversationPublicId,
                                      List<UserPublicId> userToNotify) {
        ConversationIdWithUsers conversationIdWithUsers = new ConversationIdWithUsers(conversationPublicId, userToNotify);
        applicationEventPublisher.publishEvent(conversationIdWithUsers);
        return State.<Void, String>builder().forSuccess();
    }

    @Override
    public State<Void, String> view(ConversationViewedForNotification conversationViewedForNotification, List<UserPublicId> usersToNotify) {
        MessageIdWithUsers messageIdWithUsers = new MessageIdWithUsers(conversationViewedForNotification, usersToNotify);
        applicationEventPublisher.publishEvent(messageIdWithUsers);
        return State.<Void, String>builder().forSuccess();
    }

    @EventListener
    public void handleDeleteConversation(ConversationIdWithUsers conversationIdWithUsers) {
        notificationService.sendMessage(conversationIdWithUsers.conversationPublicId().value(),
                conversationIdWithUsers.users(), NotificationEventName.DELETE_CONVERSATION);
    }

    @EventListener
    public void handleNewMessage(MessageWithUsers messageWithUsers) {
        notificationService.sendMessage(RestMessage.from(messageWithUsers.message()),
                messageWithUsers.userToNotify(), NotificationEventName.NEW_MESSAGE);
    }

    @EventListener
    public void handleView(MessageIdWithUsers messageIdWithUsers) {
        notificationService.sendMessage(messageIdWithUsers.conversationViewedForNotification(),
                messageIdWithUsers.usersToNotify(), NotificationEventName.VIEWS_MESSAGES);
    }
}
