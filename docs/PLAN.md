# Plan: Proyecto Nuevo "Bam" – Implementación de Russian Bank / Crapette (Tunj)

## Objetivo General
Crear un proyecto Android **limpio y moderno** llamado **Bam!** a partir del código más completo encontrado en `PunProto`, adaptado a estándares actuales (2026).

El juego es una variante de **Russian Bank** (también conocido como Crapette / Tunj), donde "Bam" / "Pun" representa el grito de penalización cuando el jugador no realiza un movimiento obligatorio a las fundaciones.

---

## Decisiones Tomadas (Sesión 24-May-2026)

| Decisión                  | Valor                                      |
|---------------------------|--------------------------------------------|
| Ubicación del proyecto    | `~/Desarrollo/juegos/Bam`                  |
| Nombre de la app          | **Bam!**                                   |
| Package base              | `com.teoktonos.bam`                        |
| minSdk                    | **26** (Android 8.0) – moderno y más fácil |
| targetSdk / compileSdk    | 35 (Android 15)                            |
| UI                        | XML + **ViewBinding** (sin Compose)        |
| Motor de cartas           | Reimplementado 100% en Kotlin (sin JAR)    |
| Nivel de modernización    | **Mínimo** (solo lo necesario para compilar y jugar) |
| Estructura de paquetes    | `com.teoktonos.bam.model.cards`            |
| Dependencias              | Solo las estrictamente necesarias          |
| ProGuard / R8             | Reglas básicas + minify deshabilitado por ahora |

---

## Estado Actual del Proyecto (24 de mayo de 2026)

**¡El proyecto compila y ejecuta correctamente!**

### Logros principales alcanzados:
- [x] Proyecto Android moderno completo (Gradle 8.10 + AGP 8.7 + Kotlin 2.0 + ViewBinding + Java 17)
- [x] Motor de cartas 100% reimplementado en `model.cards` (sin dependencias externas)
- [x] Toda la lógica del juego portada (BamGame, BamRules, BamActions, BamZone, BamPile, jugadores, IA del bot)
- [x] Capa de UI completa portada:
  - GraphicCard
  - ViewGroupX
  - StacksToViewsBridge
  - InputHandler (drag & drop + clicks + validación de reglas)
- [x] Recursos migrados (64 drawables de cartas + bordes + layout completo de 457 líneas + íconos)
- [x] MainActivity + GameViewModel cableados
- [x] **Build exitoso** (`assembleDebug`)
- [x] **La app se instala y ejecuta** en dispositivo/emulador

### Estado actual:
- El juego arranca y muestra el tablero.
- Se pueden realizar interacciones básicas.
- Existen problemas menores (a pulir en sesiones futuras).

### Pendiente (nivel mínimo):
- Pulido de detalles de UI y flujo de partida
- Corrección de posibles crashes o comportamientos extraños durante juego real
- Mejoras de experiencia (mensajes, turnos, etc.)

---

## Estructura de Paquetes Objetivo

```
com.teoktonos.bam
├── model.cards
│   ├── Card.kt
│   ├── CardStack.kt
│   ├── Deck.kt
│   └── RankExtensions.kt
├── BamGame.kt
├── BamRules.kt
├── BamActions.kt
├── BamZone.kt
├── BamPile.kt
├── BamPlayer.kt
├── BotPlayer.kt
├── HumanPlayer.kt
├── Result.kt
├── GraphicCard.kt
├── ViewGroupX.kt
├── StacksToViewsBridge.kt
├── InputHandler.kt
├── FYIMessage.kt
├── DeviceVibrator.kt
├── MainActivity.kt
└── GameViewModel.kt
```

---

## Reimplementación del Motor de Cartas (Crítico)

Se debe crear un paquete `com.teoktonos.bam.model.cards` que exponga **exactamente** la misma interfaz que consumía el código original de `cards.*`.

### Miembros obligatorios que debe soportar (basado en análisis de PunProto):

**Enums:**
- `Card.Suit` (SPADES, HEARTS, DIAMONDS, CLUBS)
- `Card.Rank` (ACE, TWO, ..., KING) – con `ordinal`
- `Card.Color` (RED, BLACK)

