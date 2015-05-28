package com.yacht.bootcamp.walkietalkie.HelperClasses;

import java.io.Serializable;

/**
 * Created by gebruiker on 27/05/15.
 */
public class TextMessage implements Serializable
{
    private String sender;
    private String message;
    private long timeStamp;

    public TextMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }

    public boolean messageAlreadySent(long timeStamp){
        if(this.timeStamp<timeStamp)
            return true;
        else
            return false;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
