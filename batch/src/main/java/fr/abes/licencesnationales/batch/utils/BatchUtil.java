package fr.abes.licencesnationales.batch.utils;

import java.util.Date;

public class BatchUtil {
    private static DateHelper dateHelper = new DateHelperImpl();

    public static void load(DateHelper newDateHelper) {
        dateHelper = newDateHelper;
    }

    public static Date getDate() {
        return dateHelper.getDate();
    }
}
