package test;

public class ArraysTests {
    public static void main(String[] args) throws Exception {
        assertEquals(new int[]{1, 2, 1});
        assertEquals(new int[]{3, 4});
        assertEquals(new int[]{0, 1});
        assertEquals(new int[]{1, 0});
        assertEquals(new int[]{1, 0});
        assertEquals(new int[]{1, 1, 2});
        assertEquals(new int[]{0, 0});
        assertEquals(new int[]{100000000, 100000000});
        assertEquals(new int[]{100000000, 100000000});
        assertEquals(new int[]{14419485, 34715515});
        assertEquals(new int[]{45193875, 34715515});
        assertEquals(new int[]{4114169, 4536507});
        assertEquals(new int[]{58439428, 4536507});
        assertEquals(new int[]{66846981, 79684230});
        assertEquals(new int[]{9845918, 12173585, 1582497});
        assertEquals(new int[]{57473250, 46265854});
        assertEquals(new int[]{89164828, 36174769});
        assertEquals(new int[]{90570286, 89164829});
        assertEquals(new int[]{23720786, 67248252});
        assertEquals(new int[]{89244428, 67248253});
        assertEquals(new int[]{42155494, 49587877, 38430911});
        assertEquals(new int[]{4232631, 705311});
        assertEquals(new int[]{217361, 297931});
        assertEquals(new int[]{297930, 83550501});
        assertEquals(new int[]{98915325, 66344301});
        assertEquals(new int[]{72765050, 72765049});
        assertEquals(new int[]{72763816, 77716490});
        assertEquals(new int[]{24732962, 55862695});
        assertEquals(new int[]{100000000, 100000000});
        assertEquals(new int[]{100000000, 100000000});
        assertEquals(new int[]{100000000, 100000000});
        assertEquals(new int[]{0, 0});
        assertEquals(new int[]{0, 0});
        assertEquals(new int[]{0, 0});
        assertEquals(new int[]{99999999, 99999999});
        assertEquals(new int[]{0, 2, 7, 3});
        assertEquals(new int[]{7, 9});
        assertEquals(new int[]{1, 5, 6, 7});
        assertEquals(new int[]{8, 9, 10});
    }

    private static void assertEquals(int[] A) throws Exception {
        int[] B = A.clone();
        aryssoncf.util.Arrays.sort(A);
        java.util.Arrays.sort(B);
        if (!java.util.Arrays.equals(A, B)) {
            throw new Exception("Array is not sorted!");
        }
    }
}
