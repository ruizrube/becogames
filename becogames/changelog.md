# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.3.0] - 2020-10-29
### Added
- Compatibilidad con xAPI
- Casos de prueba con Cucumber  


## [1.2.9] - 2020-10-15
### Changed
Centrar botones de juego (start, log, etc.)
Mejoras en test unitarios


## [1.2.8b] - 2020-09-09
### Added
- Nuevo tema en look&feel
- Gráficas en resultado del juego

### Changed
- Actualización a Vaadin 14
- Corregido problema al cerrar sesión
- Mejoras en imagen de fondo
- Últimas acciones estilizadas con fichas
- Textos en inglés

### Fixed
- Se impide arrancar partida con solo un jugador “joined”


## [1.2.7] - 2019-01-01
### Added
- Link con enlace de contacto en página principal
- Test de integración se lanza durante el empaquetado del proyecto
- Se incluye versionado de objeto Game para evitar modificaciones concurrentes
- Se mete un pequeño log de consola en /usermanagement para ver usuarios conectados

### Changed
- Mejora en los mensajes de log de envío de correo
- Se evita que si el servidor de email no manda un correo se detenga el flujo de ejecución
- Ordenar juegos de más reciente a más antiguo



## [1.2.6] - 2019-04-30
### Fixed
- Corrección del bug al verificar usuarios con un ID superior al id 128: https://stackoverflow.com/questions/20541636/comparing-boxed-long-values-127-and-128/20542511


## [1.2.3] - 2019-04-28
### Added
- Se permite ordenar por organización en la tabla de usuarios de user management
- En user management se puede mostrar por todos los usuarios, sólo los activados o sólo los no activados 

### Changed
- Informar de que recibirá un correo de confirmación al registrar nuevo usuario y que revisan la carpeta de spam

### Fixed
- No enviar email de cambio de contraseña a usuarios que no hayan confirmado su cuenta con anterioridad


## [1.2.2] - 2019-04-26
### Changed
- Ajustes en el envío de mails con Gmail
- Parámetro weight se actualiza en incrementos de 0.25


## [1.2.1] - 2019-04-21
### Added
Registrar en el log de eventos los parámetros (peso, asignación inicial, etc.) de creación de la partida

### Changed
- Eliminar el botón GO! de la lista de juegos si el usuario admin no participa en la partida o no es el creador de la misma
- Ancho fijo en la ventanas de log y resultados en la lista de juegos

### Fixed
- Corregida errata en mail: consend->consent


## [1.2.0] - 2019-04-18
### Added
- Dinámica de juego
- Eliminada la limitación de empezar una partida si no hay usuarios ya unidos al juego.
- Se pueden invitar nuevos usuarios aún estando la partida ya comenzada
- Se permite re-invitar a un usuario que previamente la haya rechazado
- El admin o el creador de la partida podrá visualizar los resultados de cada partida mediante un ranking de usuarios.
- Al pulsar el botón comenzar el juego se muestra un aviso con el número de usuarios que se han unido a la partida
- Al pulsar el botón resolver el juego se muestra un aviso informando de si quedan usuarios por unirse a la partida y de cuantos faltan por invertir
- Se añade una opción de configuración al inicio del juego para indicar el peso a aplicar en la fórmula
- Se añade una opción de configuración al inicio del juego para permitir mostrar a los participantes una ventana con el ranking (sólo mostrando el username de cada jugador) de la partida.
- Se añade una opción de configuración al inicio del juego para poder resolver automáticamente el juego cuando no quedan peticiones de invitación por responder y todos los jugadores ya unidos hayan invertido.
- Posibilidad de recuperación segura de contraseña en caso de olvido
- Incluir campo DNI en registro de usuario 
- Se puede activar o desactivar usuarios desde el panel de administración

### Changed
- Añadida coletilla en el consentimiento para el envío de notificaciones
- Añadida coletilla al final de cada email para informar sobre donde revocar sus datos personales.
- En lugar de borrar los datos de los usuarios al revocar su consentimiento, se desactiva su cuenta..
- Valor por defecto de la fecha nacimiento a 1/1/2000
- Actualizado el PDF de instrucciones (se simplifica en dos pasos las instrucciones para iniciar el juego)

