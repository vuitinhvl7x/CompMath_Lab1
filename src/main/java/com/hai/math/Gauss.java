package com.hai.math;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gauss {
    public double getDet(double A[][], int lengthOfMatrix, int countSwapRow)
    {
        double det = 1;
        for(int x = 0 ; x < lengthOfMatrix ; x++){
            det *=  A[x][x];
        }
        det *= Math.pow(-1,countSwapRow);
        if (Double.isNaN(det)) {
            return 0;
        }
        return det;

    }
    public double[] getNewDiscrepancy(double A[][],double[] B, double[] solution)
    {
        int lengthOfMatrix = A.length;
        double[] dis = new double[lengthOfMatrix];

        for(int i = 0; i < lengthOfMatrix; i++){
            double r = B[i];
            for(int j = 0; j < lengthOfMatrix; j++){
                r -= A[i][j]*solution[j];
            }
            dis[i] = r;
        }
        return dis;
    }
    public void solve(double[][] A, double[] B)
    {
        int lengthOfMatrix = B.length;
        int countSwapRow=0;
        for (int k = 0; k < lengthOfMatrix; k++) {
            int notNone = k;
            if(A[k][k]==0){
                for (int i = k + 1; i < lengthOfMatrix; i++)
                    if (A[i][k]!=0) {
                        notNone = i;
                        break;
                    }
            }
            if (k != notNone) {
                double[] temp = A[k];
                A[k] = A[notNone];
                A[notNone] = temp;
                double t = B[k];
                B[k] = B[notNone];
                B[notNone] = t;
                countSwapRow++;
            }
            for (int i = k + 1; i < lengthOfMatrix; i++) {
                double factor = A[i][k] / A[k][k];
                B[i] -= factor * B[k];
                for (int j = k; j < lengthOfMatrix; j++) {
                    A[i][j] -= factor * A[k][j];
                }
            }
        }

        if(printRowEchelonForm(A, B, countSwapRow) ==0) return;

        double[] solution = new double[lengthOfMatrix];
        for (int i = lengthOfMatrix - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < lengthOfMatrix; j++)
                sum += A[i][j] * solution[j];
            solution[i] = (B[i] - sum) / A[i][i];
        }
        printSolution(solution);

        System.out.println("Вектор невязки: ");
        double[] dis = getNewDiscrepancy(A,B, solution);
        for (double di : dis) System.out.printf("%.2f\s", (double) Math.round(di * 1000) / 1000);
        System.out.println();

    }

    public int printRowEchelonForm(double[][] A, double[] B, int countSwapRow)
    {
        System.out.println();
        int lengthOfMatrix = B.length;
        double tempD=getDet(A, lengthOfMatrix, countSwapRow);
        System.out.println("Определитель матрицы равен: "+tempD);
        if(tempD==0){
            System.out.println("Определитель равен 0, нет решений");
            return 0;
        }
        System.out.println("\nРядно-эшелонная форма : ");
        for (int i = 0; i < lengthOfMatrix; i++)
        {
            for (int j = 0; j < lengthOfMatrix; j++)
                System.out.printf("%.3f ", A[i][j]);
            System.out.printf("| %.3f\n", B[i]);
        }
        System.out.println();
        return 1;
    }

    public void printSolution(double[] solution)
    {
        int lengthOfMatrix = solution.length;
        System.out.println("\nНайдены корни СЛАУ : ");
        for (int i = 0; i < lengthOfMatrix; i++)
            System.out.printf("%.3f ", solution[i]);
        System.out.println();
    }
    public static void main(String[] args) throws IOException {
        Gauss gauss = new Gauss();
        int lengthOfMatrix = 0;
        ArrayList<Double> arrayList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите:");
        System.out.println("1 - для ввода с консоли");
        System.out.println("2 - для чтения файла");
        int choose = scanner.nextInt();
        while (!(choose == 1 || choose == 2)) {
            System.out.println("Ошибка ввода!");
            System.out.println("Введите:");
            System.out.println("1 - для ввода с консоли");
            System.out.println("2 - для чтения файла");
            choose = scanner.nextInt();
        }
        switch (choose) {
            case 1 -> {
                System.out.println("Укажите размерносить матрицы: ");
                lengthOfMatrix = scanner.nextInt();

                if (lengthOfMatrix == 1)
                    System.out.println("Размерность СЛАУ не может быть равна одному");
                else if (lengthOfMatrix == 2) {
                    System.out.println("Формат ввода: 'a11 a12 = b1'");
                    System.out.println("Введите коффициенты через пробел:");
                } else {
                    System.out.println("Формат ввода: 'a11 ... a1" + lengthOfMatrix + " = b1'");
                    System.out.println("Введите коффициенты через пробел:");
                }

                try {
                    String ch = "";
                    Pattern p = Pattern.compile("[A-Za-zА-Яа-я!#$@_+?]");
                    scanner.nextLine();
                    for (int i = 0; i < lengthOfMatrix; i++) {
                        for (int k = 0; k < lengthOfMatrix+1; k++) {
                            System.out.print("arr["+i+"]["+k+"] = ");
                            ch = scanner.nextLine();
                            ch = ch.replaceAll(",+", ".");
                            int countDot = 0;
                            Matcher m = p.matcher(ch);
                            for (int j = 0; j < ch.length(); j++) {
                                if (ch.charAt(j) == '.') {
                                    countDot++;
                                }
                            }
                            if (countDot > 1 || m.find()) {
                                System.out.println("Ошибка! Ещё раз");
                                k--;
                                continue;
                            }
                            arrayList.add(Double.valueOf(ch));
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода!  Проверьте, что дробные числа записаны через запятую");
                }
            }
            case 2 -> {
                try {
                    scanner.nextLine();
                    System.out.println("введите желаемое имя файла");
                    String fileName = scanner.nextLine();
                    FileInputStream path = new FileInputStream("inputTest/"+fileName+".txt");
                    DataInputStream inFile = new DataInputStream(path);
                    BufferedReader br = new BufferedReader(new InputStreamReader(inFile));
                    String data;

                    while ((data = br.readLine()) != null) {
                        data = data.replaceAll(",", ".");
                        String[] tmp = data.split(" ");
                        for (String s : tmp)
                            arrayList.add(Double.parseDouble(s));
                        lengthOfMatrix++;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода!  Проверьте, что дробные числа записаны через точку");
                    System.exit(0);
                }
                System.out.println("Размерность матрицы: ");
                System.out.println(lengthOfMatrix);
                System.out.println();
            }
        }
        // AX =B
        double[][] A = new double[lengthOfMatrix][lengthOfMatrix];
        double[] B = new double[lengthOfMatrix];

        int index = 0;
        for (int i = 0; i < lengthOfMatrix; i++) {
            for (int j = 0; j < lengthOfMatrix; j++) {
                A[i][j] = arrayList.get(index);
                index++;
            }
            B[i] = arrayList.get(index);
            index++;
        }
        gauss.solve(A,B);
    }
}