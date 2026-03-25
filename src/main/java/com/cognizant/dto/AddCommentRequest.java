package com.cognizant.dto;

// Request body for POST /api/defects/{id}/comments
public class AddCommentRequest {

    private String author;
    private String authorRole;
    private String content;

    public AddCommentRequest() {}

    public String getAuthor()                  { return author; }
    public void   setAuthor(String a)          { this.author = a; }

    public String getAuthorRole()              { return authorRole; }
    public void   setAuthorRole(String r)      { this.authorRole = r; }

    public String getContent()                 { return content; }
    public void   setContent(String c)         { this.content = c; }
}
