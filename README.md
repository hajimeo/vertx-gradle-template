# vertx-gradle-template

```bash
$ git remote rm origin
```
Above is only for not accidentally commit. Not necessary if you folk.

## IntelliJ
From Project Structure, select Java 8 (Java 10 does not work)

#### configuration
Name: xxxxxxx  
Main classs: io.vertx.core.Starter  
Program arguments: run com.example.database.ServerVerticle  
Use classpath of module: vertx-hive-jdbc.main  

## Test
```bash
yum install httpd-tools
ab -r -n 10 -c 2 "http://192.168.1.27:8080/atscale
```
