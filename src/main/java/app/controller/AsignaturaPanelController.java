package app.controller;

import bdd.AsignaturaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import modelo.Asignatura;

import java.sql.SQLException;
import java.util.Optional;


public class AsignaturaPanelController {


    @FXML
    private TableView<Asignatura> tablaAsignaturas;

    @FXML
    private TableColumn<Asignatura,String> colCodigo;

    @FXML
    private TableColumn<Asignatura,String> colNombre;

    @FXML
    private TableColumn<Asignatura,Integer> colCreditos;

    @FXML
    private TableColumn<Asignatura,Integer> colHorasTeoricas;

    @FXML
    private TableColumn<Asignatura,Integer> colHorasPracticas;


    private final AsignaturaDAO dao = new AsignaturaDAO();


    private final ObservableList<Asignatura> datos =
            FXCollections.observableArrayList();



    @FXML
    private void initialize(){

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colCreditos.setCellValueFactory(
                new PropertyValueFactory<>("creditos")
        );

        colHorasTeoricas.setCellValueFactory(
                new PropertyValueFactory<>("horasTeoricas")
        );

        colHorasPracticas.setCellValueFactory(
                new PropertyValueFactory<>("horasPracticas")
        );


        tablaAsignaturas.setItems(datos);

        cargarDatos();
    }



    private void cargarDatos(){

        try{

            datos.setAll(
                    dao.listarTodos()
            );

        }catch(SQLException e){

            error(e.getMessage());

        }

    }




    @FXML
    private void onAnadir(){

        Optional<Asignatura> resultado =
                formulario(null);


        resultado.ifPresent(a -> {

            try{

                dao.insertar(a);
                cargarDatos();

            }catch(SQLException e){

                error(e.getMessage());

            }

        });

    }





    @FXML
    private void onModificar(){

        Asignatura seleccionada =
                tablaAsignaturas.getSelectionModel()
                        .getSelectedItem();


        if(seleccionada == null){

            aviso("Seleccione una asignatura");
            return;

        }


        formulario(seleccionada)
                .ifPresent(a -> {

                    try{

                        dao.actualizar(a);
                        cargarDatos();

                    }catch(SQLException e){

                        error(e.getMessage());

                    }

                });

    }





    @FXML
    private void onEliminar(){

        Asignatura seleccionada =
                tablaAsignaturas.getSelectionModel()
                        .getSelectedItem();


        if(seleccionada == null){

            aviso("Seleccione una asignatura");
            return;

        }


        try{

            dao.eliminar(
                    seleccionada.getCodigo()
            );

            cargarDatos();


        }catch(SQLException e){

            error(e.getMessage());

        }

    }






    private Optional<Asignatura> formulario(Asignatura existente){

        Dialog<Asignatura> dialog =
                new Dialog<>();


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );


        TextField codigo =
                new TextField(
                        existente==null ? "" :
                                existente.getCodigo()
                );


        TextField nombre =
                new TextField(
                        existente==null ? "" :
                                existente.getNombre()
                );


        TextField creditos =
                new TextField(
                        existente==null ? "" :
                                existente.getCreditos()+""
                );


        TextField teoricas =
                new TextField(
                        existente==null ? "" :
                                existente.getHorasTeoricas()+""
                );


        TextField practicas =
                new TextField(
                        existente==null ? "" :
                                existente.getHorasPracticas()+""
                );


        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(
                new Insets(20)
        );


        grid.add(new Label("Código"),0,0);
        grid.add(codigo,1,0);

        grid.add(new Label("Nombre"),0,1);
        grid.add(nombre,1,1);

        grid.add(new Label("Créditos"),0,2);
        grid.add(creditos,1,2);

        grid.add(new Label("Horas Teóricas"),0,3);
        grid.add(teoricas,1,3);

        grid.add(new Label("Horas Prácticas"),0,4);
        grid.add(practicas,1,4);



        dialog.getDialogPane()
                .setContent(grid);



        dialog.setResultConverter(btn -> {

            if(btn == ButtonType.OK){

                return new Asignatura(
                        codigo.getText(),
                        nombre.getText(),
                        Integer.parseInt(creditos.getText()),
                        Integer.parseInt(teoricas.getText()),
                        Integer.parseInt(practicas.getText())
                );

            }

            return null;

        });



        return dialog.showAndWait();

    }





    private void aviso(String m){

        new Alert(
                Alert.AlertType.INFORMATION,
                m
        ).showAndWait();

    }



    private void error(String m){

        new Alert(
                Alert.AlertType.ERROR,
                m
        ).showAndWait();

    }

}