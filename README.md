# Índice del Repositorio - Android

**Alumno:** Mario Alberto Tapia Turrent  
**Matrícula:** 202360100  
**Curso:** Desarrollo de aplicaciones móviles

---

## Tecnologías utilizadas

- JavaScript (ES6) y JSX
- React 19 (hooks: `useState`, `useEffect`, `useRef`, `useContext`)
- React Native con Expo (SDK 54 y SDK 57) y Expo Go
- React Navigation 7 (Stack, Bottom Tabs y Drawer)
- API `Animated` de React Native
- Node.js, Express y MongoDB Atlas (API de películas)

---

## Estructura del repositorio

```
📁 Android-main
│
├── 📁 Ejercicio_01
│   └── 🧩 Componentes y props: MiComponente y Mensaje reutilizable
│
├── 📁 Ejemplo_02
│   └── 🧩 Flexbox, TextInput y ScrollView; DemoChildren y FlagComponent
│
├── 📁 Ejercicio_03
│   └── 🧩 Listas con FlatList y SectionList; ImageBackground e Image
│
├── 📁 Ejercicio_04
│   └── 🧩 Modal reutilizable (CustomModal) con el texto capturado
│
└── 📁 Parcial
    │
    ├── 📁 Animated
    │   └── 🧩 Animación de opacidad, posición y escala en paralelo
    │
    ├── 📁 ConsumeApiMongoDB
    │   └── 🧩 Servidor Express y MongoDB con el endpoint GET /movies
    │
    ├── 📁 DinamicFlatList
    │   └── 🧩 Lista de cursos con FlatList; al tocar uno abre un modal
    │
    ├── 📁 DrawerNavigation
    │   └── 🧩 Menú lateral con 4 pantallas y ejemplo de splash screen
    │
    ├── 📁 FitCalc
    │   └── 🧩 Calculadora de IMC con resultado y mensaje en un modal
    │
    ├── 📁 LifeRpg
    │   └── 🧩 App de rol: dados d20, personajes, enemigos y arena
    │       (Drawer y Tabs, Context API y componentes estilo pixel)
    │
    ├── 📁 StackNavigation
    │   └── 🧩 Navegación en pila: IMC, conversor USD-MXN y propina
    │
    └── 📁 TabNavigation
        └── 🧩 Barra de pestañas inferior: Inicio, Buscar y Perfil
```

---

## Ejecución

```bash
cd Parcial/FitCalc      # entrar a cualquier proyecto
npm install             # instalar dependencias
npx expo start          # abrir con Expo Go o emulador Android
```

Servidor de la API (`Parcial/ConsumeApiMongoDB/Servidor`):

```bash
npm install express mongodb cors
node Server.js
```
