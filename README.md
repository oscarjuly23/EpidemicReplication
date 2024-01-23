# EpidemicReplication
Este repositorio presenta una aplicación distribuida diseñada para replicar datos de manera epidémica y lograr una arquitectura de datos multi-versionada. La arquitectura sigue un modelo de capas, con la capa principal (Core Layer) manteniendo las versiones más recientes y las capas subsiguientes conteniendo versiones más antiguas.

## Arquitectura del Sistema

### Core Layer
Esta capa es el núcleo del sistema, donde los clientes envían transacciones de escritura. Implementa una replicación activa mediante la estrategia "Update Everywhere" y "Eager Replication". Esta capa tiene la consistencia más débil en el sistema.

### Layer 1 
Dos nodos en esta capa actúan como respaldo primario (Primary Backup) para los nodos de la capa principal. Implementa replicación pasiva ("Primary Backup") y recibe actualizaciones cada 10 actualizaciones (estrategia "Lazy").

### Layer 2
Similar a la Capa 1, actúa como respaldo primario para la Capa 1 y recibe actualizaciones cada 10 segundos (estrategia "Lazy").

Cada nodo mantiene un archivo de registro local que almacena todas las versiones de datos que ha procesado.

## Acciones
Los clientes lanzan transacciones desde un archivo local. Estas transacciones pueden ser de dos tipos:
- Read-Only: b<f>, r(30), r(49), r(69), c donde <f> es 0, 1 o 2, indicando la capa sobre la cual ejecutar la transacción.
- No Read-Only: b, r(12), w(49,53), r(69), c. Estas transacciones siempre se ejecutan en uno de los nodos de la capa principal.

##  
- @author: Oscar Julian
- @data: Enero 2023
