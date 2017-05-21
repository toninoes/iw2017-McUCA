# McUCA - Ingeniería Web 2017 - Universidad de Cádiz

Aplicación web en Java para la gestión de un establecimiento de comida rápida.


### Participantes en el proyecto

- Manuel Jesús López Jiménez
- Antonio Ruiz Rondán
- Luis Fernando Pérez Peregrino
- Andrés Martínez Gavira


### Herramientas-Tecnologías utilizadas

- Java SE Development Kit 8
- Vaadin
- Spring Boot
- MySQL Community Server 5
- Apache Maven 3
- Apache Tomcat 8
- Selenium
- JUnit
- Git


### Sincronizar el repositorio

- La primera vez: ejecutar los siguientes comandos en el directorio donde vaya a almacenar los archivos de la aplicación ("workspace"):

```sh
iw2017@mcUCA ~/workspace $ mkdir iw2017-McUCA
iw2017@mcUCA ~/workspace $ cd iw2017-McUCA
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git init
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git remote add origin https://github.com/toninoes/iw2017-McUCA.git
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git pull origin master
```

- El resto de las veces: para descargar archivos con las últimas modificaciones de los demás participantes en el proyecto, ubicados ya dentro del directorio del proyecto ("~/workspace/iw2017-McUCA"):

```sh
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git pull origin master
```

- Tras realizar nuestras propias aportaciones/modificaciones, para subir nuevos cambios al repositorio:

```sh
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git add --all
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git commit -m "Descripción de nuestras aportaciones/modificaciones en el proyecto"
iw2017@mcUCA ~/workspace/iw2017-McUCA $ git push origin master
```
