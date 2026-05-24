# Bam!

**Bam!** es una implementación moderna para Android del clásico juego de cartas **Russian Bank** (también conocido como **Crapette** o **Tunj**).

El nombre del juego proviene del grito de penalización **"¡Bam!"** (o "¡Pun!" en algunas variantes), que se lanza cuando un jugador no realiza un movimiento obligatorio a las fundaciones.

---

## Estado actual (Mayo 2026)

- ✅ El proyecto **compila y se ejecuta** correctamente
- ✅ Motor de cartas 100% reimplementado en Kotlin (sin dependencias externas)
- ✅ Lógica completa del juego portada (`BamGame`, `BamRules`, `BamActions`, etc.)
- ✅ Sistema de "Bam/Pun" funcional
- ✅ IA básica del bot
- ✅ Interfaz con **drag & drop**, animaciones y detección de ganador
- ✅ ViewBinding + Kotlin moderno (JVM 17)

---

## Requisitos

- Android Studio Hedgehog (2023.1) o superior (recomendado Koala+)
- JDK 17
- Android SDK 35 (compileSdk / targetSdk)
- minSdk 26 (Android 8.0 Oreo)

---

## Cómo compilar y ejecutar

### Desde terminal

```bash
# Limpiar y compilar
./gradlew clean assembleDebug

# Instalar en dispositivo/emulador conectado
./gradlew installDebug
```

### Desde Android Studio

1. Abre la carpeta raíz del proyecto (`Bam`)
2. Espera la sincronización de Gradle
3. `Build → Rebuild Project`
4. Ejecuta en un emulador o dispositivo con **API 26 o superior**

---

## Estructura del proyecto

```
com.teoktonos.bam
├── model.cards
│   ├── Card.kt
│   ├── CardStack.kt
│   ├── Deck.kt
│   ├── Rank.kt
│   ├── Suit.kt
│   └── Color.kt
├── BamGame.kt
├── BamRules.kt
├── BamActions.kt
├── BamZone.kt
├── BamPile.kt
├── BamPlayer.kt
├── BotPlayer.kt
├── HumanPlayer.kt
├── GraphicCard.kt
├── InputHandler.kt
├── StacksToViewsBridge.kt
├── GameViewModel.kt
└── MainActivity.kt
```

---

## Reglas del juego (resumen)

Russian Bank es un juego de **solitario competitivo para 2 jugadores** (pueden ser humano vs bot).

### Elementos principales:

- Cada jugador tiene:
  - 1 **mazo** propio (13 cartas)
  - 1 **infierno** (13 cartas)
  - 4 **filas** de construcción
- En el centro hay **4 fundaciones** compartidas (se construyen de As a Rey por palo)
- Se puede jugar desde el mazo, el infierno o las filas hacia las fundaciones o entre filas (siguiendo reglas estrictas de secuencia y color alterno)

### Mecánica "Bam"

Si en tu turno existe un movimiento **obligatorio** a las fundaciones y no lo realizas, pierdes el turno y se dice **"¡Bam!"**.

---

## Próximos pasos / Roadmap

- [ ] Pulido de UX y mensajes de juego
- [ ] Mejorar la inteligencia del bot
- [ ] Soporte para más de 2 jugadores
- [ ] Opciones de configuración (velocidad, vibración, etc.)
- [ ] Sonidos y efectos visuales
- [ ] Publicación en Google Play (futuro)

---

## Comandos útiles

```bash
# Compilar
./gradlew assembleDebug

# Instalar
./gradlew installDebug

# Ver logs
adb logcat | grep -i bam
```

---

## Créditos

Proyecto iniciado y mantenido por **Juan de Villeros** (Aeon@teoktonos.com).

Basado en una reimplementación limpia y moderna del motor original de **PunProto**.

---

**Última actualización:** 24 de mayo de 2026


