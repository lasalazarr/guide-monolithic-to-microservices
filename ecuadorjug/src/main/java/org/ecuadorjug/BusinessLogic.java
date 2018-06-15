package org.ecuadorjug;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.List;

@Stateless
public class BusinessLogic {
    public List<Cloud> getClouds() {
        return Arrays.asList(new Cloud("Oracle", 1), new Cloud("Scala", 2));
    }

    public void saveCloud(Cloud cloud) {
        System.out.println(" Cloud creado desde bean de negocio" + cloud);
    }
}
