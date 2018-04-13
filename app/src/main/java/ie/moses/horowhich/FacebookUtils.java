package ie.moses.horowhich;

import com.facebook.AccessToken;
import com.facebook.Profile;

public final class FacebookUtils {

  public static boolean isLoggedIn() {
    return AccessToken.getCurrentAccessToken() != null;
  }

  public static String toString(Profile profile) {
    return profile.getId() + ": " + profile.getName();
  }

}
