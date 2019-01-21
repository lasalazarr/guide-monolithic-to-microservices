FROM open-liberty

ENV JVM_ARGS="-Djdk.tls.client.protocols=TLSv1.1,TLSv1.2 -Dhttps.protocols=TLSv1.1,TLSv1.2"

COPY target/liberty/wlp/usr/servers/defaultServer/ /opt/ol/wlp/usr/servers/defaultServer/
COPY src/main/security/ /opt/ol/wlp/usr/servers/defaultServer/resources/security/

RUN rm -f /opt/ol/wlp/usr/servers/defaultServer/apps/application.war.xml \
&& rm -rf /opt/ol/wlp/output/defaultServer/resources/security \
&& ln -s /opt/ol/wlp/usr/servers/defaultServer/resources/security/ /opt/ol/wlp/output/defaultServer/resources/  \
&& ln -s /opt/ol/wlp/usr/servers /servers

COPY target/application.war /opt/ol/wlp/usr/servers/defaultServer/apps/


ENTRYPOINT ["/opt/ol/wlp/bin/server", "run"]
CMD ["defaultServer"]

