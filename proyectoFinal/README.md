# SITM-MIO
🚌 Proyecto: Construcción del Grafo de Paradas del SITM-MIO

Ingeniería de Software — Tarea de Grafos / Procesamiento de Rutas

Este proyecto toma los datos reales del sistema de transporte masivo SITM-MIO (Cali) y construye un grafo dirigido que representa las paradas (nodos) y los arcos (trayectos entre paradas consecutivas) para cada línea, variante y orientación (ida/regreso).

Se implementa en Java, conectándose a una base de datos PostgreSQL previamente cargada a partir de los archivos CSV oficiales del MIO.

Incluye:     
	•    Lectura de líneas, paradas y secuencias de paradas (linestops)    
	•	Construcción del grafo de arcos respetando (lineid, linevariant, orientation, stopsequence)    
	•	Impresión en consola de los arcos ordenados    
	•	Visualización gráfica del grafo usando Java Swing    


📂 Estructura del proyecto
```text
src/
 ├── ui/
 │    ├── GraphViewer.java      → Programa principal
 │    └── GraphPanel.java       → Panel Swing que dibuja el grafo
 │
 ├── Graph/
 │    └── RouteGraph.java       → Estructura del grafo en memoria
 │
 ├── Repository/
 │    ├── StopRepository.java
 │    ├── LineRepository.java
 │    ├── LineStopRepository.java
 │    ├── Arc.java              → Arco dirigido (from → to)
 │
 ├── model/
 │    ├── Stop.java
 │    ├── Line.java
 │    └── LineStop.java
 │
 └── db/
      └── DatabaseManager.java  → Conexión a PostgreSQL
```

🗄️ Base de datos utilizada

El proyecto usa una BD PostgreSQL con el esquema mio y las siguientes tablas normalizadas:
	•	lines
	•	stops
	•	linestops (contiene orden, variante y orientación)
🔧 ¿Qué hace el programa?

1. Conecta a PostgreSQL

A través de DatabaseManager.

2. Carga:
	•	Todas las paradas desde stops
	•	Todas las líneas desde lines
	•	Todas las paradas por línea ordenadas desde linestops
usando:     
```text
ORDER BY lineid, linevariant, orientation, stopsequence
```
3. Construye el grafo

Para cada par de paradas consecutivas dentro de la misma:
	•	lineid
	•	linevariant
	•	orientation

Si:
```text
    current.stopsequence == prev.stopsequence + 1
```
entonces se añade un arco
```text
    from: prev.stopid
    to:   current.stopid
```
Cada Arc guarda:
	•	línea
	•	variante
	•	orientación
	•	stop de origen
	•	stop de destino
	•	secuencia (fromSequence)

4. Imprime en consola todos los arcos ordenados

Formato:
```text
    ---------------------------------------------------------
    Línea 3422 (A42B) - Variante 0 - IDA
    Arcos (origen -> destino):
    500157 -> 514219
    514219 -> 514067
    514067 -> 514068
    ...
```
Los arcos se agrupan por:
```text
    (lineid, linevariant, orientation)
```
y se ordenan por:
```text
    fromSequence
```
5. Dibuja un grafo visual (Swing)

GraphPanel:
	•	Escala longitudes/latitudes
	•	Dibuja todas las paradas como puntos
	•	Dibuja cada arco como una línea entre dos puntos

![alt text](image.jpeg)

▶️ Cómo ejecutar el proyecto     
1. Ejecutar run Main a GraphViewer    
2. Ir al apartado de debug para visualizar las listas
Esto mostrará:    
	•	Estadísticas iniciales:
```text
    Grafo cargado con 2450 paradas.
    Grafo cargado con 130 líneas.
    Grafo cargado con 6400 arcos.
```

📌 Detalles importantes implementados

✔ Agrupación correcta por línea, variante y orientación
✔ Orden por secuencia real (stopsequence)
✔ Arcos solo entre paradas consecutivas reales
✔ Arc incluye fromSequence para ordenar
✔ Dibujo visual con Swing
✔ Manejo de casos donde falte una parada (verificación de null)
✔ Código modular (repositorios, modelos, grafo, UI)

👥 Autores      
	•	Juan Esteban Sotelo Mera      
    •	Matthew Lane Franco      
	•	Jhon Hortua 
