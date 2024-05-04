package controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import modelo.Proceso;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class VistaProcesoController implements Initializable {

    @FXML
    private TextField txtPesoSO;
    @FXML
    private TextField txtTamanoMemoria;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtTamano;
    @FXML
    private TextField txtLlegada;
    @FXML
    private TextField txtDuracion;
    @FXML
    private TableView<Proceso> tblProcesos;
    @FXML
    private TableColumn<Proceso, String> colNombre;
    @FXML
    private TableColumn<Proceso, Integer> colTamano;
    @FXML
    private TableColumn<Proceso, Integer> colLlegada;
    @FXML
    private TableColumn<Proceso, Integer> colDuracion;
    @FXML

    private ListView<String> listViewInstantes;


    private boolean memoriaCreada = false;
    private ObservableList<Proceso> listaProcesos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaProcesos = FXCollections.observableArrayList();
        tblProcesos.setItems(listaProcesos);

        // Configurar las celdas de la tabla para mostrar los datos de Proceso
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colTamano.setCellValueFactory(cellData -> cellData.getValue().tamanoProperty().asObject());
        colLlegada.setCellValueFactory(cellData -> cellData.getValue().llegadaProperty().asObject());
        colDuracion.setCellValueFactory(cellData -> cellData.getValue().duracionProperty().asObject());
    }

    @FXML
    private void click_insertar_proceso(ActionEvent event) {
        try {
            // Obtener los valores de los campos de texto
            String nombre = txtNombre.getText();
            int tamano = Integer.parseInt(txtTamano.getText());
            int llegada = Integer.parseInt(txtLlegada.getText());
            int duracion = Integer.parseInt(txtDuracion.getText());

            // Validar si hay suficiente espacio en la memoria para agregar el proceso
            int espacioDisponible = obtenerEspacioDisponible();
            if (tamano > espacioDisponible) {
                mostrarAlerta("Error", "No hay suficiente espacio disponible en la memoria.");
                return; // Salir del método si no hay suficiente espacio
            }

            // Crear un nuevo proceso con los valores obtenidos
            Proceso proceso = new Proceso(nombre, tamano, llegada, duracion);

            // Agregar el proceso a la lista de procesos
            listaProcesos.add(proceso);

            // Limpiar los campos de texto
            limpiarCamposTexto();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese valores numéricos válidos para tamaño, llegada y duración.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private int obtenerEspacioDisponible() {
        int tamanoMemoria = Integer.parseInt(txtTamanoMemoria.getText());
        int espacioUtilizado = 0;
        for (Proceso proceso : listaProcesos) {
            espacioUtilizado += proceso.getTamano();
        }
        return tamanoMemoria - espacioUtilizado;
    }

    private void limpiarCamposTexto() {
        txtNombre.clear();
        txtTamano.clear();
        txtLlegada.clear();
        txtDuracion.clear();
    }

    @FXML
    private void click_crear_memoria(ActionEvent event) {
        if (!memoriaCreada) {
            try {
                // Obtener los valores de los campos de texto para la memoria total y la del SO
                int tamanoMemoria = Integer.parseInt(txtTamanoMemoria.getText());
                int pesoSO = Integer.parseInt(txtPesoSO.getText());

                // Marcar que la memoria ha sido creada
                memoriaCreada = true;

                // Bloquear los campos de texto relacionados con la memoria
                txtTamanoMemoria.setEditable(false);
                txtPesoSO.setEditable(false);

                // Insertar automáticamente el proceso del sistema operativo en la tabla
                Proceso procesoSO = new Proceso("Sistema Operativo", pesoSO, 0, 0); // Por ahora, llegada y duración son 0
                listaProcesos.add(procesoSO);

            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingrese valores numéricos válidos para la memoria total y el peso del SO.");
            }
        } else {
            System.out.println("La memoria ya ha sido creada.");
        }
    }

    @FXML
    private void click_comenzar_simulacion(ActionEvent event) {
        generarSecuenciaInstantes();
    }

    private void generarSecuenciaInstantes() {
        // Limpiar el ListView
        listViewInstantes.getItems().clear();

        // Obtener el tamaño total de la memoria
        int tamanoMemoria = Integer.parseInt(txtTamanoMemoria.getText());

        // Obtener el peso del sistema operativo
        int pesoSO = Integer.parseInt(txtPesoSO.getText());

        // Calcular el espacio libre inicial (tamaño total - peso del sistema operativo)
        int espacioLibre = tamanoMemoria - pesoSO;

        // Agregar el proceso del sistema operativo en la bitácora
        listViewInstantes.getItems().add("Instante 0");
        listViewInstantes.getItems().add("SO " + pesoSO);
        listViewInstantes.getItems().add("Libre " + espacioLibre);

        // Agregar el proceso del sistema operativo a la lista de procesos
        Proceso procesoSO = new Proceso("Sistema Operativo", pesoSO, 0, 0);
        listaProcesos.add(procesoSO);

        // Registrar los instantes de los otros procesos
        for (int i = 0; i < listaProcesos.size(); i++) {
            Proceso proceso = listaProcesos.get(i);
            int llegadaProceso = proceso.getLlegada();
            int duracionProceso = proceso.getDuracion();
            String nombreProceso = proceso.getNombre();
            int tamanoProceso = proceso.getTamano();

            // Agregar el proceso en los instantes de llegada y mientras dure su ejecución
            for (int j = llegadaProceso; j < llegadaProceso + duracionProceso; j++) {
                listViewInstantes.getItems().add("Instante " + j);

                // Verificar si el proceso está entrando o saliendo en este instante
                if (j == llegadaProceso) {
                    // El proceso está entrando
                    listViewInstantes.getItems().add(nombreProceso + " " + tamanoProceso);
                    espacioLibre -= tamanoProceso;
                } else if (j == llegadaProceso + duracionProceso - 1) {
                    // El proceso está saliendo
                    listViewInstantes.getItems().add(nombreProceso + " (salida)");
                    espacioLibre += tamanoProceso;
                }

                // Agregar el estado de la memoria en este instante
                listViewInstantes.getItems().add("Libre " + espacioLibre);
            }
        }
    }


    @FXML
    private void click_generar_bitacora(ActionEvent event) {
        try {
            generarBitacora();
            mostrarAlerta("Bitácora Generada", "Se ha generado la bitácora exitosamente.");
        } catch (IOException e) {
            mostrarAlerta("Error", "Hubo un problema al generar la bitácora.");
            e.printStackTrace();
        }
    }


    private void generarBitacora() throws IOException {
        // Crear un archivo de texto para la bitácora
        String nombreArchivo = "bitacora.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo));

        // Escribir los instantes en el archivo de bitácora
        for (String instante : listViewInstantes.getItems()) {
            writer.write(instante);
            writer.newLine(); // Agregar una nueva línea para separar los instantes
        }

        // Cerrar el escritor
        writer.close();
    }
}
