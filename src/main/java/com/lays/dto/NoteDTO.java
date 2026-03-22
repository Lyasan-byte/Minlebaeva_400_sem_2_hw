package com.lays.dto;

public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private boolean isPublic;

    public NoteDTO() {
    }

    public NoteDTO(Long id, String title, String content, boolean isPublic) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
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

    public boolean isPublic() {
        return isPublic;
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

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}