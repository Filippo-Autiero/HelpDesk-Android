package com.example.helpdesk;

import com.google.gson.annotations.SerializedName;

class ApiUser {
    @SerializedName("Email")
    public String Email;

    @SerializedName("Token")
    public String Token;

    public ApiUser(String email, String token) {
        this.Email = email;
        this.Token = token;
    }
}

class ApiRequest<T> {
    @SerializedName("object")
    public T object;

    @SerializedName("user")
    public ApiUser user;

    public ApiRequest(T object, ApiUser user) {
        this.object = object;
        this.user = user;
    }
}

class ReadParams {
    @SerializedName("ID")
    public int ID;

    @SerializedName("Page")
    public int Page;

    @SerializedName("Perpage")
    public int Perpage;

    public ReadParams(int id, int page, int perpage) {
        this.ID = id;
        this.Page = page;
        this.Perpage = perpage;
    }
}

class DeleteParams {
    @SerializedName("ID")
    public int ID;

    public DeleteParams(int id) {
        this.ID = id;
    }
}

class ApiResponse {
    @SerializedName("message")
    public String message;

    @SerializedName("ID")
    public int id;
}