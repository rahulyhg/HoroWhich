package ie.moses.horowhich;

import android.support.annotation.Nullable;

public interface TryFailCallback<T> extends Callback<T> {

  void onFailure(@Nullable Throwable error);

}
