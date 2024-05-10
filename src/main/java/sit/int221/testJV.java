package sit.int221;

public class testJV {
    public static void main(String[] args) {
        System.out.println("".trim().length() == 0);
        String string1 = "using equals method";
        String string2 = "using equals method";

        String string3 = "using EQUALS method";
        String string4 = new String("using equals method");

        System.out.println(string1.equals(string3));
    }
}
