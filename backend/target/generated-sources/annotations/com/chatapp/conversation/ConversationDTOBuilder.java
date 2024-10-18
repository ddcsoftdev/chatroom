package com.chatapp.conversation;

import com.chatapp.conversation.infrastructure.ConversationDTO;
import com.chatapp.infrastructure.primary.conversation.RestUserForConversation;
import com.chatapp.infrastructure.primary.message.RestMessage;
import java.lang.String;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated("Jilt-1.5")
public class ConversationDTOBuilder {
  private UUID publicId;

  private String name;

  private List<RestUserForConversation> members;

  private List<RestMessage> messages;

  public static ConversationDTOBuilder conversationDTO() {
    return new ConversationDTOBuilder();
  }

  public ConversationDTOBuilder publicId(UUID publicId) {
    this.publicId = publicId;
    return this;
  }

  public ConversationDTOBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ConversationDTOBuilder members(List<RestUserForConversation> members) {
    this.members = members;
    return this;
  }

  public ConversationDTOBuilder messages(List<RestMessage> messages) {
    this.messages = messages;
    return this;
  }

  public ConversationDTO build() {
    return new ConversationDTO(publicId, name, members, messages);
  }
}