package com.neighbours.neighbours.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedResponse {

    @SerializedName("Post")
    @Expose
    private Post post;
    @SerializedName("Comment")
    @Expose
    private List<Comment> comment = null;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

}