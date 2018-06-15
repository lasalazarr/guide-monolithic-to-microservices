package org.ecuadorjug;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;

@Singleton
public class ListenClouds {

    @Asynchronous
    public void onCloud(@Observes final Cloud cloud){
        System.out.println(cloud);
    }
}
