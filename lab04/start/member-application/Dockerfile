FROM open-liberty



COPY target/liberty/wlp/usr/servers/defaultServer/ /opt/ol/wlp/usr/servers/defaultServer/

COPY src/main/security/ /opt/ol/wlp/usr/servers/defaultServer/resources/security/

RUN rm -f /opt/ol/wlp/usr/servers/defaultServer/apps/member-application.war.xml
COPY target/member-application.war /opt/ol/wlp/usr/servers/defaultServer/apps/

RUN ln -s /opt/ol/wlp/usr/servers /servers



ENTRYPOINT ["/opt/ol/wlp/bin/server", "run"]
CMD ["defaultServer"]

