package com.example.tester;

import com.google.gson.annotations.SerializedName;

public class Quote {

    @SerializedName("quote")
    private String quote;

    @SerializedName("author")
    private String author;

    @SerializedName("category")
    private String success;

    public Quote(String quote, String author, String success) {
        this.quote = quote;
        this.author = author;
        this.success = success;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "quote='" + quote + '\'' +
                ", author='" + author + '\'' +
                ", success='" + success + '\'' +
                '}';
    }
}
