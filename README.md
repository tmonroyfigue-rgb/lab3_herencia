TecniHogar – Lab 3 Herencia

Archivos del proyecto

Todos los archivos .java están en la misma carpeta (sin paquetes):

Archivo	Descripción
Main.java	Menú de consola, lectura de datos y datos iniciales. Contiene main.
SistemaTecniHogar.java	Gestiona órdenes, cuadrillas, jornadas, consultas y reportes.
OrdenServicio.java	Clase abstracta con los datos y el estado comunes de una orden.
Diagnostico.java	Categoría Diagnóstico.
Instalacion.java	Categoría Instalación.
InstalacionElectricaCertificada.java	Hereda de Instalacion.
MantenimientoAire.java	Categoría Mantenimiento de aire acondicionado.
Cuadrilla.java	Cuadrilla de técnicos.
Jornada.java	Jornada de una cuadrilla con sus órdenes asignadas.
RegistroIngresos.java	Ingresos por registro, por visitas adicionales y por categoría.
Cotizacion.java	Resultado temporal de una cotización.
Zona.java	Enum: METROPOLITANA, PERIFERICA.
EstadoOrden.java	Enum: PENDIENTE, ASIGNADA, EN_ATENCION, COMPLETADA, CANCELADA.
EstadoJornada.java	Enum: PREPARACION, INICIADA, CERRADA.
Cómo compilar

Abre una terminal en la carpeta donde están los archivos .java y ejecuta:

javac -encoding UTF-8 *.java

Si prefieres dejar los .class en una carpeta aparte:

mkdir out
javac -encoding UTF-8 -d out *.java

En Windows con CMD, si *.java no se expande, usa:

javac -encoding UTF-8 Main.java

(javac compila automáticamente las demás clases que Main necesita.)

Cómo ejecutar

Si compilaste en la misma carpeta:

java Main

Si compilaste con -d out:

java -cp out Main
Tildes y símbolos en Windows

Si en la consola se ven mal las tildes (por ejemplo Ã³ en lugar de ó), ejecuta antes de iniciar el programa:

chcp 65001
java -Dfile.encoding=UTF-8 Main
Con un IDE (IntelliJ IDEA, Eclipse, NetBeans, VS Code)
Crea un proyecto Java nuevo.
Copia los 14 archivos .java en la carpeta src (sin crear paquetes).
Ejecuta la clase Main.
Uso del menú
===== TECNIHOGAR =====
1. Cotizar / registrar orden
2. Registrar cuadrilla
3. Crear jornada
4. Asignar orden a jornada
5. Retirar orden de jornada
6. Iniciar jornada
7. Registrar resultado de visita
8. Consultar órdenes
9. Consultar cuadrillas
10. Consultar jornadas
11. Reportes
0. Salir

El menú se repite hasta que se elige 0. Las entradas con formato incorrecto o las operaciones no permitidas muestran un mensaje que empieza con ERROR: y no modifican los datos ni los ingresos.

Flujo típico
Cotizar / registrar (opción 1): se ingresa el código, la categoría y los datos del servicio. El programa muestra el costo y los días hábiles. Si el cliente responde s, la orden se registra, se cobra el monto completo y queda PENDIENTE. Si responde n, no se guarda nada.
Crear jornada (opción 3): se indica el código de la cuadrilla. Se genera un identificador (J-1, J-2, ...). Una cuadrilla no puede tener más de una jornada sin cerrar.
Asignar orden (opción 4): se indica el ID de la jornada y el código de la orden.
Retirar orden (opción 5): solo mientras la jornada está en preparación. La orden vuelve a PENDIENTE.
Iniciar jornada (opción 6): las órdenes pasan a EN_ATENCION. Aquí se cobran los Q40 por cada visita posterior a la primera. No se puede iniciar una jornada vacía.
Registrar resultado (opción 7): 1 = completada, 2 = visita fallida. Cuando todas las órdenes de la jornada tienen resultado, la jornada se cierra y la cuadrilla queda libre.
Reportes (opción 11): órdenes por categoría y estado, ingresos (por registro, por visitas adicionales, total y por categoría) y jornadas no cerradas con horas asignadas y capacidad disponible.
Datos iniciales (sin ingresos)

Órdenes pendientes

Código	Categoría	Zona	Horas	Otros datos	Costo
D-001	Diagnóstico	Metropolitana	1.0	–	Q150.00
D-002	Diagnóstico	Periférica	1.5	–	Q225.00
I-001	Instalación	Metropolitana	2.5	5 puntos (3.75 h por puntos)	Q460.00
I-002	Instalación	Periférica	2.0	4 puntos	Q445.00
E-001	Inst. eléctrica certificada	Metropolitana	2.5	5 puntos, materiales Q2,000	Q680.00
E-002	Inst. eléctrica certificada	Periférica	3.0	6 puntos, materiales Q1,500	Q820.00
M-001	Mantenimiento de aire	Metropolitana	2.0	18,000 BTU	Q400.00
M-002	Mantenimiento de aire	Metropolitana	1.5	24,000 BTU	Q345.00

Cuadrillas

Código	Capacidad	Certificada en refrigeración
C-01	8.0 h	Sí
C-02	6.0 h	No
C-03	3.0 h	No
Reglas de negocio implementadas
Categoría	Costo	Días hábiles (metro. / periférica)	Máx. visitas
Diagnóstico (máx. 1.5 h)	Q150 metropolitana, Q225 periférica	1 / 2	3
Instalación	Base Q100 (metro.) o Q175 (perif.) + Q90 × horas facturables, donde horas facturables = techo(máx(horas estimadas, puntos × 0.75))	3 / 5	3
Instalación eléctrica certificada	Costo de instalación + Q120 + 5 % del valor de materiales	5 / 7	2
Mantenimiento de aire acondicionado (9,000 a 60,000 BTU, solo zona metropolitana)	Base Q100 + Q110 × horas estimadas + Q80	2	1
Cada visita adicional a la primera genera un cargo de Q40, que se registra al iniciar la jornada.
Al agotar las visitas, la orden queda CANCELADA y no puede asignarse de nuevo.
La capacidad de la cuadrilla se calcula con las horas estimadas, no con las facturables.
Los mantenimientos de aire acondicionado solo se asignan a cuadrillas certificadas en refrigeración.
Las cotizaciones, los retiros de órdenes y las operaciones rechazadas no alteran los ingresos.

Nota: para el mantenimiento de aire se usa la tarifa base metropolitana de Q100, ya que solo se acepta en zona metropolitana.

Diseño (resumen)
OrdenServicio es una clase abstracta. Cada subclase sobrescribe calcularCosto(), estimarDias(), maxVisitas() y categoria(). MantenimientoAire también sobrescribe esCompatible(Cuadrilla).
InstalacionElectricaCertificada hereda de Instalacion y reutiliza su cálculo con super.
Jornada y SistemaTecniHogar no verifican el tipo de la orden: llaman a los métodos de OrdenServicio y el polimorfismo decide.
Agregar una categoría nueva solo requiere crear una subclase de OrdenServicio y agregar la opción en el menú de Main. Jornada y los reportes no cambian.
Autor
