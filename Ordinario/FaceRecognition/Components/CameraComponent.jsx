import React, { useRef, useState } from 'react';
import { Button, Image, StyleSheet, Text, View } from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';
import { File, Paths } from 'expo-file-system';

export default function CameraComponent() {
  const [permission, requestPermission] = useCameraPermissions();
  const [photo, setPhoto] = useState(null);

  const cameraRef = useRef(null);

  if (!permission) {
    return (
      <View style={styles.container}>
        <Text>Solicitando permiso para usar la cámara...</Text>
      </View>
    );
  }

  if (!permission.granted) {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>
          Necesitamos permiso para utilizar la cámara.
        </Text>

        <Button
          title="Dar permiso"
          onPress={requestPermission}
        />
      </View>
    );
  }

  const takePicture = async () => {
    if (cameraRef.current) {
      const picture = await cameraRef.current.takePictureAsync();

      // Archivo temporal generado por la cámara
      const temporaryFile = new File(picture.uri);

      // Nombre que tendrá nuestra copia local
      const savedFile = new File(
        Paths.document,
        `foto-${Date.now()}.jpg`
      );

      // Copiamos la fotografía al almacenamiento permanente
      temporaryFile.copy(savedFile);

      // Mostramos la fotografía guardada
      setPhoto(savedFile.uri);

      console.log('Foto guardada en:', savedFile.uri);
    }
  };

  return (
    <View style={styles.container}>

      {!photo ? (
        <>
          <CameraView
            ref={cameraRef}
            style={styles.camera}
          />

          <Button
            title="Tomar fotografía"
            onPress={takePicture}
          />
        </>
      ) : (
        <>
          <Image
            source={{ uri: photo }}
            style={styles.photo}
          />

          <Button
            title="Tomar otra fotografía"
            onPress={() => setPhoto(null)}
          />
        </>
      )}

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },

  camera: {
    width: '100%',
    height: 500,
  },

  photo: {
    width: '100%',
    height: 500,
    resizeMode: 'cover',
  },

  text: {
    textAlign: 'center',
    marginBottom: 20,
  },
});

