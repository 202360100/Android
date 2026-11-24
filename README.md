# Índice del Repositorio - Android

**Alumno:** Mario Alberto Tapia Turrent  
**Matrícula:** 202360100  
**Curso:** Desarrollo de aplicaciones móviles

Repositorio de prácticas, ejercicios y proyectos desarrollados durante el curso. Incluye aplicaciones con React Native y Expo, proyectos nativos de Android y ejemplos de consumo de servicios y persistencia de datos.

---

## Tecnologías utilizadas

- **JavaScript (ES6) y JSX**
- **React y React Native**
- **Expo:** se identifican proyectos con SDK 54, SDK 57 y SDK 58.
- **React Navigation 7:** navegación Stack, Bottom Tabs y Drawer.
- **Componentes de React Native:** `FlatList`, `SectionList`, `ScrollView`, `TextInput`, `Modal` e `ImageBackground`.
- **Hooks y estado:** `useState`, `useEffect`, `useRef` y Context API, según el proyecto.
- **Animaciones:** API `Animated` de React Native y Reanimated.
- **Persistencia y servicios:** AsyncStorage, Expo SQLite, Supabase y MongoDB.
- **Backend:** Node.js y Express.
- **Android nativo:** proyectos desarrollados con Java y herramientas de Android Studio.

> Las versiones y dependencias pueden variar entre proyectos. Consulta el `package.json` de cada aplicación Expo o los archivos Gradle de cada proyecto nativo.

---

## Estructura del repositorio

```text
Android-main/
│
├── BattleSimulator/
│   └── Simulador de batallas con unidades, movimiento y combate.
│       Incluye creación y consulta de simulaciones guardadas,
│       navegación Drawer y almacenamiento local.
│
├── Ejercicio_01/
│   └── Componentes reutilizables: MiComponente y Mensaje.
│
├── Ejemplo_02/
│   └── Ejemplos de componentes, entrada de texto y composición:
│       DemoChildren, DemoInputText y FlagComponent.
│
├── Ejercicio_03/
│   └── Listas con FlatList y SectionList, además de imágenes
│       e ImageBackground.
│
├── Ejercicio_04/
│   └── Modal reutilizable (CustomModal) y ejemplos de uso.
│
├── Parcial/
│   │
│   ├── Animated/
│   │   └── Ejercicios de animación con la API Animated.
│   │
│   ├── ConsumeApiMongoDB/
│   │   └── Servidor Express para consultar películas mediante
│   │       el endpoint GET /movies y conexión con MongoDB.
│   │
│   ├── DinamicFlatList/
│   │   └── Lista dinámica con FlatList y modal de detalle.
│   │
│   ├── DrawerNavigation/
│   │   └── Navegación mediante menú lateral y ejemplo de
│   │       pantalla de inicio (splash screen).
│   │
│   ├── FitCalc/
│   │   └── Calculadora de IMC con resultado presentado
│   │       mediante un modal.
│   │
│   ├── LifeRpg/
│   │   └── Aplicación de rol con personajes, enemigos,
│   │       dados y arena; utiliza navegación y Context API.
│   │
│   ├── StackNavigation/
│   │   └── Ejemplo de navegación en pila entre pantallas.
│   │
│   └── TabNavigation/
│       └── Navegación inferior por pestañas: Inicio,
│           Buscar y Perfil.
│
└── Ordinario/
    │
    ├── CloneWhatsApp/
    │   └── Aplicación Android nativa con contactos,
    │       conversaciones y mensajes.
    │
    ├── CustomPassEmailRecovery/
    │   └── Flujo de registro, recuperación de contraseña
    │       por correo y creación de una nueva contraseña.
    │
    ├── FaceRecognition/
    │   └── Aplicación React Native relacionada con cámara
    │       y detección de rostros.
    │
    ├── Fragmentado/
    │   └── Aplicación Android con navegación mediante
    │       fragments: Inicio, Acerca y Contacto.
    │
    ├── Layouts/
    │   └── Ejemplos de FrameLayout, LinearLayout,
    │       RelativeLayout y TableLayout.
    │
    ├── Listas/
    │   └── Ejemplo de listas con Adapter para mostrar
    │       información de estudiantes.
    │
    ├── Persistencia/
    │   └── Aplicación React Native para agregar, consultar,
    │       actualizar y eliminar productos; utiliza Supabase.
    │
    ├── PersistenciaAndroid/
    │   └── Proyecto Android nativo relacionado con
    │       persistencia de datos.
    │
    ├── RiskMex/
    │   └── Juego de estrategia por territorios con mapa
    │       de México, ejércitos y resolución de combates.
    │
    ├── Sensores_NavDrawer/
    │   └── Aplicación Android con menú lateral y ejemplos
    │       de cámara, GPS, acelerómetro, giroscopio,
    │       magnetómetro y podómetro.
    │
    ├── VertHorizon/
    │   └── Proyecto Android nativo.
    │
    └── CalculadoraSumas/
        └── Carpeta de proyecto incluida en el repositorio.
```

---

## Ejecución de proyectos React Native con Expo

Cada aplicación Expo tiene su propio `package.json`. Por ello, las dependencias deben instalarse desde la carpeta del proyecto que se desea ejecutar.

Por ejemplo:

```bash
# Entrar al proyecto
cd Parcial/FitCalc

# Instalar dependencias
npm install

# Iniciar Expo
npx expo start
```

Después de iniciar Expo, puedes abrir la aplicación con Expo Go cuando la versión y las dependencias del proyecto sean compatibles, o utilizar un emulador Android.

Para ejecutar otro proyecto, cambia la ruta. Por ejemplo:

```bash
cd Ejercicio_01
npm install
npx expo start
```

---

## Ejecución del servidor de películas

El servidor se encuentra en `Parcial/ConsumeApiMongoDB/Servidor`.

```bash
cd Parcial/ConsumeApiMongoDB/Servidor

node Server.js
```

El servidor requiere las dependencias de Node.js que utiliza el código y una configuración válida de conexión a MongoDB. El endpoint documentado en el proyecto es:

```http
GET /movies
```

---

## Proyectos Android nativos

Las carpetas que contienen proyectos Android nativos pueden abrirse individualmente desde Android Studio.

1. Abrir Android Studio.
2. Seleccionar **Open**.
3. Elegir la carpeta del proyecto deseado dentro de `Ordinario`.
4. Esperar a que Gradle sincronice las dependencias.
5. Seleccionar un emulador o dispositivo Android y ejecutar la aplicación.

---

## Notas

- No existe un único comando de instalación para todo el repositorio: cada proyecto mantiene sus propias dependencias.
- Las versiones de Expo, React y React Native no son iguales en todas las aplicaciones.
- Los proyectos que utilizan servicios externos requieren su configuración correspondiente antes de ejecutarse.
- No publiques credenciales, claves API ni archivos `.env` con información sensible.
