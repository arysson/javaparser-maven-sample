class MyClass {
	int getValue() {
		return 123;
	}
}

public abstract class Blabla {
	
	int[] getArray() {
		return new int[0];
	}
	
	Object getObject() {
		return new Object();
	}
	
	static boolean getBoolean(String str) {
		return str.equals("blabla");
	}
	
	char getChar() {
		return 'b';
	}
	
	static int getInt() {
		return 1;
	}
	
	abstract int getInt2();

    private final void method1013(MyClass mc, int x) {
        System.out.println("method1013");
    }
    
    public static void main(String[] args) {
    	assert(getInt() == 1);
    	assert(getBoolean("blabla"));
    }
}