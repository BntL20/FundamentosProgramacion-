import java.util.Scanner;
public class Usuario {
    public String nombre;
    public int edad;
    public String correo;

    public Usuario(String nombre, int edad, String correo) {
        this.nombre = nombre;
        this.edad = edad;
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getCorreo() {
        return correo;
    }
    public static boolean validarEdad(int edad){
        return edad >=0;
    }
    public static boolean validarCorreo(String correo){
        return correo.contains("@")&& correo.contains(".");
    }
    public static Usuario registrarUsuario(){
        Scanner in = new Scanner(System.in);
        String nombre;
        int edad;
        String correo;

      System.out.println("Ingrese su nombre:");
      nombre= in.nextLine();
      do {
          System.out.println("Ingrese su edad");
          edad = in.nextInt();
      } while (!validarEdad(edad));
      in.nextLine();

      do {
          System.out.println("Ingrese su correo: ");
          correo = in.nextLine();
      } while(!validarCorreo(correo));
      return new Usuario(nombre,edad, correo);
      }
    }


