import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
    //    String[][] rosco = cargarDatos("rosco_facil.txt");
        // ⬆⬆⬆⬆⬆ falta el metodo de cargar datos q se encarga persona 1

    //    jugarRosco(rosco);
    //   mostrarResultados(rosco);

    }


    //RECORRE TODO EL ROSCO PA SABER SI HAY PASAPALBRAS O NO
    public static boolean hayPasapalabras(String[][] rosco) {
        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("3")) {
                return true;
            }
        }
        return false;
    }

    //LOGICA DEL JUEGO
    public static void jugarRosco(String[][] rosco) {
        Scanner sc = new Scanner(System.in);
        String respuesta;
        char continuar;

        //PRIMERA VUELTA (donde se hacen las preguntas)
        for (int i = 0; i < 26; i++) {
            //Solo preguntas no planteadas (recuerden que: 0 = no preguntadas, 1 = correctas, 2 = incorrectas, 3 = pasapalabra
            if (rosco[i][3].equals("0")) {
                System.out.println("Letra " + rosco[i][0] + ":" + rosco[i][1]);
                respuesta = sc.nextLine();
                if (respuesta.equalsIgnoreCase("pasapalabra")) {
                    rosco[i][3] = "3";
                } else if (respuesta.equalsIgnoreCase(rosco[i][2])) {
                    rosco[i][3] = "1";
                    System.out.println("Correcto");
                } else {
                    rosco[i][3] = "2";
                    System.out.println("Incorrecto");
                }
            }
        }

        //SEGUNDA VUELTA (aquí se hacen las preguntas donde se respondieron "pasapalabra"
        if (hayPasapalabras(rosco)) {
            System.out.print("¿Desea continuar con las preguntas pendientes? (s/n): ");
            continuar = sc.nextLine().toLowerCase().charAt(0);

            if (continuar == 's') {
                for (int i = 0; i < 26; i++) {
                    if (rosco[i][3].equals("3")) {
                        System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                        respuesta = sc.nextLine();

                        if (respuesta.equalsIgnoreCase("pasapalabra")) {
                            //sigue siendo pasapalabra
                            rosco[i][3] = "3";
                        } else if (respuesta.equalsIgnoreCase(rosco[i][2])) {
                            rosco[i][3] = "1";
                            System.out.println("Correcto");
                        } else {
                            rosco[i][3] = "2";
                            System.out.println("Incorrecto");
                        }
                    }
                }
            }
        }
    }

    //UN PRINT DE LOS RESULTADOS
    public static void mostrarResultados(String[][] rosco){
        int aciertos = 0;
        int fallos = 0;
        int pasapalabras = 0;

        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("1")) {
                aciertos++;
            } else if (rosco[i][3].equals("2")) {
                fallos++;
            } else if (rosco[i][3].equals("3")) {
                pasapalabras++;
            }
        }

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Aciertos: " + aciertos);
        System.out.println("Fallos: " + fallos);
        System.out.println("Pasapalabras:" +pasapalabras);
    }
}

