import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a scheduling algorithm:");
        System.out.println("A. Shortest Job First (CPU Scheduling)");
        System.out.println("B. Shortest Remaining Time First (CPU Scheduling)");
        System.out.println("C. Shortest Seek Time First (Disk Scheduling)");
        System.out.print("Enter your choice (A, B, or C): ");
        String choice = scanner.nextLine();

        switch(choice.toUpperCase()) {
            case "A":
                // Call the function for Shortest Job First
                shortestJobFirst();
                break;
            case "B":
                // Call the function for Shortest Remaining Time First
                shortestRemainingTimeFirst();
                break;
            case "C":
                // Call the function for Shortest Seek Time First
                shortestSeekTimeFirst();
                break;
            default:
                System.out.println("Invalid choice. Please enter A, B, or C.");
                break;
        }
    }

    public static void shortestJobFirst() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();
        int[] arrivalTime = new int[n];
        int[] burstTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];

        for (int i = 0; i < n; i++) {
            System.out.println("Enter arrival time and burst time for process " + (i + 1) + ": ");
            arrivalTime[i] = scanner.nextInt();
            burstTime[i] = scanner.nextInt();
        }

        // Sort processes by burst time
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (burstTime[i] > burstTime[j]) {
                    int temp = burstTime[i];
                    burstTime[i] = burstTime[j];
                    burstTime[j] = temp;

                    temp = arrivalTime[i];
                    arrivalTime[i] = arrivalTime[j];
                    arrivalTime[j] = temp;
                }
            }
        }

        // Calculate waiting time and turnaround time
        waitingTime[0] = 0;
        turnaroundTime[0] = burstTime[0];
        int totalWaitingTime = waitingTime[0];
        int totalTurnaroundTime = turnaroundTime[0];

        for (int i = 1; i < n; i++) {
            waitingTime[i] = burstTime[i - 1] - arrivalTime[i] + waitingTime[i - 1];
            turnaroundTime[i] = waitingTime[i] + burstTime[i];
            totalWaitingTime += waitingTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }

        // Print waiting time and turnaround time for each process
        for (int i = 0; i < n; i++) {
            System.out.println("Process " + (i + 1) + ": Waiting time = " + waitingTime[i] + ", Turnaround time = " + turnaroundTime[i]);
        }

        // Print average waiting time and turnaround time
        System.out.println("Average waiting time: " + (double) totalWaitingTime / n);
        System.out.println("Average turnaround time: " + (double) totalTurnaroundTime / n);
    }

    public static void shortestRemainingTimeFirst() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();
        int[] arrivalTime = new int[n];
        int[] burstTime = new int[n];
        int[] remainingTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];

        for (int i = 0; i < n; i++) {
            System.out.println("Enter arrival time and burst time for process " + (i + 1) + ": ");
            arrivalTime[i] = scanner.nextInt();
            burstTime[i] = scanner.nextInt();
            remainingTime[i] = burstTime[i];
        }

        int complete = 0, t = 0, minm = Integer.MAX_VALUE;
        int shortest = 0, finishTime;
        boolean check = false;

        // Process until all processes gets
        // completed
        while (complete != n) {

            // Find process with minimum remaining
            // time among the processes that
            // arrives till the current time`
            for (int j = 0; j < n; j++) {
                if ((arrivalTime[j] <= t) && (remainingTime[j] < minm) && remainingTime[j] > 0) {
                    minm = remainingTime[j];
                    shortest = j;
                    check = true;
                }
            }

            if (!check) {
                t++;
                continue;
            }

            // Reduce remaining time by one
            remainingTime[shortest]--;

            // Update minimum
            minm = remainingTime[shortest];
            if (minm == 0)
                minm = Integer.MAX_VALUE;

            // If a process gets completely
            // executed
            if (remainingTime[shortest] == 0) {

                // Increment complete
                complete++;
                check = false;

                // Find finish time of current
                // process
                finishTime = t + 1;

                // Calculate waiting time
                waitingTime[shortest] = finishTime - burstTime[shortest] - arrivalTime[shortest];

                if (waitingTime[shortest] < 0)
                    waitingTime[shortest] = 0;
            }
            // Increment time
            t++;
        }

        // Calculate turnaround time
        for (int i = 0; i < n; i++)
            turnaroundTime[i] = burstTime[i] + waitingTime[i];

        // Print waiting time and turnaround time for each process
        for (int i = 0; i < n; i++) {
            System.out.println("Process " + (i + 1) + ": Waiting time = " + waitingTime[i] + ", Turnaround time = " + turnaroundTime[i]);
        }

        // Print average waiting time and turnaround time
        System.out.println("Average waiting time: " + Arrays.stream(waitingTime).average().getAsDouble());
        System.out.println("Average turnaround time: " + Arrays.stream(turnaroundTime).average().getAsDouble());
    }

    public static void shortestSeekTimeFirst() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the current position: ");
        int currentPosition = scanner.nextInt();
        System.out.print("Enter the track size: ");
        int trackSize = scanner.nextInt();
        System.out.print("Enter the seek rate: ");
        int seekRate = scanner.nextInt();
        System.out.print("Enter the number of requests (max 10): ");
        int n = scanner.nextInt();
        int[] requests = new int[n];

        for (int i = 0; i < n; i++) {
            System.out.println("Enter the location of request " + (i + 1) + ": ");
            requests[i] = scanner.nextInt();
        }

        // Calculate total head movement
        int totalHeadMovement = 0;
        while (n != 0) {
            int closest = Integer.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < n; i++) {
                if (Math.abs(currentPosition - requests[i]) < closest) {
                    closest = Math.abs(currentPosition - requests[i]);
                    index = i;
                }
            }
            totalHeadMovement += closest;
            currentPosition = requests[index];
            for (int i = index; i < n - 1; i++) {
                requests[i] = requests[i + 1];
            }
            n--;
        }

        // Calculate seek time
        double seekTime = (double) totalHeadMovement / seekRate;

        // Print total head movement and seek time
        System.out.println("Total head movement: " + totalHeadMovement);
        System.out.println("Seek time: " + seekTime);

        // Ask user if they want to repeat the process or terminate the program
        System.out.print("Do you want to repeat the process? (yes/no): ");
        String repeat = scanner.next();
        if (repeat.equalsIgnoreCase("yes")) {
            shortestSeekTimeFirst();
        }
    }
}