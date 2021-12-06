package fr.abes.licencesnationales.core.services;

import java.util.UUID;

public class GenererIdAbes {

    private static final String baseDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String toBase62( int decimalNumber ) {
        return fromDecimalToOtherBase( 62, decimalNumber );
    }

    public static int fromBase16( String base16Number ) {
        return fromOtherBaseToDecimal( 16, base16Number );
    }

    private static String fromDecimalToOtherBase ( int base, int decimalNumber ) {
        String tempVal = decimalNumber == 0 ? "0" : "";
        int mod = 0;

        while( decimalNumber != 0 ) {
            mod = decimalNumber % base;
            tempVal = baseDigits.substring( mod, mod + 1 ) + tempVal;
            decimalNumber = decimalNumber / base;
        }

        return tempVal;
    }

    private static int fromOtherBaseToDecimal( int base, String number ) {
        int iterator = number.length();
        int returnValue = 0;
        int multiplier = 1;

        while( iterator > 0 ) {
            returnValue = returnValue + ( baseDigits.indexOf( number.substring( iterator - 1, iterator ) ) * multiplier );
            multiplier = multiplier * base;
            --iterator;
        }
        return returnValue;
    }

    //  genere une cle primaire
    public static String generateId() {
        String id16s = UUID.randomUUID().toString().replace("-","");

        int[] id10s = new int[8];
        int i = 0;int j = 0;
        String chunk = "";
        for(i=0;i<id16s.length();i++){
            chunk = chunk + id16s.charAt(i);
            if ((i+1)%4==0){
                id10s[j] = fromBase16(chunk);
                chunk = "";
                j = ++j;
            }
        }
        String identifiant = "";
        i = 0;
        for(int id10:id10s){
            identifiant = identifiant + toBase62(id10);
            i = ++i;
        }
        return   identifiant;
    }

    public static String genererIdAbes(String identifiant) {
        return "ABES" + identifiant.substring(0, 9).toUpperCase();
    }




}
