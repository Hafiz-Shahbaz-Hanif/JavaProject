package com.DC.utilities.hub;

import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.hub.connect.authservice.ConnectAuthServiceApiRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConnectMethods {

    public JSONObject getRandomClientExternalGateway(String auth0Token) throws Exception {
        JSONObject userInfo =  ConnectAuthServiceApiRequest.getUserInfoJsonExternalGateway(auth0Token);
        List<JSONObject> clients = getClientsForUser(userInfo);
        return (JSONObject) SharedMethods.getRandomItemFromList(clients);
    }

    public JSONObject getRandomClient(String auth0Token) throws Exception {
        JSONObject userInfo =  ConnectAuthServiceApiRequest.getUserInfoJson(auth0Token);
        List<JSONObject> clients = getClientsForUser(userInfo);
        return (JSONObject) SharedMethods.getRandomItemFromList(clients);
    }

    public List<JSONObject> getClientsForUser(JSONObject userInfo){
        JSONArray userClients = userInfo.getJSONArray("clients");
        List<JSONObject> clients = new ArrayList<>();
        for (Object userClient : userClients) {
            JSONObject client = (JSONObject) userClient;
            clients.add(client);
        }
        return clients;
    }

    public List<JSONObject> getRetailersForUser(JSONObject client){
        JSONObject filterDefaults = client.getJSONObject("filterDefaults");
        JSONArray userRetailersForClient = filterDefaults.getJSONArray("retailers");
        List<JSONObject> retailers = new ArrayList<>();
        for (Object userRetailerForClient : userRetailersForClient) {
            JSONObject retailer = (JSONObject) userRetailerForClient;
            retailers.add(retailer);
        }
        return retailers;
    }

    public JSONObject getRandomRetailer(JSONObject client){
        List<JSONObject> retailers = getRetailersForUser(client);
        return (JSONObject) SharedMethods.getRandomItemFromList(retailers);
    }
}