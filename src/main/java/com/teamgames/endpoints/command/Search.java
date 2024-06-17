package com.teamgames.endpoints.command;

import com.teamgames.https.Post;
import com.teamgames.lib.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Nelson
 */
public class Search {

    public int id;
    public String name;
    public String message;

    public static Search[] searches(String secret, String commandType, String search) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("secret", secret);
        params.put("command", commandType);
        params.put("search", search);
        String result = Post.sendPostData(params, "api/command/search", secret);
        Gson gson = new Gson();
        return gson.fromJson(result, Search[].class);
    }


}
