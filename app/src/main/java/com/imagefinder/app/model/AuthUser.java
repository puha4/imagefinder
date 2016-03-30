package com.imagefinder.app.model;

public class AuthUser {
    public Auth auth;

    public static class Auth {
        public Token token;
        public User user;

        public static class Token {
            public String _content;
        }

        public static class User {
            public String nsid;
            public String username;
            public String fullname;
        }
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "auth=" + auth.token._content +
                ", user="+ auth.user.username +
                ", full="+ auth.user.fullname +
                '}';
    }
}
