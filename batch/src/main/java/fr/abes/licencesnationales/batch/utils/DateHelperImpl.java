package fr.abes.licencesnationales.batch.utils;

import java.util.Calendar;
import java.util.Date;

public class DateHelperImpl implements DateHelper {
    @Override
    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }
}
