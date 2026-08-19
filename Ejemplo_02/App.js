import { useState } from 'react';
import { View, Text, TextInput, Button, ScrollView, StyleSheet, Dimensions } from 'react-native';
import FlagComponent from './componentes/FlagComponent';


export default function App() {

  const [texto, setTexto] = useState()
  const [enviar, setEnviar] = useState()

  return (
    <View style={styles.container}>
      <View style={styles.panel1}></View>
      <View style={styles.panel2}>
         <ScrollView style = {styles.input}>
            <Text>Inicial {enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>{enviar}</Text>
            <Text>Final {enviar}</Text>
          </ScrollView>
      </View>
      <View style={styles.panel3}>
        <TextInput
          placeholder="Escribe aqui..."
          onChangeText={ t => setTexto(t) }
        />
        <Button
          title='Enviar'
          onPress={ () => setEnviar(texto) }
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: '#030303'
  },
  panel1: {
    flex: 1,
    backgroundColor: "#18a724"
  },
  panel2: {
    flex: 1,
    backgroundColor: "#fff"
  },
  panel3: {
    flex: 1,
    backgroundColor: "#ce1919"
  },
});
