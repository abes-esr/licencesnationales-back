package fr.abes.licencesnationales.core.entities.ip;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import oracle.jdbc.driver.Const;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IpV4Test {
    private StatutIpEntity statut;
    @BeforeEach
    void init() {
        statut = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
    }

    @Test
    @DisplayName("Ip unique - valide")
    void testValidIpV4_single() throws IpException {
        IpV4 ip = new IpV4("90.36.1.1", "test", statut);
    }

    @Test
    @DisplayName("Ip unique - valide mais dans les IP réservées")
    void testReservedIpV4_single() throws IpException {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4("192.168.20.15", "test", statut);
        });
        assertEquals("192.168.20.15/32 est inclus dans les IP réservées 192.168.0.0/16", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip unique - chaîne vide")
    void testInvalidIpv4Pattern1() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4("", "test", statut);
        });
        assertEquals("Ip ne peut pas être nulle", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip unique - chaîne nulle")
    void testInvalidIpv4Pattern3() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4(null, "test", statut);
        });
        assertEquals("Ip ne peut pas être nulle", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 simple non valide")
    void testInvalidIpv4Pattern2() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV4 ip = new IpV4("abcdefgABCDEFG", "test", statut);
        });
        assertEquals("Invalid IPv4 address: 'abcdefgABCDEFG'", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 plage non valide")
    void testInvalidIpV4_range() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV4 ip = new IpV4("192.168.20-5.15-2", "test", statut);
        });
        assertEquals("Invalid range [192.168.20.15..192.168.5.2]", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 plage valide mais dans les IP réservées")
    void testValidIpV4_range() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4("192.168.5-20.2-15", "test", statut);
        });
        assertEquals("192.168.5.2-192.168.20.15 est inclus dans les IP réservées 192.168.0.0/16", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 plage valide mais avec des espaces")
    void testValidIpV4_range_whitespace() throws IpException {
        IpV4 ip = new IpV4("193.51 .5 - 7.0 - 255", "test", statut);
    }

    @Test
    @DisplayName("Test fonctionnelle 1")
    void test_fonc_1() throws IpException {
        IpV4 ip = new IpV4("193.51.5-7.0-255", "test", statut);
    }

    @Test
    @DisplayName("Test fonctionnelle 2 - plage 1")
    void test_fonc_2_range_1() throws IpException {
        IpV4 ip = new IpV4("193.55.60-63.0-255", "test", statut);
    }

    @Test
    @DisplayName("Test fonctionnelle 2 - plage 2")
    void test_fonc_2_range_2() throws IpException {
        IpV4 ip = new IpV4("193.55.44-45.0-255", "test", statut);
    }

    @Test
    @DisplayName("Test fonctionnelle 2 - ip simple")
    void test_fonc_2_ip() throws IpException {
        IpV4 ip = new IpV4("154.59.125.63", "test", statut);
    }

    @Test
    @DisplayName("Test fonctionnelle 3 - ip simple")
    void test_fonc_3() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV4 ip = new IpV4("154.1399.0.289", "test", statut);
        });
        assertEquals("Invalid IPv4 address: '154.1399.0.289'", exception.getLocalizedMessage());
    }
}