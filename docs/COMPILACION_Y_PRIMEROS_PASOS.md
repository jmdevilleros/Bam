# Guía de Compilación y Primeros Pasos - Proyecto Bam!

## Requisitos

- **Android Studio** Hedgehog | 2023.1.1 o superior (recomendado: Koala o Ladybug 2024.2+)
- **JDK 17** (Android Studio lo incluye por defecto desde 2023)
- **Android SDK**:
  - compileSdk 35 (Android 15)
  - targetSdk 35
  - minSdk 26
- Gradle 8.10 (se descarga automáticamente vía wrapper)

---

## Pasos para abrir y compilar el proyecto

### 1. Abrir el proyecto

1. Abre Android Studio
2. `File → Open...`
3. Selecciona la carpeta `Bam` (la raíz, donde está `settings.gradle`)
4. Espera a que termine la sincronización de Gradle (primera vez puede tardar 2-5 minutos)

### 2. Primera compilación

Ejecuta en terminal dentro de la carpeta del proyecto:

```bash
./gradlew clean assembleDebug
```

O desde Android Studio:
- `Build → Clean Project`
- `Build → Rebuild Project`

---

## Errores comunes y soluciones

### Error: "Could not find com.android.tools.build:gradle"

**Causa**: Primera sincronización sin conexión o caché corrupto.

**Solución**:
- Asegúrate de tener internet
- En Android Studio ve a `File → Invalidate Caches / Restart`

### Error: "Namespace not specified"

En `app/build.gradle` ya está definido:
```gradle
namespace = "com.teoktonos.bam"
```

Si aparece, sincroniza de nuevo (`File → Sync Project with Gradle Files`).

### Error de "Unresolved reference: R"

- Haz `Build → Clean Project` + `Rebuild Project`
- Si persiste, `File → Invalidate Caches / Restart`

### Advertencias de "kotlin-android-extensions"

En este proyecto **ya eliminamos** el plugin `kotlin-android-extensions`. 
Si ves errores relacionados, verifica que en `app/build.gradle` **no** aparezca:

```gradle
apply plugin: 'kotlin-android-extensions'   // ← Este NO debe estar
```

### Problemas con ViewBinding

Si `ActivityMainBinding` no se genera:

1. Verifica que en `app/build.gradle` tengas:
   ```gradle
   buildFeatures {
       viewBinding = true
   }
   ```
2. Rebuild del proyecto.

### Errores de "Unresolved reference: BamGame" u otras clases

Esto es normal la primera vez. Haz:

```bash
./gradlew clean build
```

O en Android Studio: `Build → Rebuild Project`.

---

## Estado actual del juego (Mayo 2026)

### Lo que ya funciona / está implementado:

- Motor de cartas completo (`model.cards`)
- Todas las reglas del juego (`BamRules`)
- Sistema de "Bam/Pun" (la mecánica central de penalización)
- Lógica de turnos y acciones (`BamGame`, `BamActions`)
- IA básica del bot (`BotPlayer`)
- Capa visual completa (cartas, pilas, filas)
- Drag & Drop
- Detección de ganador

### Lo que todavía puede fallar o estar incompleto:

- El cableado final de `MainActivity` con el juego real puede necesitar pequeños ajustes.
- El resaltado del jugador actual y el ciclo de turnos pueden necesitar pulido.
- Algunas animaciones (`TransitionManager`) pueden comportarse diferente en Android moderno.
- No se ha probado exhaustivamente el flujo completo de una partida.
- El `HumanPlayer` actualmente devuelve `NoAction()` (el input real viene de `InputHandler`).

---

## Cómo ejecutar en emulador / dispositivo

1. Crea o selecciona un emulador con **API 26 o superior**.
2. En Android Studio haz clic en el botón de "Run" (el triángulo verde).
3. Selecciona el dispositivo/emulador.
4. Espera a que se instale `Bam!`.

Al abrir la app deberías ver:
- El fondo de mesa
- Las cartas repartidas
- Los indicadores de jugador
- Poder hacer long-press + drag & drop de cartas

---

## Próximos pasos recomendados

1. **Compilar y ejecutar** por primera vez (objetivo principal).
2. Probar mecánicas básicas:
   - Revelar cartas del infierno
   - Mover cartas a filas
   - Intentar movimientos ilegales (debería mostrar mensajes "Pun")
3. Reportar qué errores aparecen en Logcat.
4. Decidir si queremos:
   - Pulir la experiencia actual hasta que sea jugable
   - Mejorar la IA del bot
   - Agregar soporte para 1 humano + bots
   - Limpiar nombres (`Bam*` → nombres más claros)

---

## Comandos útiles

```bash
# Limpiar y compilar
./gradlew clean assembleDebug

# Instalar en dispositivo conectado
./gradlew installDebug

# Ver todos los dispositivos
adb devices

# Ver logs mientras corre la app
adb logcat | grep -i bam
```

---

**Última actualización:** 24 de mayo de 2026

Si al compilar te aparece algún error específico, copia el mensaje aquí y lo resolvemos juntos.
