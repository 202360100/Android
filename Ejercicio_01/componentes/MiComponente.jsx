import { View, Text, StyleSheet } from "react-native";

const MiComponente = () => {
    return(
        <View>
            <Text style = {styles.color_texto}>Hola Mundo pero desde MiComponente</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    color_texto: {
        color: 'green',
    } 
})

export default MiComponente;