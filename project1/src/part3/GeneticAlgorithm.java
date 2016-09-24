package part3;
import java.util.*;


@SuppressWarnings({"Duplicates", "FieldCanBeLocal"})
public class GeneticAlgorithm {

    private boolean input;
    private Random random;
    private int n, popSize,numOfMutations,solutionsFound, runTime, arrayPrintIndexing;
    private int[] startBoard, parents;
    private Scanner reader;
    private int[][] populationArray;
    private HashSet<String> solutionSet;

    private GeneticAlgorithm(){

        // ---- SETTINGS ---------------------------------
        this.input = false;
        this.n = 10;
        this.popSize = 4;
        this.numOfMutations = 1;
        this.runTime = 5;
        this.arrayPrintIndexing = 0;
        // -----------------------------------------------
        reader = new Scanner(System.in);

        if (input){
            System.out.print("n = ");
            this.n = reader.nextInt();
            reader.nextLine();
            System.out.print("input = ");
            String initialBoardString = reader.nextLine();
            if (initialBoardString.length()>0){
                this.startBoard = Arrays.stream(initialBoardString.split(" ")).mapToInt(Integer::parseInt).toArray();
            }
        }
        this.solutionsFound = 0;
        this.solutionSet = new HashSet<>();
        this.startBoard = new int[n];
        this.random = new Random();
        this.populationArray = new int[popSize][n];

        System.out.println("Initial board:");
        printBoard(startBoard);
        sortRows(startBoard);
        System.out.println("\nSorted initial board:");
        printBoard(startBoard);
        populationArray = generatePopulation();
    }

    private void runAlgorithm() {
        long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        while(currentTime - startTime < runTime*1000){
            parents = selectParents();
            breed(populationArray[parents[0]], populationArray[parents[1]]);

            currentTime = System.currentTimeMillis();
        }

    }

    private int[] selectParents() {
        int[] parentFitnessArray = new int[popSize];
        int[] bestParentIndex = new int[2];
        int currentBest = 4*this.n;

        for (int i = 0; i < popSize; i++){
            parentFitnessArray[i] = calculateFitness2(populationArray[i]);
        }
//        printArray(parentFitnessArray);

        for (int i = 0; i < bestParentIndex.length; i++) {
            for (int j = 0; j < parentFitnessArray.length; j++) {
                if(parentFitnessArray[j] < currentBest){
                    currentBest = parentFitnessArray[j];
                    bestParentIndex[i] = j;
                }
            }
            parentFitnessArray[bestParentIndex[i]] = 4*n;
            currentBest = 4*n;
        }

        //check
//        for (int i = 0; i < bestParentIndex.length; i++) {
//            System.out.println(calculateFitness(populationArray[bestParentIndex[i]]));
//        }

        return bestParentIndex;
    }

    private int[] selectParents2(int[][] populationArray) {
        int[] parentFitnessArray = new int[popSize];
        int[][] parentCouplesArray = new int[popSize][2];
        int parentFitness;
        int totalFitness = 0;

        for (int i = 0; i < popSize; i++){
            parentFitness = calculateFitness2(populationArray[i]);
            parentFitnessArray[i] = parentFitness;
            totalFitness += parentFitness;
        }
//        printArray(parentFitnessArray);
        for (int i = 0; i < popSize; i++) {
            parentFitnessArray[i] = totalFitness-parentFitnessArray[i];
        }
        totalFitness = 0;
        for (int i = 0; i < popSize; i++) {
            totalFitness+= parentFitnessArray[i];
        }

        double parentPointer = random.nextDouble() * totalFitness;
        for (int i = 0; i < popSize; i++) {

//            System.out.println("TotalFitness = " + totalFitness + " InitFitness = " + parentFitnessArray[i] + "ParentPointer = " + parentPointer);
            parentPointer -= (parentFitnessArray[i]);
            if (parentPointer < 0.0){
                parentCouplesArray[0][0] = i;
                break;
            }
        }
        parentCouplesArray[0][1] = parentCouplesArray[0][0];
        while (parentCouplesArray[0][0] == parentCouplesArray[0][1]) {
            parentPointer = random.nextDouble() * totalFitness;
            for (int i = 0; i < popSize; i++) {
                parentPointer -= (parentFitnessArray[i]);
                if (parentPointer < 0.0){
                    parentCouplesArray[0][1] = i;
                    break;
                }
            }
        }
//        printArray(parentCouplesArray[0]);
        return parentCouplesArray[0];
    }

    private void breed(int[] first, int[] second){

//        System.out.println("Breeding First");
//        printArray(first);
//        printBoard(first);
//        System.out.println("Second");
//        printArray(second);
//        printBoard(second);

//        System.out.println("Initial population");
//        printPopulation(populationArray);


        for (int i = 0; i < popSize; i++) {

            int[] child = new int[n];
            for (int j = 0; j < this.n; j++) {
                if (j <= (n-1)/2) child[j] = first[j];

                else child[j] = second[j];
            }
//            System.out.println("Child");
//            printArray(child);
//            printBoard(child);

            sortRows(child);

//            System.out.println("Sorted Child");
//            printArray(child);
//            printBoard(child);

            child = mutate(child);

//            printArray(child);
//            printBoard(child);

            populationArray[i] = child;
        }

//        System.out.println("neww population");
//        printPopulation(populationArray);

    }

    private int[][] generatePopulation(){
        int[][] newPopulationArray = new int[popSize][n];
        for (int i = 0; i < popSize; i++) {
            newPopulationArray[i] = mutate(startBoard);
//            printArray(newPopulationArray[i]);
        }
        System.out.println();
        return newPopulationArray;
    }