**Card:**
- `data class Card(val rank: Rank, val suit: Suit, var faceup: Boolean = false)`
- Propiedades: `name`, `color`
- Métodos: `flip()`

**CardStack:**
- `open class CardStack(val name: String = "")`
- Debe implementar `Iterable<Card>`
- Métodos clave:
  - `top(): Card?`
  - `isEmpty()`, `isNotEmpty()`, `size`
  - `moveTo(target: CardStack)`
  - `takeFrom(source: CardStack, count: Int)`
  - `flip(reverse: Boolean = true)`
  - `showTop(faceup: Boolean = true)`
  - `forEach { ... }`

**Deck:**
- `object Deck { fun create(name: String, shuffled: Boolean = true, facedown: Boolean = true): List<Card> }`

**Extensiones de Rank (usadas intensivamente):**
- `fun Rank.nextOrNull(): Rank?`
- `fun Rank.previousOrNull(): Rank?`
- `fun Rank.adjacentsWithLoop(): List<Rank>`

**BamPile** depende de:
- `Card(Rank.ACE, suit)`
- `top()?.rank?.nextOrNull()`
- `isFull()` (cuando llega a KING)

---

## Checklist de Migración (Nivel Mínimo)

### Fase 1 – Configuración del Proyecto (Actual)
- [ ] Crear `settings.gradle`
- [ ] Crear `build.gradle` (project)
- [ ] Crear `app/build.gradle` con configuración moderna mínima
- [ ] Crear `gradle.properties`
- [ ] Crear `app/proguard-rules.pro` (razonable)
- [ ] Crear `AndroidManifest.xml` básico

### Fase 2 – Motor de Cartas
- [ ] Implementar `model.cards.Card`
- [ ] Implementar `model.cards.CardStack`
- [ ] Implementar `model.cards.Deck`
- [ ] Implementar extensiones de `Rank`
- [ ] Escribir pruebas unitarias básicas (recomendado aunque sea nivel mínimo)

### Fase 3 – Lógica del Juego
- [ ] Portar `BamGame`, `BamRules`, `BamActions`
- [ ] Portar `BamZone`, `BamPile`
- [ ] Portar `BamPlayer`, `BotPlayer`, `HumanPlayer`
- [ ] Portar `Result`, `FYIMessage`, `DeviceVibrator`

### Fase 4 – Capa de Presentación
- [ ] Copiar assets de cartas y drawables
- [ ] Copiar `activity_main.xml` + valores (dimens, colors, strings, styles)
- [ ] Habilitar ViewBinding
- [ ] Portar `GraphicCard`, `ViewGroupX`, `StacksToViewsBridge`
- [ ] Portar `InputHandler`
- [ ] Portar `MainActivity` + `GameViewModel`
- [ ] Reemplazar `findViewById` por ViewBinding

### Fase 5 – Verificación
- [ ] Compilar debug
- [ ] Instalar y lanzar
- [ ] Probar flujo básico (2 jugadores humanos o 1+bot)
- [ ] Verificar mecánica de "Bam/Pun"
- [ ] Verificar victoria

---

## Configuración de Build Recomendada (Nivel Mínimo)

- **Gradle**: 8.10+
- **AGP**: 8.7+
- **Kotlin**: 2.0.20 o superior
- **ViewBinding**: Habilitado
- **minifyEnabled**: `false` (por ahora)
- **ProGuard**: Incluir reglas básicas de keep para `model.cards.**` y clases `Bam*`

---

## Notas Técnicas Importantes

- El layout `activity_main.xml` es grande (~457 líneas) y usa muchos `LinearLayout` + `FrameLayout` para representar pilas.
- `GraphicCard` usa `resources.getIdentifier(...)` basado en el nombre de la carta → el método `Card.name` debe producir strings compatibles (`ace_of_clubs`, `two_of_hearts`, etc.).
- Drag & Drop + `TransitionManager` se usan intensivamente.
- El sistema de "Bam" está distribuido entre `BamRules.isBam()`, `BamAction.isBam`, y `InputHandler.isIllegalOrPun()`.

