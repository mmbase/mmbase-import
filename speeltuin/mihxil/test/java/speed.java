
/**
 * Tries if it makes a difference to use a member variable to store return values.

*/


class A {

    private int hits = 0;
    private Object rtn;

    public Object geta(String a) {
        hits++;
        rtn = a + "a";
        return a;
    }


    public Object getb(String a) {
        hits++; // make sure function cannot be optimized away
        Object r = a + "b";
        return r;
    }

    public Object getc(String a) {
        hits++;
        return  a + "c";
    }


    public static void main (String[] args) {
        long number = 6000000;
        A a = new A();
        long start = System.currentTimeMillis();
        for (long i =0 ; i < number; i++) {
            a.geta("hoi");            
        }
        System.out.println("a: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (long i =0 ; i < number; i++) {
            a.getb("hoi");            
        }
        System.out.println("b: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (long i =0 ; i < number; i++) {
            a.getc("hoi");            
        }
        System.out.println("c: " + (System.currentTimeMillis() - start) + " ms");

        // another time..
        start = System.currentTimeMillis();
        for (long i =0 ; i < number; i++) {
            a.geta("hoi");            
        }
        System.out.println("a: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (long i =0 ; i < number; i++) {
            a.getb("hoi");            
        }
        System.out.println("b: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();
        for (long i =0 ; i < number; i++) {
            a.getc("hoi");            
        }
        System.out.println("c: " + (System.currentTimeMillis() - start) + " ms");


    } // end of main ()
    
    
}
