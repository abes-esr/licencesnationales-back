package fr.abes.lnevent;

import fr.abes.lnevent.entities.IpEntity;
import fr.abes.lnevent.repository.IpRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LneventApplicationTests {

    @Autowired
    private IpRepository ipRepository;

    @Test
    void ipTest() {
        ipRepository.save(new IpEntity(null,
                "eeee",
                "987645"));
        List<IpEntity> a = ipRepository.findAllBySiren("987645");
    }

}
