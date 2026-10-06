import AsyncStorage from '@react-native-async-storage/async-storage';

const STORAGE_KEY = '@battle_simulator_simulations';

export async function saveSimulation(simulation) {
  try {
    const existingData =
      await AsyncStorage.getItem(
        STORAGE_KEY
      );

    const simulations =
      existingData
        ? JSON.parse(existingData)
        : [];

    const newSimulation = {
      id: Date.now().toString(),
      ...simulation,
    };

    const updatedSimulations = [
      newSimulation,
      ...simulations,
    ];

    await AsyncStorage.setItem(
      STORAGE_KEY,
      JSON.stringify(
        updatedSimulations
      )
    );

    return newSimulation;
  } catch (error) {
    console.error(
      'Error al guardar la simulación:',
      error
    );

    return null;
  }
}

export async function getSimulations() {
  try {
    const data =
      await AsyncStorage.getItem(
        STORAGE_KEY
      );

    if (!data) {
      return [];
    }

    return JSON.parse(data);
  } catch (error) {
    console.error(
      'Error al obtener las simulaciones:',
      error
    );

    return [];
  }
}

export async function deleteSimulation(
  simulationId
) {
  try {
    const data =
      await AsyncStorage.getItem(
        STORAGE_KEY
      );

    const simulations =
      data
        ? JSON.parse(data)
        : [];

    const updatedSimulations =
      simulations.filter(
        (simulation) =>
          simulation.id !==
          simulationId
      );

    await AsyncStorage.setItem(
      STORAGE_KEY,
      JSON.stringify(
        updatedSimulations
      )
    );

    return true;
  } catch (error) {
    console.error(
      'Error al eliminar la simulación:',
      error
    );

    return false;
  }
}

export async function clearSimulations() {
  try {
    await AsyncStorage.removeItem(
      STORAGE_KEY
    );

    return true;
  } catch (error) {
    console.error(
      'Error al eliminar las simulaciones:',
      error
    );

    return false;
  }
}
