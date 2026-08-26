import React from "react";
import { useState } from "react";
import { Text, StyleSheet, Button, View, SafeAreaView, TextInput } from "react-native";
import CustomModal from "./componentes/CustomModal"

export default function App(){
    const [ modalVisible, setModalVisible ] = useState(false);
    const [texto, setTexto] = useState("")
    
    const Presionado = () => {
        setModalVisible(true)
    }

    return(
        <SafeAreaView style = { styles.container }>
            <View>
                <CustomModal
                    visible = { modalVisible }
                    onClose = { () => setModalVisible(false) }
                    contenido = { texto }
                />
                <TextInput
                    placeholder="Escribe aqui..."
                    value = { texto }
                    onChangeText={ t => setTexto(t) }
                />
                <Button
                    title = "Abrir Modal"
                    onPress = { Presionado }
                />
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
    },
});