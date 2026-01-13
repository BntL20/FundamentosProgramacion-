import java.io.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        char jugarOtra;
        do {
            //Registrar o identificar usuario
            Usuario usuarioActual = RegistraroIdentificarUsuario();

            //Elegir nivel
            String nivel = elegirNivel();
            String nombreFichero = obtenerFicheroNivel(nivel); //rosco

            //Cargar datos del rosco según el nivel
            String[][] rosco = cargarDatos(nombreFichero);

            //Lógica del juego
            jugarRosco(rosco);

            //Resultados
            int[] r = calcularResultados(rosco);
            mostrarResultados(rosco);

            String NombreGuardar = "src/Data/MarcadorUsuario.txt";

            //Guardar Resultados
            guardarDatosPartida(NombreGuardar, usuarioActual.correo, r[0], r[1], r[2], nivel);

            //Ver estadísticas
            char verEst;
            do {
                System.out.print("¿Quieres ver las estadísticas generales? (s/n): ");
                verEst = sc.nextLine().toLowerCase().charAt(0);
            } while (verEst != 's' && verEst != 'n');

            if (verEst == 's') {
                historialPartida(NombreGuardar);
                mejorPuntaje(NombreGuardar);
                partidasPorNivel(NombreGuardar);
            }

            //Jugar otra vez
            do {
                System.out.print("¿Deseas jugar otra vez? (s/n): ");
                jugarOtra = sc.nextLine().toLowerCase().charAt(0);
            } while (jugarOtra != 's' && jugarOtra != 'n');

        } while (jugarOtra == 's');

        System.out.println("GRACIAS POR JUGAR. HASTA LUEGO!");
    }

    /**
     * Esta función Registra los usuarios si no están registrados
     * Y si el correo introducido coinciden con MarcadorUsuario.txt
     * Print de Usuario reconocido o Usuario nuevo.
     * @return un usuario nuevo
     * @author Brent
     */
    //REGISTRO Y RECONOCIMIENTO DE USUARIOS
    public static Usuario RegistraroIdentificarUsuario() {
        Scanner sc = new Scanner(System.in);

        // Pedir datos al usuario, independientemente si el usuario exista o no
        System.out.print("Introduce nombre: ");
        String nombre = sc.nextLine();

        //Pedimos la edad
        int edad;
        //Pedimos el correo
        String correo;
        //Comprobamos si el usuario existe usando su correo
        boolean existe = false;

        //verificamos la edad sea correcta
        do {
            System.out.print("Introduce edad: ");
            edad = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            if (edad <= 0) {
                System.out.println("La edad debe ser mayor que 0.");
            }
        } while (edad <= 0);

        //Verificamos el correo que sea correcto
        do {
            System.out.print("Introduce correo: ");
            correo = sc.nextLine();

            // Validación del correo electrónico
            if (!correo.contains("@")) {
                System.out.println("El correo debe contener '@'.");
            }
        } while (!correo.contains("@"));

        //Abrimos el fichero de los marcadores de los Usuarios y verificamos si ese Usuario existe o no (jugo o no)
        try {
            File fichero = new File("src/Data/MarcadorUsuario.txt");
            if (fichero.exists()) { //si el fichero existe se lee
                Scanner file = new Scanner(fichero);
                while (file.hasNextLine()) { // Se recorre el fichero línea a línea
                    String linea = file.nextLine();
                    String[] datos = linea.split(";"); //los datos spliteados se guardan en un array
                    if (datos[0].equalsIgnoreCase(correo)) { // datos[0] contiene el correo que se ha guardado en el fichero
                        existe = true;
                        break; // Se deja de buscar porque ya se ha encontrado
                    }
                }
                file.close();
            }
        } catch (Exception e) {
            System.out.println("Error al leer el fichero de usuarios.");
        }

        //Mensajes que se le da al usuario
        if (existe) { //si existe, reconoce al usuario
            System.out.println("Usuario reconocido. Bienvenido de nuevo " +nombre+ "!");
        } else { //si no existe, identificamos como nuevo usuario
            System.out.println("Usuario nuevo. Bienvenido a 'Juguemos al pasapalabras'");
        }

        return new Usuario(nombre, edad, correo);
    }

    /**
     * Solicita al usuario que elija un nivel válido y lo devuelve.
     * La función no finaliza hasta que se introduce un nivel correcto.
     *
     * @return nivel elegido: infantil, facil, medio o avanzado
     * @author Brent
     */
    //ELECCIÓN DE NIVEL
    public static String elegirNivel(){
        Scanner sc = new Scanner(System.in);
        String nivel;
        do { //validamos que el usuario escoja un nivel
            System.out.println("Elige nivel: infantil / facil / medio / avanzado");
            nivel = sc.nextLine().toLowerCase();

            if(!nivel.equals("infantil") && !nivel.equals("facil") && !nivel.equals("medio") && !nivel.equals("avanzado")){ //damos el mensaje que escoja un nivel correcto
                System.out.println("Error de elección de nivel. Coloque uno de los niveles indicados");
            }
        }while (!nivel.equals("infantil") && !nivel.equals("facil") && !nivel.equals("medio") && !nivel.equals("avanzado"));

        return nivel;
    }

    /**
     * Devuelve la ruta del fichero correspondiente al nivel elegido
     * @param nivel del juego
     * @return ruta del fichero asociado al nivel
     * @author Brent
     */
    //DEVUELVE EL FICHERO CON EL NIVEL ELEGIDO
    public static String obtenerFicheroNivel(String nivel){
        return "src/Data/rosco_"+nivel+".txt";
    }


    /**
     * Esta función carga los ficheros de los niveles en el programa para poder trabajar con ellos.
     * @param nombreFichero fichero del nivel elegido por el usuario.
     * @return devuelve el rosco completo y con las 26 preguntas elegidas.
     * @author Sofia
     */
    //CARGAR DATOS
    public static String[][] cargarDatos(String nombreFichero){

        String [][] rosco = new String [26][4]; //matriz que guarda la letra, la pregunta, la respuesta y el estado.

        //Inicializamos el estado de las preguntas para que siempre empiecen en 0
        for (int i = 0;i<26;i++){
            rosco [i][3] = "0";
        }

        //PRIMERA LECTURA DEL FICHERO
        try {
            //Contamos cuántas palabras hay por letra
            int [] contadorPorLetra = new int [26];
            BufferedReader br = new BufferedReader(new FileReader(nombreFichero));
            String linea;
            //Este while lee la línea del fichero hasta que esté vacía = fichero acabado
            while ((linea = br.readLine()) != null){
                char letra = linea.charAt (0); //sacamos la primera letra de la línea
                int indice = letra - 'A';//y convertimos las letras en números para que representen el índice
                if(indice >= 0 && indice < 26) {
                    contadorPorLetra[indice]++;  //sumamos 1 a la letra del índice porque ha encontrado una pregunta más
                }
            }
            br.close(); //cerramos el fichero

            //Elegir una posición aleatoria para cada letra
            int [] aleatorioPorLetra = new int [26]; //aquí guardamos qué pregunta queremos elegir de cada letra (solo 1 por letra)
            for (int i = 0; i<26;i++){
                aleatorioPorLetra[i] = (int) (Math.random() * contadorPorLetra [i]); /*Con Math.random() obtenemos un número entre 0 y 1.
                                                                                     Al multiplicarlo por contadorPorLetra pedimos un número aleatorio entre 0 y el índice
                                                                                     Con (int) quitamos los decimales*/
            }

        //SEGUNDA LECTURA DEL FICHERO
            //Volver a leer el fichero y guardar la elegida
            int[] contadorActual = new int [26]; //Almacena cuantas preguntas hemos leído ya de cada letra
            br = new BufferedReader (new FileReader (nombreFichero));//abrimos el fichero otra vez
            while ((linea = br.readLine()) != null){ //mientras haya líneas en el fichero lo lee línea a línea
                String [] partes = linea.split(";"); /*separa la línea en partes cada vez que encuentra un;
                                                            partes [0] = letra
                                                            partes [1] = pregunta
                                                            partes [2] = respuesta*/
                char letra = partes [0].charAt(0);
                int indice = letra - 'A';
                if(indice >= 0 && indice < 26) {
                    if (contadorActual[indice] == (aleatorioPorLetra[indice])) { //elige aleatoriamente la pregunta de la letra.
                        rosco[indice][0] = partes[0]; //guardamos la letra
                        rosco[indice][1] = partes[1]; //guardamos la pregunta
                        rosco[indice][2] = partes[2]; //guardamos la respuesta
                    }
                    contadorActual[indice]++; //sumamos 1 porque hemos leído otra pregunta de esa letra
                }
            }
            br.close();//cerramos el fichero

        }catch(IOException e){
            System.out.println ("Error leyendo el fichero"); //si no funciona el try que imprima ese error
        }
        return rosco; //devolvemos la matriz completa y lista
    }

    /**
     * Ejecuta la lógica principal del juego.
     * Realiza una primera ronda de preguntas y una segunda ronda para aquellas letras marcadas como "pasapalabra".
     * El estado de cada letra se actualiza directamente en el array rosco.
     *
     * @param rosco array que contiene la letra, la pregunta, la respuesta correcta y el estado de cada posición del rosco
     * @author Brent
     */

    //LOGICA DEL JUEGO
    public static void jugarRosco(String[][] rosco) {
        Scanner sc = new Scanner(System.in);
        String respuesta;
        char continuar;

        //PRIMERA VUELTA (donde se hacen las preguntas)
        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("0")) { //Solo preguntas no planteadas (0)
                System.out.println("Letra " + rosco[i][0] + ":" + rosco[i][1]);
                respuesta = sc.nextLine().trim();
                if (respuesta.equalsIgnoreCase("pasapalabra")) { //si la respuesta fue pasapalabra
                    rosco[i][3] = "3"; //se rellena con 3 = pasapalabra
                } else if (respuesta.equalsIgnoreCase(rosco[i][2])) { //si la respuesta es igual a la respuesta correcta
                    rosco[i][3] = "1"; //se rellena con 1 = correcta
                    System.out.println("Correcto");
                } else {
                    rosco[i][3] = "2";//se rellena con 2 = incorrecta
                    System.out.println("Incorrecto");
                }
            }
        }


        //SEGUNDA VUELTA (aquí se hacen las preguntas donde se respondieron "pasapalabra")
        if (hayPasapalabras(rosco)) { //si hay
            System.out.print("¿Desea continuar con las preguntas pendientes? (s/n): ");
            continuar = sc.nextLine().toLowerCase().charAt(0);

            if (continuar == 's') {
                for (int i = 0; i < 26; i++) {
                    if (rosco[i][3].equals("3")) {
                        System.out.println("Letra " + rosco[i][0] + ": " + rosco[i][1]);
                        respuesta = sc.nextLine().trim();

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

    /**
     * Un booleano que retorna si hay pasapalabras al recorrer todas las preguntas del rosco
     * @param rosco recorre el rosco
     * @return si o no hay pasapalabras
     * @author Brent
     */

    //Recorre todo el rosco para saber si hay pasapalabras
    public static boolean hayPasapalabras(String[][] rosco) {
        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("3")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Un print de los resultados del rosco jugado, sacando los datos de un array
     * @param rosco recibe el rosco jugado, calcula los resultados y los imprime
     * @author brent
     */
    //MOSTRAR RESULTADOS (reutilizando los datos de calcularResultados)
    public static void mostrarResultados(String[][] rosco) {
        int[] r = calcularResultados(rosco);

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Aciertos: " + r[0]);
        System.out.println("Fallos: " + r[1]);
        System.out.println("Pasapalabras: " + r[2]);
    }

    /**
     * Calcula los resultados del rosco jugado en contadores de aciertos, fallos y pasapalabras, lo guarda en un array y lo retorna
     * @param rosco obtiene los datos necesarios del rosco jugado
     * @return un array con los contadores
     * @author brent
     */
    //CALCULAR RESULTADOS
    public static int[] calcularResultados(String[][] rosco) {
        int aciertos = 0;
        int fallos = 0;
        int pasapalabras = 0;

        for (int i = 0; i < 26; i++) {
            if (rosco[i][3].equals("1")){
                aciertos++;
            }
            else if (rosco[i][3].equals("2")){
                fallos++;
            }
            else if (rosco[i][3].equals("3")){
                pasapalabras++;
            }
        }
        return new int[]{aciertos, fallos, pasapalabras};
    }

    /**
     * Esta función recibe el fichero MarcadorUsuario y escribe en él el correo, los aciertos, fallos, pasapalabras y el nivel.
     * @param NombreGuardar fichero donde se va a escribir los datos del usuario
     * @param correoUsuario correo que introduce el usuario
     * @param aciertos preguntas respondidas correctamente
     * @param fallos preguntas respondidas incorrectamente
     * @param pasapalabras preguntas no respondidas
     * @param nivel el nivel que ha elegido el usuario
     * @author Sofia
     */
    //GUARDAR PARTIDA
    public static void guardarDatosPartida(String NombreGuardar, String correoUsuario, int aciertos, int fallos, int pasapalabras, String nivel){
        try {
            BufferedWriter bw = new BufferedWriter (new FileWriter (NombreGuardar, true));
            bw.write (correoUsuario + ";" + aciertos + ";" + fallos + ";" + pasapalabras + ";" + nivel);
            bw.newLine();
            bw.close();
            System.out.println("Partida guardada");
        }

        catch (IOException e){
            System.out.println ("Error al escribir el fichero");
        }

    }

    /**
     *
     * Muestra por pantalla el historial completo de partidas almacenado en un fichero
     *
     * @param fichero nombre del fichero donde se almacenan las estadisticas de las partidas
     * @author danna
     */
    //ESTADISTICAS FINALES DEL JUEGO
    public static void historialPartida (String fichero){
        System.out.println("=====ESTADÍSTICAS=====");
        try{
            Scanner file = new Scanner (new File(fichero)); // Crea un Scanner para leer el fichero indicado
            System.out.println("==Historial de partidas==");

            while(file.hasNextLine()){  // Mientras existan líneas por leer en el fichero
                String linea = file.nextLine();
                String []datos = linea.split(";");// Divide la línea en partes usando ';' como separador

                System.out.println("Correo: " + datos[0]);  // Muestra el correo del jugador
                System.out.println("Aciertos: " + datos[1]); // Muestra el número de aciertos
                System.out.println("Fallos: " + datos[2]); // Muestra el número de fallos
                System.out.println("Pasapalabras: " + datos[3]);// Muestra el número de pasapalabras
                System.out.println("Nivel: " + datos[4]);// Muestra el nivel jugado
                System.out.println("---------------------------------");
            }
            file.close();
        }
        catch (FileNotFoundException e){
            // Mensaje si el fichero no existe o no se puede abrir
            System.out.println("No hay estadisticas registradas");
        }
    }

    /**
     *
     * Calcula y muestra la mejor puntuación registrada a partir de los datos almacenados en el fichero
     *
     * @param fichero nombre del fichero donde se almacenan las estadisticas de las partidas
     * @author danna
     */
    public static void mejorPuntaje(String fichero){
        int mejor = 0;  // Variable para guardar la mejor puntuación encontrada

        try{
            Scanner file= new Scanner(new File(fichero)); // Crea un Scanner para leer el fichero
            while(file.hasNextLine()){   // Recorre todas las líneas del fichero
                String []datos = file.nextLine().split(";");// Divide la línea en datos usando ';'
                int aciertos = Integer.parseInt(datos[1]);// Convierte el número de aciertos a entero

                if(aciertos > mejor){  // Comprueba si la puntuación actual es mayor que la mejor
                    mejor = aciertos;
                }
            }

            file.close();
            System.out.println("---Mejor puntuacion registrada: "+ mejor + " aciertos---");// Muestra la mejor puntuación encontrada
        }
        catch(Exception e){
            System.out.println("Error al leer las estadisticas"); // Mensaje de error si ocurre algún problema al leer el fichero
        }
    }

    /**
     * Cuenta cuántas partidas se han jugado en cada nivel y muestra el resultado por pantalla
     *
     *
     * @param nombreFichero nombre del fichero donde se almacenan las estadisticas de las partidas
     * @author danna
     */

    public static void partidasPorNivel(String nombreFichero){
        // Contadores para cada nivel de dificultad
        int infantil =0;
        int facil=0;
        int medio=0;
        int avanzado=0;

        try{
            Scanner file = new Scanner(new File(nombreFichero)); // Abre el fichero con un Scanner
            while(file.hasNextLine()){  // Recorre todas las líneas del fichero
                String[]datos = file.nextLine().split(";");  // Divide la línea en partes
                String nivel = datos[4].toLowerCase();// Obtiene el nivel y lo pasa a minúsculas

                // Incrementa el contador según el nivel
                if(nivel.equals("infantil")){
                    infantil++;
                }
                else if(nivel.equals("facil")){
                    facil++;
                }
                else if(nivel.equals("medio")){
                    medio++;
                }
                else if(nivel.equals("avanzado")){
                    avanzado++;
                }
            }
            file.close();

            // Muestra las estadísticas por nivel
            System.out.println("==Partidas por nivel==");
            System.out.println("Infantil: "+ infantil);
            System.out.println("Facil: "+ facil);
            System.out.println("Medio: "+ medio);
            System.out.println("Avanzado: "+ avanzado);
        }
        catch(Exception e){
            // Mensaje de error si falla al leer el fichero
            System.out.println("Error al leer el fichero de estadisticas");
        }
    }
}