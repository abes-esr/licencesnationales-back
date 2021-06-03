package fr.abes.lnevent;

import fr.abes.lnevent.entities.ContactEntity;
import fr.abes.lnevent.entities.EditeurEntity;
import fr.abes.lnevent.entities.EtablissementEntity;
import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.repository.EditeurRepository;
import fr.abes.lnevent.repository.EtablissementRepository;
import fr.abes.lnevent.repository.IpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LneventApplicationTests {

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private EtablissementRepository etablissementRepository;

    @Autowired
    private EditeurRepository editeurRepository;

    @BeforeEach
    void init() {

        editeurRepository.deleteAll();
        etablissementRepository.deleteAll();
        ipRepository.deleteAll();

        EtablissementEntity etablissementEntity = new EtablissementEntity(null,
                "umtp2init",
                "3555646init",
                "universite",
                "68fqse",
                null,
                null);
        etablissementRepository.save(etablissementEntity);

        EditeurEntity editeurEntity = new EditeurEntity(null,
                "charlesinit",
                "mtpinit",
                new ArrayList<>(),
                new ArrayList<>(),
                null);
        editeurRepository.save(editeurEntity);

        EditeurEntity editeurEntity1 = editeurRepository.getFirstByNom("charlesinit");
        EtablissementEntity etablissementEntity1 = etablissementRepository.getFirstBySiren("3555646init");

        HashSet<EditeurEntity> editeurEntityHashSet = new HashSet<>();
        editeurEntityHashSet.add(editeurEntity1);
        etablissementEntity1.setEditeurs(editeurEntityHashSet);

        HashSet<EtablissementEntity> etablissementEntityHashSet = new HashSet<>();
        etablissementEntityHashSet.add(etablissementEntity1);
        editeurEntity1.setEtablissements(etablissementEntityHashSet);

        etablissementRepository.save(etablissementEntity1);
    }

    @Test
    @Transactional
    void editeurSave() {
        EditeurEntity editeurEntity = new EditeurEntity(null,
                "charles",
                "mtp",
                new ArrayList<>(),
                new ArrayList<>(),
                null);
        editeurRepository.save(editeurEntity);
    }

    @Test
    void EtablissementEntitySave() {

        EtablissementEntity etablissementEntity = new EtablissementEntity(null,
                "umtp2",
                "3555646",
                "universite",
                "68fqse",
                null,
                null);
        etablissementRepository.save(etablissementEntity);
    }

    @Test
    @Transactional
    void etablissementEditeurLink() {
        assertThat(etablissementRepository.getFirstBySiren("3555646init").getEditeurs().size()).isEqualTo(1);

        assertThat(etablissementRepository.getFirstBySiren("3555646init")
                .getEditeurs()
                .stream()
                .filter(editeurEntity -> editeurEntity.getNom().equals("charlesinit")).findFirst().get().getAdresse())
                .isEqualTo("mtpinit");

        assertThat(editeurRepository.getFirstByNom("charlesinit").getEtablissements()
                .stream().filter(etablissementEntity -> etablissementEntity.getSiren().equals("3555646init")).findFirst().get().getName())
                .isEqualTo("umtp2init");
    }

    @Test
    @Transactional
    void contactSave() {

        EtablissementEntity etablissementEntity1 = etablissementRepository.getFirstBySiren("3555646init");
        ContactEntity contactEntity = new ContactEntity(null,
                "moi2",
                "simba",
                "c@est.moi",
                "lacour",
                "le roi",
                "du royaume",
                "animal",
                "34000",
                "CS",
                "Montpellier",
                "admin");

        etablissementEntity1.setContact(contactEntity);

        assertThat(etablissementRepository.getFirstBySiren("3555646init").getContact().getNom()).isEqualTo("moi2");
    }

    @Test
    @Transactional
    void ipSave() {
        EtablissementEntity etablissementEntity1 = etablissementRepository.getFirstBySiren("3555646init");
        IpEntity ipEntity = new IpEntity(
                null,
                "231.256.257.2802", "plage", "ipv4", "Comment");
        etablissementEntity1.getIps().add(ipEntity);


        etablissementRepository.save(etablissementEntity1);

        assertThat(etablissementRepository.getFirstBySiren("3555646init").getIps().size()).isEqualTo(1);
        assertThat(etablissementRepository.getFirstBySiren("3555646init").getIps().stream().findFirst().get().getIp()).isEqualTo("231.256.257.2802");
        assertThat(etablissementRepository.getFirstBySiren("3555646init").getIps().stream().findFirst().get().getTypeAcces()).isEqualTo("plage");
        assertThat(etablissementRepository.getFirstBySiren("3555646init").getIps().stream().findFirst().get().getTypeIp()).isEqualTo("ipv4");
    }

}
