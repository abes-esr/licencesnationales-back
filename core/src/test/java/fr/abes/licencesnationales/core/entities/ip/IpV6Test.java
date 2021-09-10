package fr.abes.licencesnationales.core.entities.ip;

import fr.abes.licencesnationales.core.exception.IpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IpV6Test {

    @Test
    @DisplayName("Ip unique - valide")
    public void testValidIpV6_single() throws IpException {
        IpV6 ip = new IpV6("5800:10C3:E3C3:F1AA:48E3:D923:D494:AAFF", "test");
    }

    @Test
    @DisplayName("Ip unique - chaîne nulle")
    public void testInvalidIpV6_single() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV6 ip = new IpV6(null, "test");
        });
        assertEquals("Ip ne peut pas être nulle", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip unique - chaîne vide")
    public void testInvalidIpv6Pattern1() {
        Exception exception = assertThrows(IpException.class, () -> {
            IpV6 ip = new IpV6("", "test");
        });
        assertEquals("Ip ne peut pas être nulle", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip unique - IPV4")
    public void testInvalidIpV6_ipv4() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV6 ip = new IpV6("192.168.1.1", "test");
        });
        assertEquals("Invalid IPv6 address: '192.168.1.1'", exception.getLocalizedMessage());
    }

    @Test
    @DisplayName("Ip plage - valide")
    public void testValidIpV6_range() throws IpException {
        IpV6 ip = new IpV6("5800:10C3:E3C3:F1AA:48E3:D923:D494-D497:AAFF-BBFF", "test");
    }

    @Test
    @DisplayName("Ip plage - non valide")
    public void testInvalidIpV6_range() throws IpException {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            IpV6 ip = new IpV6("5800:10C3:E3C3:F1AA:48E3:D923:D497-D494:BBFF-AAFF", "test");
        });
        assertEquals("Invalid range [5800:10c3:e3c3:f1aa:48e3:d923:d497:bbff..5800:10c3:e3c3:f1aa:48e3:d923:d494:aaff]", exception.getLocalizedMessage());
    }

}