---

## Próximos Pasos Inmediatos (Siguiente Sesión)

1. Completar los archivos de Gradle del proyecto base.
2. Crear la estructura de paquetes `com.teoktonos.bam.model.cards`.
3. Empezar la reimplementación del motor de cartas (esta es la pieza más crítica).
4. Una vez que el motor compile y pase pruebas básicas, empezar a portar `BamGame` y `BamZone`.

---

---

## Progreso de la Sesión (24 de mayo de 2026)

### Lo que se creó:
- Estructura completa de directorios del proyecto Android moderno
- `gradle/wrapper/gradle-wrapper.properties` (Gradle 8.10)
- `settings.gradle` + `build.gradle` raíz
- `gradle/libs.versions.toml` (Version Catalog limpio)
- `app/build.gradle` con:
  - ViewBinding activado
  - Java 17 / Kotlin JVM 17
  - minSdk 26, targetSdk 35, compileSdk 35
  - Dependencias modernas mínimas
- `app/proguard-rules.pro` básico con keeps para `model.cards` y clases Bam*
- `AndroidManifest.xml` + permisos (VIBRATE) + orientación portrait
- Recursos básicos (strings, colors, styles, dimens, layout placeholder)
- `MainActivity` stub compilable con ViewBinding
- Paquete base correcto: `com.teoktonos.bam`

### Motor de cartas (`model.cards`) – IMPLEMENTADO
Archivos creados:
- `Suit.kt`
- `Color.kt`
- `Rank.kt` (con `nextOrNull()`, `previousOrNull()` y `adjacentsWithLoop()`)
- `Card.kt` (con `name` compatible con los recursos drawable)
- `CardStack.kt` (soporta `top()`, `moveTo()`, `takeFrom()`, `flip()`, iteración, etc.)
- `Deck.kt` (fábrica de mazos de 52 cartas)

### Lógica del juego – IMPLEMENTADA
Archivos creados:
- `Result.kt`
- `BamPile.kt`
- `BamZone.kt`
- `BamActions.kt`
- `BamRules.kt` (sistema completo de validación + mecánica "Bam")
- `BamGame.kt`
- `BamPlayer.kt` + `BotPlayer.kt` + `HumanPlayer.kt`

### Capa de UI – IMPLEMENTADA
- Assets gráficos copiados (64 drawables: cartas, bordes, fondos)
- `dimens.xml` y `colors.xml` actualizados desde el original
- `activity_main.xml` completo copiado (457 líneas)
- `GraphicCard.kt` portado
- `ViewGroupX.kt` portado
- `StacksToViewsBridge.kt` portado
- `InputHandler.kt` portado (drag & drop + clicks + reglas)
- `FYIMessage.kt` y `DeviceVibrator.kt` creados (stubs funcionales)
- `GameViewModel.kt` creado
- `MainActivity.kt` cableado con inicialización completa del juego + UI

**Estado final de la sesión (24 de mayo de 2026):**

**¡El proyecto compila y la aplicación se ejecuta correctamente!**

- Todo el motor de cartas, la lógica del juego y la capa de UI han sido portados exitosamente.
- Se logró un **BUILD SUCCESSFUL**.
- La app se instala (`adb install`) y ejecuta en dispositivo/emulador.
- Existen problemas menores de pulido que se abordarán en sesiones futuras.

Se actualizó este documento y se creó `docs/COMPILACION_Y_PRIMEROS_PASOS.md` como guía de referencia.

### Próximos pasos recomendados:

1. Probar la aplicación en juego real y reportar comportamientos extraños o crashes.
2. Ir corrigiendo los problemas menores que aparezcan durante el uso.
3. Decidir prioridades futuras (mejorar IA, pulir UX, agregar más jugadores, etc.).

---

**Documento actualizado:** 24 de mayo de 2026  
**Responsable:** Juan de Villeros  
**Estado:** Plan vivo – se actualizará en cada sesión.
