import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// метод інтерфейсу Sorted: public ArrayList<Integer> sort(ArrayList<Integer> input);
interface Sorter {
    ArrayList<Integer> sort(ArrayList<Integer> input);
}

// enum SortingType з видами сортування (4 елементи)
enum SortingType {
    BUBBLE, SHELL, MERGE, QUICK
}

// фабричний метод (статичний), який отримує на вхід значення enum і повертає відповідний клас сортування (тип значення, що повертається - інтерфейс Sorter)
class SorterFactory {
    public static Sorter createSorter(SortingType type) {
        switch (type) {
            case BUBBLE:
                return new BubbleSorter();
            case SHELL:
                return new ShellSorter();
            case MERGE:
                return new MergeSorter();
            case QUICK:
                return new QuickSorter();
            default:
                throw new IllegalArgumentException("Непідтримуваний тип сортування");
        }
    }
}

// Bublesort
class BubbleSorter implements Sorter {
    @Override
    public ArrayList<Integer> sort(ArrayList<Integer> input) {
        ArrayList<Integer> sortedArray = new ArrayList<>(input);
        int n = sortedArray.size();

        // Перевірка розміру масиву
        if (n >= 1000000) {
            System.out.println("Розмір масиву занадто великий для сортування цим методом.");
            return sortedArray;
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedArray.get(j) > sortedArray.get(j + 1)) {
                    // обмін temp і arr[i]
                    Collections.swap(sortedArray, j, j + 1);
                }
            }
        }
        return sortedArray;
    }
}

// Shellsort
class ShellSorter implements Sorter {
    @Override
    public ArrayList<Integer> sort(ArrayList<Integer> input) {
        ArrayList<Integer> sortedArray = new ArrayList<>(input);
        int n = sortedArray.size();

        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = sortedArray.get(i);
                int j;
                for (j = i; j >= gap && sortedArray.get(j - gap) > temp; j -= gap) {
                    sortedArray.set(j, sortedArray.get(j - gap));
                }
                sortedArray.set(j, temp);
            }
        }
        return sortedArray;
    }
}

// Mergesort
class MergeSorter implements Sorter {
    @Override
    public ArrayList<Integer> sort(ArrayList<Integer> input) {
        ArrayList<Integer> sortedArray = new ArrayList<>(input);
        mergeSort(sortedArray, 0, sortedArray.size() - 1);
        return sortedArray;
    }

    private void mergeSort(ArrayList<Integer> arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            merge(arr, left, mid, right);
        }
    }

    private void merge(ArrayList<Integer> arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        for (int i = 0; i < n1; i++) {
            leftArray[i] = arr.get(left + i);
        }

        for (int j = 0; j < n2; j++) {
            rightArray[j] = arr.get(mid + 1 + j);
        }

        // Злиття тимчасових масивів
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                arr.set(k++, leftArray[i++]);
            } else {
                arr.set(k++, rightArray[j++]);
            }
        }

        // Копіювання залишкових елементів leftArray[], якщо є
        while (i < n1) {
            arr.set(k++, leftArray[i++]);
        }

        // Копіювання залишкових елементів rightArray[], якщо є
        while (j < n2) {
            arr.set(k++, rightArray[j++]);
        }
    }
}

// Quicksort
class QuickSorter implements Sorter {
    @Override
    public ArrayList<Integer> sort(ArrayList<Integer> input) {
        ArrayList<Integer> sortedArray = new ArrayList<>(input);
        quickSort(sortedArray, 0, sortedArray.size() - 1);
        return sortedArray;
    }

    private void quickSort(ArrayList<Integer> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(ArrayList<Integer> arr, int low, int high) {
        int pivot = arr.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr.get(j) < pivot) {
                i++;

                // обмін arr[i] і arr[j]
                Collections.swap(arr, i, j);
            }
        }

        // обмін arr[i+1] і arr[high] (або pivot)
        Collections.swap(arr, i + 1, high);

        return i + 1;
    }
}

public class Main {
    public static void main(String[] args) {
        for (int count : new int[]{10, 1000, 10000, 1000000}) {

            System.out.println("Розмір масиву: " + count);
            System.out.println();

            // Генерація масиву
            ArrayList<Integer> inputArray = generateArray(count);

            for (SortingType type : SortingType.values()) {
                System.out.println("Вид сортування: " + type);

                // Створення об'єкту сортування за допомогою фабричного методу
                Sorter sorter = SorterFactory.createSorter(type);

                // Вимірювання і виведення часу виконання
                long startTime = System.currentTimeMillis();
                ArrayList<Integer> sortedArray = sorter.sort(new ArrayList<>(inputArray));
                long endTime = System.currentTimeMillis();
                System.out.println("Час виконання: " + (endTime - startTime) + " мілісекунд");

                // Виведення перших 50 елементів відсортованого масиву
                System.out.println("Відсортований масив (перші 50 елементів): " +
                        Arrays.toString(sortedArray.subList(0, Math.min(50, count)).toArray()));

                System.out.println("----------------------------------");
            }
        }
    }

    // Метод для генерації масиву випадкових цілих чисел
    private static ArrayList<Integer> generateArray(int count) {
        ArrayList<Integer> array = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            array.add(random.nextInt(count));
        }

        return array;
    }
}
