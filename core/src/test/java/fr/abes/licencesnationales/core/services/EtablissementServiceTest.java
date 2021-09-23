package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEntity;
import fr.abes.licencesnationales.core.entities.statut.StatutEtablissementEntity;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.contactediteur.ContactCommercialEditeurRepository;
import fr.abes.licencesnationales.core.repository.contactediteur.ContactTechniqueEditeurRepository;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EtablissementService.class})
class EtablissementServiceTest {

    @Autowired
    private EtablissementService service;

    @MockBean
    private EtablissementRepository etablissementDao;

    @MockBean
    private ContactCommercialEditeurRepository contactCommercialEditeurDao;

    @MockBean
    private ContactTechniqueEditeurRepository contactTechniqueEditeurDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ContactRepository contactEtablissementDao;

    @MockBean
    private StatutRepository statutRepository;

    @DisplayName("test getFirstBySiren success")
    @Test
    void testGetFirstBySirenSuccess() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);
        Optional<EtablissementEntity> optionEtab = Optional.of(etabIn);
        Mockito.when(etablissementDao.getFirstBySiren("000000000")).thenReturn(optionEtab);
        EtablissementEntity etabOut = service.getFirstBySiren("000000000");
        Assertions.assertEquals(1, etabOut.getId().intValue());
        Assertions.assertEquals("testNom", etabOut.getName());
        Assertions.assertEquals("000000000", etabOut.getSiren());
        Assertions.assertEquals("testType", etabOut.getTypeEtablissement().getLibelle());
        Assertions.assertEquals("12345", etabOut.getIdAbes());
        Assertions.assertEquals("nom", etabOut.getContact().getNom());
        Assertions.assertEquals("prenom", etabOut.getContact().getPrenom());
        Assertions.assertEquals("adresse", etabOut.getContact().getAdresse());
        Assertions.assertEquals("BP", etabOut.getContact().getBoitePostale());
        Assertions.assertEquals("CP", etabOut.getContact().getCodePostal());
        Assertions.assertEquals("ville", etabOut.getContact().getVille());
        Assertions.assertEquals("cedex", etabOut.getContact().getCedex());
        Assertions.assertEquals("telephone", etabOut.getContact().getTelephone());
        Assertions.assertEquals("mail@mail.com", etabOut.getContact().getMail());
        Assertions.assertEquals("password", etabOut.getContact().getMotDePasse());
    }

    @DisplayName("test getFirstBySiren avec Exception")
    @Test
    void testGetFirstBySirenThrows() {
        Mockito.when(etablissementDao.getFirstBySiren("000000000")).thenThrow(new UnknownEtablissementException());
        Assertions.assertThrows(UnknownEtablissementException.class, ()->service.getFirstBySiren("000000000"));
    }

    @DisplayName("test save établissement")
    @Test
    void save() throws MailDoublonException, SirenExistException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        StatutEntity statut = new StatutEtablissementEntity(Constant.STATUT_ETAB_NOUVEAU, "Nouveau");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity( "testNom", "000000000", type, "12345", contact);
        Mockito.when(etablissementDao.existeSiren("000000000")).thenReturn(false);
        Mockito.when(contactEtablissementDao.findByMail("mail@mail.com")).thenReturn(Optional.empty());
        Mockito.when(statutRepository.findById(1)).thenReturn(Optional.of(statut));
        Mockito.when(etablissementDao.save(etabIn)).thenReturn(etabIn);

        service.save(etabIn);

        Mockito.when(etablissementDao.existeSiren("000000000")).thenReturn(true);
        Assertions.assertThrows(SirenExistException.class, () -> service.save(etabIn));

        Mockito.when(etablissementDao.existeSiren("000000000")).thenReturn(false);
        Mockito.when(contactEtablissementDao.findByMail("mail@mail.com")).thenReturn(Optional.of(contact));
        Assertions.assertThrows(MailDoublonException.class, () -> service.save(etabIn));
    }

    @DisplayName("test doublon email")
    @Test
    void testDoublonEmail() {
        Set<ContactTechniqueEditeurEntity> ctSet = new HashSet<>();
        ContactTechniqueEditeurEntity ct1 = new ContactTechniqueEditeurEntity("nom1", "prenom1", "mail1@mail.com");
        ContactTechniqueEditeurEntity ct2 = new ContactTechniqueEditeurEntity("nom2", "prenom2", "mail2@mail.com");
        ctSet.add(ct1);
        ctSet.add(ct2);

        Set<ContactCommercialEditeurEntity> ccSet = new HashSet<>();
        ContactCommercialEditeurEntity cc1 = new ContactCommercialEditeurEntity("nom3", "prenom3", "mail3@mail.com");
        ContactCommercialEditeurEntity cc2 = new ContactCommercialEditeurEntity("nom4", "prenom4", "mail4@mail.com");
        ccSet.add(cc1);
        ccSet.add(cc2);

        Mockito.when(contactTechniqueEditeurDao.findByMailContact("mail1@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactTechniqueEditeurDao.findByMailContact("mail2@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactCommercialEditeurDao.findByMailContact("mail3@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactCommercialEditeurDao.findByMailContact("mail4@mail.com")).thenReturn(Optional.empty());

        Assertions.assertEquals(false, service.checkDoublonMail(ccSet, ctSet));

        Mockito.when(contactTechniqueEditeurDao.findByMailContact("mail1@mail.com")).thenReturn(Optional.of(new ContactTechniqueEditeurEntity("nomTest", "prenomTest", "mail1@mail.com")));
        Assertions.assertEquals(true, service.checkDoublonMail(ccSet, ctSet));

        Mockito.when(contactTechniqueEditeurDao.findByMailContact("mail1@mail.com")).thenReturn(Optional.empty());
        Mockito.when(contactCommercialEditeurDao.findByMailContact("mail3@mail.com")).thenReturn(Optional.of(new ContactCommercialEditeurEntity("nomTest", "prenomTest", "mail3@mail.com")));
        Assertions.assertEquals(true, service.checkDoublonMail(ccSet, ctSet));
    }
}