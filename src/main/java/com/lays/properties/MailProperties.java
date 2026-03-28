package com.lays.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mail")
@EnableConfigurationProperties(MailProperties.class)
public class MailProperties {
    private String from;
    private String sender;
    private String subject;
    private String content;

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}