    private int[] mutate(int[] oldArray){
        int[] array = oldArray.clone();
        for (int i = 0; i < this.numOfMutations; i++) {
            swapColumns(array);
//            printArray(array);
        }
        return array;
    }

    private void swapColumns(int[] array) {
        int first = random.nextInt(array.length);
        int second = random.nextInt(array.length);
        while(first == second){
            second = random.nextInt(array.length);
        }
        int temp = array[first];
        array[first] = array[second];
        array[second] = temp;
//        System.out.println("Swapped column "+first+" with " + second);
    }

    private void sortRows(int[] array){
        boolean[] taken = new boolean[this.n];
        ArrayList<Integer> collisionColumns = new ArrayList<>();

        for (int col = 0; col < array.length; col++) {
            if (!taken[array[col]]) taken[array[col]] = true;
            else collisionColumns.add(col);
        }
        for (int row = 0; row < taken.length; row++) {
            if(taken[row] == false){
                array[collisionColumns.remove(random.nextInt(collisionColumns.size()))] = row;
            }
        }
    }



    private int calculateFitnessWithRows(int[] array){
        int cost= 0;
        int downRight[] = new int[2*this.n-1];
        int upRight[] = new int[2*this.n-1];
        int horizontal[] = new int[array.length];
        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.length; x++) {
            downRight[array[x]+x]++;
            upRight[x-array[x]+this.n-1]++;
            horizontal[array[x]]++;
        }

        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
        }

        for (int i = 0; i < horizontal.length; i++) {
            if (horizontal[i] > 0 ) cost += horizontal[i] - 1;
        }
        // System.out.println("cost: "+cost);
        if(cost == 0){
//            if(!solutionSet.contains(arrayToString(array))) {
//                System.out.println("Solution found!!!");
//                printArray(array);
//                printBoard(array);
//            }
            solutionSet.add(arrayToString(array));
            solutionsFound++;
        }
        return cost;
    }

    private int calculateFitness2(int[] array){
        int cost = 0;
        int downRight[] = new int[2*this.n-1];
        int upRight[] = new int[2*this.n-1];
        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.length; x++) {
            downRight[array[x]+x]++;
            upRight[x-array[x]+this.n-1]++;
        }

        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
        }
        // System.out.println("cost: "+cost);
        if(cost == 0){
//            if(!solutionSet.contains(arrayToString(array))) {
//                System.out.println("Solution found!!!");
//                printArray(array);
//                printBoard(array);
//            }
            solutionSet.add(arrayToString(array));
            solutionsFound++;
        }
        return cost;
    }

    private void printBoard(int[] array) {
        printArray(array);
        for (int i = this.n-1; i > -1; i--) {
            for (int j = 0; j < this.n; j++) {
                if(array[j] == i) System.out.print("X ");
                else System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printArray(int[] array){
        for (int i = 0; i < array.length; i++) {
            System.out.print((array[i]+arrayPrintIndexing)+" ");
        }
        System.out.println();
    }

    private void printPopulation(int[][] array){
        for (int i = 0; i < array.length; i++) {
            printArray(array[i]);
            printBoard(array[i]);
        }
    }

    private String arrayToString(int[] array){
        String str = "";
        for (int i : array) str += i;
        return str;
    }

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        long startTime = System.currentTimeMillis();
        ga.runAlgorithm();
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("n = " + ga.n);
        System.out.println("popSize = " + ga.popSize);
        System.out.println("numOfMutations = " +ga.numOfMutations);
        System.out.println("\nSolutions found: "+ ga.solutionsFound);
        System.out.println("Unique solutions found: "+ ga.solutionSet.size());
        System.out.println("Execution time: " + executionTime/1000 + " seconds");

    }

//    public static void main(String[] args) {
//        GeneticAlgorithm ga = new GeneticAlgorithm();
//        int[] testArray = new int[] {1,3,4,2};
//        ga.printArray(testArray);
//        int[] mutatedArray = ga.mutate(testArray);
//    }

}
/*    public void selectParents(){
        int[] parentFitnessArray = new int[popSize];
        double totalFitness = 0;
        double[] parentProbArray = new double[popSize+1];
        int parentFitness;
        int[] selectedParents = new int[2*n];
        for (int i = 0; i < populationArray.length; i++) {
            parentFitness =  calculateFitness(populationArray[i]);
            parentFitnessArray[i] = parentFitness;
            totalFitness += parentFitness;
        }
        System.out.println("Total fitness: " + totalFitness);
        parentProbArray[1] = (double)parentFitnessArray[0] / totalFitness;
        for (int i = 2; i < populationArray.length; i++) {
            parentProbArray[i] = (double)parentFitnessArray[i] / totalFitness + parentProbArray[i - 1];
        }
        parentProbArray[0]= 0;
        parentProbArray[popSize] = 1;
        for (int i = 0; i < parentProbArray.length; i++) {
            System.out.println("Parent prob" + i + " - " + parentProbArray[i]);
        }
        double parentPointer = random.nextDouble();
        int index = parentProbArray.length/2;
        int parentCounter = 0;
        while (parentCounter < numOfParentCombinations*2){
          //  System.out.println("Index: " + parentProbArray[index] + " Pointer: " + parentPointer);
            if (parentProbArray[index] > parentPointer){
                if (index == 0 || parentProbArray[index-1] < parentPointer) {
                    selectedParents[parentCounter] = index;
                    parentCounter += 1;
                    parentPointer = random.nextDouble();
                }
                else index += index/2.0;
            }
            else index -= index/2.0;


        }

    }
    public void breed(int[] first, int[] second){

    }
*/