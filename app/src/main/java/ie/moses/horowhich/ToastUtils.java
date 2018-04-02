package ie.moses.horowhich;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtils {

  public static void toast(Context context, CharSequence msg) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

}
