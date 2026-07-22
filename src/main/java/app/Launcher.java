package app;

/**
 * Clase de arranque separada de MainApp (que extiende Application).
 * JavaFX exige que la clase con el main() ejecutado directamente por la JVM
 * NO sea la misma que extiende Application cuando se corre sin pasar
 * --module-path manualmente; de lo contrario tira
 * "Error: JavaFX runtime components are missing".
 * Con este Launcher, el boton ▶ normal de IntelliJ funciona igual que
 * mvn javafx:run.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}