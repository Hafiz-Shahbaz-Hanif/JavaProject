package com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class MarketShareUserInfoResponseBody {

    public Auth0 auth0;
    public List<User> users;

    public List<User> getUsers(){
        return users;
    }

    public Auth0 getAuth0(){
        return auth0;
    }

    public static class Auth0 {
        public String sub;
        public String name;
        public String nickname;
        public String email;

        public String getEmail(){
            return email;
        }

        public String getSub(){
            return sub;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        public String id;
        public String uuid;
        public String first_name;
        public String last_name;
        public String email;
        public String username;
        public String permission_level;
        public String created_at;
        public String updated_at;
        public String release_email;
        public Key key;
        public Client client;

        public Key getKey(){
            return key;
        }

        public Client getClient(){
            return client;
        }

        public String getId(){
            return id;
        }

        public String getUuid(){
            return uuid;
        }

        public String getEmail(){
            return email;
        }

        public String getUsername(){
            return username;
        }

        public static class Key {
            public String id;
            public String key;
            public String ignore_limits;
            public String is_private_key;
            public String ip_addresses;
            public String client_id;
            public String user_id;
            public String created_at;
            public String updated_at;
            public String deleted_at;

            public String getId(){
                return id;
            }

            public String getKey(){
                return key;
            }

            public String getClientId(){
                return client_id;
            }

            public String getUserId(){
                return user_id;
            }
        }

        public static class Client {
            public String id;
            public String uuid;
            public String client_name;
            public String email;
            public String pass;
            public String manufacturer_name;
            public String client_active;
            public String internal_client;
            public String is_ms_lite;
            public String domain;
            public String tld_id;
            public String created_at;
            public String updated_at;
            public String deleted_at;
            public String client_type_id;
            public String catalog_type_id;
            public List<Subscriptions> subscriptions;
            public CatalogType catalog_type;
            public Tld tld;

            public String getId(){
                return id;
            }

            public String getUuid(){
                return uuid;
            }

            public List<Subscriptions> getSubscriptions(){
                return subscriptions;
            }

            public String getClientName(){
                return client_name;
            }

            public String getDomain(){
                return domain;
            }

            public static class Subscriptions {
                public String retailer_id;
                public String retailer_name;
                public String subscription_id;
                public String subscription_name;

                public String getRetailer_id() {
                    return retailer_id;
                }

                public String getRetailer_name() {
                    return retailer_name;
                }

                public String getSubscription_id() {
                    return subscription_id;
                }

                public String getSubscription_name() {
                    return subscription_name;
                }
            }

            public static class CatalogType {
                public String id;
                public String name;
            }

            public static class Tld {
                public String id;
                public String name;
            }


        }
    }

}