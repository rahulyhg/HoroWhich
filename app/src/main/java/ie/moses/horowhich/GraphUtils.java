package ie.moses.horowhich;

import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class GraphUtils {

  private GraphUtils() {
  }

  public static void getFacebookFriends(AccessToken accessToken, TryFailCallback<List<FacebookFriend>> callback) {
    GraphRequest.newGraphPathRequest(
        accessToken,
        "/me/friends",
        response -> {
          try {
            JSONObject friendsJson = response.getJSONObject();
            List<FacebookFriend> facebookFriends = parseFriendsJson(friendsJson);
            callback.call(facebookFriends);
          } catch (JSONException jsone) {
            callback.onFailure(jsone);
          }
        }).executeAsync();
  }

  public static List<FacebookFriend> parseFriendsJson(final JSONObject root) throws JSONException {
    JSONArray data = root.getJSONArray("data");
    final List<FacebookFriend> facebookFriends = new ArrayList<>();
    for (int i = 0; i < data.length(); i++) {
      JSONObject friend = data.getJSONObject(i);
      String friendName = friend.getString("name");
      String friendId = friend.getString("id");
      FacebookFriend facebookFriend = new FacebookFriend(friendName, friendId);
      facebookFriends.add(facebookFriend);
    }

    return facebookFriends;
  }

  public static GraphRequest makeUserProfilePicGraphRequest(final String userId, final TryFailCallback<String> callback) {
    Bundle params = new Bundle();
    params.putBoolean("redirect", false);
    params.putString("type", "square");

    return new GraphRequest(
        AccessToken.getCurrentAccessToken(),
        userId + "/picture",
        params,
        HttpMethod.GET,
        response -> {
          try {
            String profilePicUrl = (String) response.getJSONObject().getJSONObject("data").get("url");
            callback.call(profilePicUrl);
          } catch (JSONException jsone) {
            callback.onFailure(jsone);
          }
        }
    );
  }

}
