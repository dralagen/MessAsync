package fr.dralagen.messasync.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class ServerApplicationTests {

    @Test
    void contextLoads() {
        ApplicationModules.of(ServerApplication.class).verify();
    }

}
