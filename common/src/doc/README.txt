Installation:

1. MySQL installieren

2. MySQL starten
[root@mochatini root]# service mysqld start
MySQL starten:                                             [  OK  ]

3. Benutzer metamusic mit Paﬂwort metamusic in Datenbank metamusic anlegen
(kann in core/src/profile/<konfiguriertes profil>.profile und das Profil
 in core/local.properties konfiguriert werden)

[root@mochatini root]# mysql --user=root mysql
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2 to server version: 3.23.56

Type 'help;' or '\h' for help. Type '\c' to clear the buffer.

mysql> GRANT ALL PRIVILEGES ON *.* TO metamusic@localhost IDENTIFIED BY 'metamusic' WITH GRANT OPTION;
mysql> GRANT ALL PRIVILEGES ON *.* TO test@localhost IDENTIFIED BY 'test' WITH GRANT OPTION;

4. Kompilieren 

build/build.sh 

5. Tests starten

build/build.sh tests

6. Dump starten

build/output/metamusic-x.x.xxx/bin/mm dump

7. Interaktiven MP3-Importer starten

build/output/metamusic-x.x.xxx/bin/mm mp3importer


