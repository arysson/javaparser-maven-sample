
class MyClass {
	public static void main(String[] args) {
		System.out.println("Main method of another class!");
	}
}

public class Blabla {
	
	class MyClass {
		int getValue() {
			return 123;
		}
	}
	
	int[] getArray() {
		return new int[0];
	}
	
//	Object getObject() {
//		return new Test();
//	}
	
	static boolean getBoolean(String str) {
		return str.equals("blabla");
	}
	
	char getChar() {
		return 'b';
	}
	
	static int getInt() {
		return 1;
	}

    private final void method1013(MyClass mc, int x) {
        System.out.println("method1013");
    }
    
    public static void main(String[] args) {
    	assert(getInt() == 1);
    	assert(getBoolean("blabla"));
    }
}