# Laboratorio 3 - Herencia

**Nombre completo:** Edith Estefany Pérez Trujillo
**Carné:** 26952

**Nombre completo:** Emily Lucila Menchú Menchú 
**Carné:** 20472

## Descripción
El sistema desarrollado para EnvíaGT consiste en la gestión de envíos, vehículos y despachos de la empresa, quienes antes utilizaban una hoja de cálculo para llevar todos los registros. 

Utilizando herencia y polimorfismo, se logra manejar cuatro categorías de envios, las cuales son sobre, paquete, paquete fragil y carga refrigerada, cada una hereda de la clase abstracta envío, mientras que paquete fragil también hereda de la clase paquete. Estas clases heredan los métodos y atributos de envio, sobreescribiendo ciertos métodos y contieniendo ciertos atributos propios. 

Entre las funcionalidades del programa se encuentran: 
-cotizar y registrar envios
-registrar y consultar vehiculos
-Crear despachos, asignar y rtirar envios
-Registrar si una entrega fue exitosa o fallida 
-Gestionar los reintentos de entrega
-Reportes de envios, ingresos y despachos
-Validaciones de peso, dimensiones, temperaturas y la capacidad de los vehiculos.

****Al iniciar el sistema se cargan 8 envios y 3 vehículos de prueba SIN ingresos registrados. Las operaciones se distribuyen dentro del menú, el cual permanece activo hasta que el usuario decida salir al elegir la opción 0. 

## Cómo ejecutar
```bash
javac -d bin src/*.java
java -cp bin Main
```
