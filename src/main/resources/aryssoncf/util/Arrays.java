package aryssoncf.util;

public class Arrays {
    public static void sort(int[] a) throws Exception {
        if (a == null) {
            return;
        }
        Integer[] A = new Integer[a.length];
        for (int i = 0; i < a.length; i++) {
            A[i] = a[i];
        }
        sort(A);
        for (int i = 0; i < a.length; i++) {
            a[i] = A[i];
        }
    }
    
    public static void sort(long[] a) throws Exception {
        if (a == null) {
            return;
        }
        Long[] A = new Long[a.length];
        for (int i = 0; i < a.length; i++) {
            A[i] = a[i];
        }
        sort(A);
        for (int i = 0; i < a.length; i++) {
            a[i] = A[i];
        }
    }

    public static void sort(Object[] A) throws Exception {
        if (A == null) {
            return;
        }
        sort(A, 0, A.length);
    }

    public static void sort(Object[] A, int fromIndex, int toIndex) throws Exception {
        if (A == null) {
            return;
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex > toIndex");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("fromIndex < 0");
        }
        if (toIndex > A.length) {
            throw new ArrayIndexOutOfBoundsException("toIndex > A.length");
        }
        Object[] B = new Object[A.length];
        sort(A, fromIndex, toIndex, B);
    }

    private static void sort(Object[] A, int fromIndex, int toIndex, Object[] B) {
        if (toIndex == fromIndex + 1) {
            return;
        }
        int midIndex = fromIndex + (toIndex - fromIndex) / 2;
        sort(A, fromIndex, midIndex, B);
        sort(A, midIndex, toIndex, B);
        merge(A, fromIndex, midIndex, toIndex, B);
        System.arraycopy(B, fromIndex, A, fromIndex, toIndex - fromIndex);
    }

    private static void merge(Object[] A, int fromIndex, int midIndex, int toIndex, Object[] B) {
        for (int i = fromIndex, j = midIndex, k = fromIndex; i < midIndex || j < toIndex; ) {
            if (i < midIndex && (j == toIndex || ((Comparable)A[i]).compareTo((Comparable)A[j]) < 0)) {
                B[k++] = A[i++];
            } else {
                B[k++] = A[j++];
            }
        }
    }
}
