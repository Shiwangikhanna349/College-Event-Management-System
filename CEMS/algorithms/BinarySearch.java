package algorithms;

public class BinarySearch {
    public static <T extends Comparable<T>> int binarySearch(T[] arr, T target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (arr[mid].equals(target))
                return mid;

            if (arr[mid].compareTo(target) < 0)
                left = mid + 1;
            else
                right = mid - 1;
        }

        return -1; // Element not found
    }


} 