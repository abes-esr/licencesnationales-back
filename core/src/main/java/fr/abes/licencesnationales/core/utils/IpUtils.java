package fr.abes.licencesnationales.core.utils;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.ip.IpType;
import fr.abes.licencesnationales.core.exception.IpException;

public class IpUtils {

    /**
     * Retourne l'IP de début ou de fin à partir d'une chaîne de caractère normée selon les règles de l'application
     *
     * @param ip         String
     * @param typeIp     Type de l'adresse IP;
     * @param whichValue Valeur de début=1 ou de fin=2
     * @return L'IP au bon format
     */
    public static String getIP(String ip, IpType typeIp, int whichValue) throws IpException {

        if (ip == null || ip.isEmpty()) {
            throw new IpException(Constant.IP_NOTNULL);
        }

        try {
            String temp = "";

            // Par défaut typeIp == IpType.V4
            String separator = ".";
            String splitter = "\\.";
            int base = 10;

            if (typeIp == IpType.IPV6) {
                separator = ":";
                splitter = ":";
                base = 16;
            }

            String[] values = ip.trim().split(splitter);
            int i = 0;
            for (String value : values) {
                temp += value.trim().replaceAll("(.*)-(.*)", "$" + whichValue).trim();
                i++;
                if (i != values.length)
                    temp += separator;
            }
            return temp;
        } catch (Exception ex) {
            throw new IpException(Constant.IP_UNABLE_TO_DECODE);
        }
    }

    // Renvoi l'adresse IP sous forme de chaine de caractère.
    // Rajoute un padding à gauche des nombres
    //
    // Ex:
    // 192.168.1.2
    // retournera
    // 192168001002
    //
    // Dans le cas d'une plage d'adresse, on récupère la plus petite adresse
    // de la plage ou la plus grande en fonction du paramètre 'whichValue' ( 1 ou 2 )
    private String getValue(String ip, String typeIp, String whichValue) {
        String temp = "";
        String[] values = ip.trim().split("\\.|:");
        int base = (typeIp.equals("ipv4")) ? 10 : 16;
        int length = (typeIp.equals("ipv4")) ? 3 : 4;
        for (String value : values) {
            value = value.trim().replaceAll("(.*)-(.*)", "$" + whichValue);
            temp = temp + String.format("%0" + length + "d", Integer.parseInt(value, base));
        }
        return temp;
    }

    private String[] getRange(String ip, Integer whichRange) {
        String[] range = {"0", "1"};
        String[] values = ip.split("\\.|:");
        int i = 0;
        for (String value : values) {
            if (value.matches("(.*)-(.*)")) {
                range[i] = value.split("-")[whichRange];
                i = ++i;
            }
        }
        return range;
    }

    // Utilisé par la fusion d'accès..
    private void replaceRange(Integer whichRange, String[] ranges) {
        String ip = "";
        String[] values = ip.split("\\.|:");
        int i = 0;
        for (String value : values) {
            if (value.matches("(.*)-(.*)"))
                value = value.replaceAll("(.*)-(.*)", (whichRange == 0) ? ranges[0] + "-$2" : "$1-" + ranges[1]);
            ip = ip + value;
            i = ++i;
            if (i != values.length)
                ip = ip + ".";
        }
        ip = ip;
    }
}