### Fixed
- Corrección en la implementación de la fórmula del beneficio para los nuevos juegos
- Cambio en la implementación del envío de emails para evitar problemas con Gmail al mandar correos de forma masiva


## [1.1.0] - 2019-04-02
### Added
- Se almacena en la base de datos, además del perfil (animal) resultante, los valores de extroversión, cordialidad, responsabilidad, estabilidad y apertura
- Se permite volver a crear el perfil del usuario desde cero
- Se incluye breve mensaje de consentimiento de datos en el registro
- Se habilita una opción para eliminar del sistema los datos del usuario
- El factor de ponderación de la fórmula de bienes comunes se calcula de forma automática, sin necesidad de introducirlo al crear el juego
- Permitir finalizar una partida aunque no se hayan recibido todas las aportaciones (botón completar)
- Se recibe feedback sobre cómo ha influido mi aportación sobre la del resto, incluyendo ranking, mínimo obtenible, óptimo social y máximo alcanzable
- Añadido el PDF con instrucciones del juego accesible desde la partida de juego
- Mejoras en ventana de invitar a jugadores
- Campos adicionales para saber en cuantas partidas han sido invitados los usuarios y en cuantas se han unido...
- Se añade un filtro para cada uno de los campos de la tabla, con el fin de poder localizar los usuarios de forma rápida
- Un botón para elegir aleatoriamente N usuarios (de los filtrados con anterioridad) a los que enviar invitaciones 
- Alta de organizaciones
- Baja de organizaciones
- Modificación de organizaciones
- Consulta de organizaciones
- Modificación de usuarios
- Consulta de usuarios
- Exportar datos en formato Excel de los Usuarios, Organizaciones, Partidas, Acciones de cada partida e Items del cuestionario


## [1.0.2] - 2019-02-13
### Added
- Despliegue de aplicación en Amazon Web Services. 
- Accesibilidad desde dispositivos de escritorio o móvil. 
- Como usuario quiero registrarme en el app para poder hacer uso de ella
- Como usuario quiero confirmar la activación de mi cuenta tras recibir un mail
- Como usuario quiero iniciar sesión en el app con usuario y clave
- Como administrador quiero definir el conjunto de preguntas del cuestionario
- Como usuario quiero poder completar un cuestionario de personalidad
- Como usuario quiero que el sistema perfile mi personalidad
- Como administrador quiero crear una nueva partida
- Como propietario quiero arrancar una partida
- Como propietario quiero pausar una partida
- Como usuario quiero retomar una partida
- Como propietario quiero detener una partida
- Como propietario de la partida quiero invitar a otros jugadores 
- Como usuario quiero aceptar o rechazar unirme a partidas concretas
- Como administrador quiero visualizar el log de acciones de cada partida
- Como usuario quiero ver la lista de las últimas acciones que he realizado en las diferentes partidas
- Como usuario quiero ver la lista de partidas en las que participo
- Como participante quiero invertir una cierta cantidad de (puntos/monedas) para lograr el máximo beneficio
- Como propietario quiero conocer el rendimiento logrado por mi y el resto de integrantes
- Como usuario quiero recibir notificaciones por email cuando alguien me invite a una partida
- Como usuario quiero recibir notificaciones por email cuando cambie el juego haya comenzado y cuando haya finalizado
- Como propietario quiero recibir notificaciones por email cuando el juego pueda estar listo para comenzar
- Como propietario/usuario quiero recibir notificaciones web cada vez que haya un cambio en el estado de la partida o se realicen acciones. Ej: “Alguien ha invertido ya” 
- Como usuario quiero que cuando se actualicen los datos del juegos durante la partida se me refresquen la información en pantalla (concurrencia y sincronía)


## [0.1.0] - 2018-07-20
### Added
- Prototipo navegable (no funcional) para dispositivos Android

