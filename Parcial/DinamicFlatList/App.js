import { StyleSheet, Text, View, FlatList, TouchableOpacity } from 'react-native';
import { useState } from 'react';
import CustomModal from './componentes/CustomModal';

export default function App() {
  const cursos = [
    {id: "1", titulo: "Aplicaciones moviles", duracion: "20 horas", rating: "5.0"},
    {id: "2", titulo: "Bases de datos", duracion: "30 horas", rating: "4.0"},
    {id: "3", titulo: "Diseño de redes", duracion: "40 horas", rating: "3.0"},
    {id: "4", titulo: "Arquitectura orientada a servicios", duracion: "50 horas", rating: "2.0"},
    {id: "5", titulo: "Mate", duracion: "60 horas", rating: "4.0"},
  ]

  const [modalVisible, setModalVisible] = useState(false)
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null)
  
  const manejaPresionCurso = (tituloCurso) => {
    setCursoSeleccionado({ valor:tituloCurso });
    setModalVisible(true)
  }
  
  const renderCard = ({item}) => (
    <TouchableOpacity
      style = {styles.card}
      onPress = { () => manejaPresionCurso(item.titulo)}
      activeOpacity = {0.7}
    >
      <View>
        <Text style = {styles.title}> {item.titulo}</Text>
        <Text style = {styles.subtitulo}> {item.duracion} | {item.rating} </Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <Text style = {styles.header}>Mi lista de cursos</Text>
      <FlatList
        data = {cursos}
        renderItem = {renderCard}
        keyExtractor = {item => item.id}
        contentContainerStyle = {styles.listContainer} 
      />
      <CustomModal
        visible = {modalVisible}
        onClose = {setModalVisible(false)}
        contenido = {cursoSeleccionado}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'rgb(15, 10, 10)000'
  },
  listContainer: {
    paddingHorizontal: 16,
    paddingBottom: 16
  },
  header: {
    fontSize: 24,
    fontWeight: "bold",
    padding: 16,
    color: "#ad1111"
  },
  card: {
    backgroundColor: "#000",
    padding: 16,
    marginVertical: 8,
    borderRadius: 12,
  },
  title: {
    fontSize: 18,
    fontWeight: "bold",
    color: "rgba(33, 182, 33, 0.67)",
    marginBottom: 4
  },
  subtitulo: {
    fontSize: 14,
    color: "#5bc431"
  }
});
