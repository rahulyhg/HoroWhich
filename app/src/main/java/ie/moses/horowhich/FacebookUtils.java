package ie.moses.horowhich;

import com.facebook.AccessToken;

public final class FacebookUtils {

  public static boolean isLoggedIn() {
    return AccessToken.getCurrentAccessToken() != null;
  }

}
