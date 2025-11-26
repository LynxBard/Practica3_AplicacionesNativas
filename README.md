-----

# Gestor de Archivos Para Android

Una aplicación nativa de Android desarrollada en Kotlin utilizando **Jetpack Compose**. Esta herramienta funciona como un explorador de archivos completo que permite navegar, gestionar y visualizar diferentes tipos de documentos en el almacenamiento del dispositivo, con soporte avanzado para temas personalizados.

## 📱 Características Principales

### 📂 Gestión de Archivos

  * **Navegación Completa:** Explora directorios del almacenamiento interno y externo.
  * **Operaciones Básicas:** Crear carpetas, renombrar archivos y eliminar elementos.
  * **Acceso Rápido:** Sistema de **Favoritos** y lista de **Archivos Recientes**.
  * **Permisos Inteligentes:** Soporte compatible desde Android 10 (Legacy Storage) hasta Android 14+ (Manage External Storage).

### 👁️ Visualizadores Integrados

  * **Visor de Imágenes:** Soporte para gestos (Zoom y Pan) y rotación de imágenes.
  * **Visor de Texto:** Lectura de archivos planos `.txt`, `.md`, etc.
  * **Formateador de Código:** Visualización con formato automático (pretty-print) para archivos **JSON** y **XML**.

### 🎨 Personalización y Temas

La aplicación cuenta con un motor de temas dinámico que persiste las preferencias del usuario:

  * **Paletas de Colores:**
      * 🟣 **Default:** Material Design 3 estándar.
      * 🍒 **Guinda:** Tema inspirado en colores institucionales (IPN).
      * 🔵 **Azul:** Tema inspirado en ESCOM.
  * **Modos de Apariencia:**
      * ☀️ Claro (Light)
      * 🌑 Oscuro (Dark)
      * ⚙️ Sistema (Sigue la configuración del dispositivo)

## 🛠️ Tecnologías Utilizadas

  * **Lenguaje:** [Kotlin](https://kotlinlang.org/)
  * **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
  * **Arquitectura:** MVVM (Model-View-ViewModel)
  * **Diseño:** Material Design 3
  * **Asincronía:** Corrutinas de Kotlin y Flow
  * **Navegación:** Compose Navigation

## 📋 Requisitos Previos

  * Android Studio Ladybug o superior.
  * JDK 11 o superior.
  * Dispositivo o Emulador con Android 7.0 (API 24) mínimo.
      * *Nota: Para probar todas las funciones de permisos se recomienda API 29+.*

## 🚀 Instalación y Ejecución

1.  Clona este repositorio:
    ```bash
    git clone https://github.com/tu-usuario/Practica3_AplicacionesNativas.git
    ```
2.  Abre el proyecto en **Android Studio**.
3.  Espera a que Gradle sincronice las dependencias.
4.  Ejecuta la aplicación (`Shift + F10`) en tu emulador o dispositivo físico.

## 📂 Estructura del Proyecto

```text
com.example.p3_aplicacionesnativas
├── data          # Persistencia (FavoritesManager, SettingsManager)
├── model         # Modelos de datos (FileItem)
├── ui
│   ├── components # Componentes reutilizables (Dialogs, FileListItems)
│   ├── screens    # Pantallas (FileManager, ImageViewer, TextViewer)
│   └── theme      # Definición de Temas y Colores
├── utils         # Utilidades (FileOperations)
└── viewmodel     # Lógica de negocio (FileManagerViewModel)
```

## 🔒 Permisos

La aplicación solicita los siguientes permisos para funcionar correctamente:

  * `READ_EXTERNAL_STORAGE`
  * `WRITE_EXTERNAL_STORAGE`
  * `MANAGE_EXTERNAL_STORAGE` (Para Android 11+)

-----

