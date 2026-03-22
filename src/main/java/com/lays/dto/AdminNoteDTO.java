package com.lays.dto;

import java.time.LocalDateTime;

public class AdminNoteDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean isPublic;
    private Long authorId;
    private String authorUsername;

    public AdminNoteDTO() {
    }

    public AdminNoteDTO(Long id,
                        String title,
                        String content,
                        LocalDateTime createdAt,
                        boolean isPublic,
                        Long authorId,
                        String authorUsername) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.isPublic = isPublic;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }
}