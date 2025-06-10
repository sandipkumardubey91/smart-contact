package com.scm.scm.forms;

import com.scm.scm.entities.Contact;
import com.scm.scm.entities.Message;

public class MessageView {
    private Message message;
    private Contact contact;
    private boolean isFromContact;

    public MessageView(Message message, boolean isFromContact, Contact contact) {
        this.message = message;
        this.isFromContact = isFromContact;
        this.contact = contact;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isFromContact() {
        return isFromContact;
    }
}