package fr.abes.licencesnationales.batch.relance;

import fr.abes.licencesnationales.batch.utils.DateHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelperTest implements DateHelper {
    @Override
    public Date getDate() {
        Calendar calendar = new GregorianCalendar(2020, Calendar.JANUARY, 1);
        return calendar.getTime();
    }
}
