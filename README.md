# Simulador del Planificador de Procesos de Linux

Este proyecto consiste en simular el comportamiento del planificador de procesos de Linux, utilizando conceptos de programación concurrente. Para más información sobre el planificador y sobre el diseño del simulador revisar el [PDF](https://github.com/CI4821-Sep-Dic-2019/scheduler/blob/master/Proyecto%20I_%20Entrega%20Final.pdf).

## Uso
### Requerimientos
[Maven](https://maven.apache.org/download.cgi) como manejador de paquetes de Java.

### Instalación
Suponiendo que se está en la carpeta raíz del proyecto, primero nos movemos a la carpeta `/scheduler`:
```bash
cd scheduler
```
y luego se compila el proyecto y se instalan las dependencias:
```bash
mvn package
```

### Ejecución
La manera más rápida de ejecutar el proyecto, una vez compilado es con:
```bash
 java -cp target/scheduler-1.0-SNAPSHOT.jar ci4821.sepdic2019.App 
```