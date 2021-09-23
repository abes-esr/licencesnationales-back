package fr.abes.licencesnationales.core.entities.ip;

import fr.abes.licencesnationales.core.exception.IpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IpV4Test {

    @Test
    @DisplayName("Ip unique - valide")
    public void testValidIpV4_single() throws IpException {
        IpV4 ip = new IpV4("90.36.1.1", "test");
    }

    @Test
    @DisplayName("Ip unique - valide mais dans les IP réservées")
    public void testReservedIpV4_single() throws IpException {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4("192.168.20.15", "test");
        });
        assertEquals("192.168.20.15/32 est inclus dans les IP réservées 192.168.0.0-192.168.254.254", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip unique - chaîne vide")
    public void testInvalidIpv4Pattern1() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4("", "test");
        });
        assertEquals("Ip ne peut pas être nulle", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip unique - chaîne nulle")
    public void testInvalidIpv4Pattern3() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4(null, "test");
        });
        assertEquals("Ip ne peut pas être nulle", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 simple non valide")
    public void testInvalidIpv4Pattern2() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV4 ip = new IpV4("abcdefgABCDEFG", "test");
        });
        assertEquals("Invalid IPv4 address: 'abcdefgABCDEFG'", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 plage non valide")
    public void testInvalidIpV4_range() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV4 ip = new IpV4("192.168.20-5.15-2", "test");
        });
        assertEquals("Invalid range [192.168.20.15..192.168.5.2]", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 plage valide mais dans les IP réservées")
    public void testValidIpV4_range() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV4 ip = new IpV4("192.168.5-20.2-15", "test");
        });
        assertEquals("192.168.5.2-192.168.20.15 est inclus dans les IP réservées 192.168.0.0-192.168.254.254", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip v4 plage valide mais avec des espaces")
    public void testValidIpV4_range_whitespace() throws IpException {
        IpV4 ip = new IpV4("193.51 .5 - 7.0 - 255", "test");
    }

    @Test
    @DisplayName("Test fonctionnelle 1")
    public void test_fonc_1() throws IpException {
        IpV4 ip = new IpV4("193.51.5-7.0-255", "test");
    }

    @Test
    @DisplayName("Test fonctionnelle 2 - plage 1")
    public void test_fonc_2_range_1() throws IpException {
        IpV4 ip = new IpV4("193.55.60-63.0-255", "test");
    }

    @Test
    @DisplayName("Test fonctionnelle 2 - plage 2")
    public void test_fonc_2_range_2() throws IpException {
        IpV4 ip = new IpV4("193.55.44-45.0-255", "test");
    }

    @Test
    @DisplayName("Test fonctionnelle 2 - ip simple")
    public void test_fonc_2_ip() throws IpException {
        IpV4 ip = new IpV4("154.59.125.63", "test");
    }

    @Test
    @DisplayName("Test fonctionnelle 3 - ip simple")
    public void test_fonc_3() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV4 ip = new IpV4("154.1399.0.289", "test");
        });
        assertEquals("Invalid IPv4 address: '154.1399.0.289'", exception.getLocalizedMessage());
    }
}