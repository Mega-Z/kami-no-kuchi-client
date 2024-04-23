package com.megaz.knk;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    // 将Date转换为Long  
    @TypeConverter
    public static Long dateToLong(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    // 将Long转换回Date  
    @TypeConverter
    public static Date longToDate(Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value);
    }
}