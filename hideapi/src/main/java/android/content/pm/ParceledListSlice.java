package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParceledListSlice<T extends Parcelable> extends BaseParceledListSlice<T> {
    public static final Parcelable.ClassLoaderCreator<ParceledListSlice<?>> CREATOR =
            new Parcelable.ClassLoaderCreator<ParceledListSlice<?>>() {
                public ParceledListSlice<?> createFromParcel(Parcel in) {
                    throw new IllegalArgumentException("Stub!");
                }

                @Override
                public ParceledListSlice<?> createFromParcel(Parcel in, ClassLoader loader) {
                    throw new IllegalArgumentException("Stub!");
                }

                @Override
                public ParceledListSlice<?>[] newArray(int size) {
                    throw new IllegalArgumentException("Stub!");
                }
            };

    public ParceledListSlice(List<T> list) {
        super(list);
    }

    @Override
    public int describeContents() {
        throw new IllegalArgumentException("Stub!");
    }
}
