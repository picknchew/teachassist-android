package me.picknchew.teachassist.parcel;

import android.os.Parcel;

import org.parceler.ParcelClass;
import org.parceler.ParcelConverter;

import me.picknchew.teachassistapi.model.CourseInfo;

@ParcelClass(
        value = CourseInfo.class,
        annotation = @org.parceler.Parcel(converter = CourseInfoParcelConverter.class)
)
public class CourseInfoParcelConverter implements ParcelConverter<CourseInfo> {

    @Override
    public void toParcel(CourseInfo input, Parcel parcel) {
        parcel.writeString(input.getId());
        parcel.writeString(input.getMark());
        parcel.writeString(input.getName());
    }

    @Override
    public CourseInfo fromParcel(Parcel parcel) {
        return new CourseInfo(parcel.readString(), parcel.readString(), parcel.readString());
    }
}
