import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class SchedulerGUI extends JFrame {
    private JButton sjfSubmitButton;
    private JTextArea outputArea;

    public SchedulerGUI() {
        setTitle("Scheduling Algorithm Selector");
        setSize(418, 290);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel algorithmLabel = new JLabel("Choose a scheduling algorithm:");
        algorithmLabel.setBounds(10, 20, 200, 25);
        panel.add(algorithmLabel);

        String[] algorithms = {"Shortest Job First", "Shortest Remaining Time First", "Shortest Seek Time First"};
        JComboBox<String> algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setBounds(220, 20, 150, 25);
        panel.add(algorithmComboBox);

        sjfSubmitButton = new JButton("Submit");
        sjfSubmitButton.setBounds(150, 70, 80, 25);
        panel.add(sjfSubmitButton);

        outputArea = new JTextArea();
        outputArea.setBounds(10, 120, 380, 120);
        outputArea.setEditable(false);
        panel.add(outputArea);

        sjfSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                switch (selectedAlgorithm) {
                    case "Shortest Job First":
                        onSJFSubmit();
                        break;
                    case "Shortest Remaining Time First":
                        onSRTFSubmit();
                        break;
                    case "Shortest Seek Time First":
                        onSSTFSubmit();
                        break;
                    default:
                        JOptionPane.showMessageDialog(SchedulerGUI.this, "Invalid choice. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        });
    }

    private void onSJFSubmit() {
        JFrame inputFrame = new JFrame("SJF Input");
        inputFrame.setSize(400, 300);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputFrame.add(inputPanel);

        // Get the number of processes
        int numberOfProcesses = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of processes:"));

        for (int i = 0; i < numberOfProcesses; i++) {
            JPanel processPanel = new JPanel();
            processPanel.setLayout(new GridLayout(1, 3, 10, 10));

            processPanel.add(new JLabel("Process " + (i + 1)));
            processPanel.add(new JLabel("Arrival Time"));
            processPanel.add(new JLabel("Burst Time"));

            JTextField arrivalField = new JTextField();
            JTextField burstField = new JTextField();

            processPanel.add(arrivalField);
            processPanel.add(burstField);

            inputPanel.add(processPanel);
        }

        JButton submitInputButton = new JButton("Submit Input");
        inputPanel.add(submitInputButton);

        submitInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate and collect input from the fields
                try {
                    int[] arrivalTime = new int[numberOfProcesses];
                    int[] burstTime = new int[numberOfProcesses];

                    Component[] components = inputPanel.getComponents();
                    for (int i = 0; i < numberOfProcesses; i++) {
                        JPanel processPanel = (JPanel) components[i];
                        JTextField arrivalField = (JTextField) processPanel.getComponent(3);
                        JTextField burstField = (JTextField) processPanel.getComponent(4);

                        arrivalTime[i] = Integer.parseInt(arrivalField.getText());
                        burstTime[i] = Integer.parseInt(burstField.getText());
                    }

                    // Call the SJF logic with the input values
                    sjfLogic(arrivalTime, burstTime);
                    // Close the input frame after processing
                    inputFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(inputFrame, "Please enter valid numerical values for arrival and burst time.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        inputFrame.setVisible(true);
    }

    private void sjfLogic(int[] arrivalTime, int[] burstTime) {
        int n = arrivalTime.length;

        // Create a copy of burstTime array to track remaining burst time for each process
        int[] remainingTime = Arrays.copyOf(burstTime, n);

        // Sort processes based on arrival time
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arrivalTime[j] > arrivalTime[j + 1]) {
                    // Swap arrivalTime
                    int tempArrival = arrivalTime[j];
                    arrivalTime[j] = arrivalTime[j + 1];
                    arrivalTime[j + 1] = tempArrival;

                    // Swap burstTime
                    int tempBurst = burstTime[j];
                    burstTime[j] = burstTime[j + 1];
                    burstTime[j + 1] = tempBurst;

                    // Swap remainingTime
                    int tempRemaining = remainingTime[j];
                    remainingTime[j] = remainingTime[j + 1];
                    remainingTime[j + 1] = tempRemaining;
                }
            }
        }

        // Variables to store waiting time and turnaround time
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];

        // Initialize the first process
        waitingTime[0] = 0;
        turnaroundTime[0] = burstTime[0];

        // Calculate waiting time and turnaround time for each process
        for (int i = 1; i < n; i++) {
            waitingTime[i] = waitingTime[i - 1] + burstTime[i - 1];
            turnaroundTime[i] = waitingTime[i] + burstTime[i];
        }

        // Calculate average waiting time and average turnaround time
        double averageWaitingTime = Arrays.stream(waitingTime).average().orElse(0);
        double averageTurnaroundTime = Arrays.stream(turnaroundTime).average().orElse(0);

        // Display the results in the JTextArea
        StringBuilder result = new StringBuilder("SJF Output:\n");
        for (int i = 0; i < n; i++) {
            result.append("Process ").append(i + 1).append(": Waiting time = ").append(waitingTime[i]).append(", Turnaround time = ").append(turnaroundTime[i]).append("\n");
        }

        // Append average waiting time and average turnaround time
        result.append("Average Waiting Time: ").append(averageWaitingTime).append("\n");
        result.append("Average Turnaround Time: ").append(averageTurnaroundTime).append("\n");

        outputArea.setText(result.toString());
    }

    private void onSRTFSubmit() {
        JFrame inputFrame = new JFrame("SRTF Input");
        inputFrame.setSize(400, 300);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputFrame.add(inputPanel);

        // Get the number of processes
        int numberOfProcesses = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of processes:"));

        for (int i = 0; i < numberOfProcesses; i++) {
            JPanel processPanel = new JPanel();
            processPanel.setLayout(new GridLayout(1, 3, 10, 10));

            processPanel.add(new JLabel("Process " + (i + 1)));
            processPanel.add(new JLabel("Arrival Time"));
            processPanel.add(new JLabel("Burst Time"));

            JTextField arrivalField = new JTextField();
            JTextField burstField = new JTextField();

            processPanel.add(arrivalField);
            processPanel.add(burstField);

            inputPanel.add(processPanel);
        }

        JButton submitInputButton = new JButton("Submit Input");
        inputPanel.add(submitInputButton);

        submitInputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate and collect input from the fields
                try {
                    int[] arrivalTime = new int[numberOfProcesses];
                    int[] burstTime = new int[numberOfProcesses];

                    Component[] components = inputPanel.getComponents();
                    for (int i = 0; i < numberOfProcesses; i++) {
                        JPanel processPanel = (JPanel) components[i];
                        JTextField arrivalField = (JTextField) processPanel.getComponent(3);
                        JTextField burstField = (JTextField) processPanel.getComponent(4);

                        arrivalTime[i] = Integer.parseInt(arrivalField.getText());
                        burstTime[i] = Integer.parseInt(burstField.getText());
                    }

                    // Call the SRTF logic with the input values
                    srtfLogic(arrivalTime, burstTime);
                    // Close the input frame after processing
                    inputFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(inputFrame, "Please enter valid numerical values for arrival and burst time.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        inputFrame.setVisible(true);
    }

    private void srtfLogic(int[] arrivalTime, int[] burstTime) {
        int n = arrivalTime.length;

        // Create a copy of burstTime array to track remaining burst time for each process
        int[] remainingTime = Arrays.copyOf(burstTime, n);

        // Variables to store waiting time and turnaround time
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];

        // Variable to keep track of total completed processes
        int completedProcesses = 0;

        // Variable to keep track of current time
        int currentTime = 0;

        // Continue processing until all processes are completed
        while (completedProcesses < n) {
            int shortestProcess = -1;
            int shortestBurst = Integer.MAX_VALUE;

            // Find the process with the shortest remaining burst time among the arrived processes
            for (int i = 0; i < n; i++) {
                if (arrivalTime[i] <= currentTime && remainingTime[i] < shortestBurst && remainingTime[i] > 0) {
                    shortestBurst = remainingTime[i];
                    shortestProcess = i;
                }
            }

            // If a process is found, update waiting time, turnaround time, and remaining burst time
            if (shortestProcess != -1) {
                remainingTime[shortestProcess]--;

                // Update waiting time for processes not currently running
                for (int i = 0; i < n; i++) {
                    if (i != shortestProcess && arrivalTime[i] <= currentTime && remainingTime[i] > 0) {
                        waitingTime[i]++;
                    }
                }

                // Check if the process has completed
                if (remainingTime[shortestProcess] == 0) {
                    completedProcesses++;
                    int completionTime = currentTime + 1;
                    turnaroundTime[shortestProcess] = completionTime - arrivalTime[shortestProcess];
                    waitingTime[shortestProcess] = turnaroundTime[shortestProcess] - burstTime[shortestProcess];
                }
            }

            currentTime++;
        }

        // Calculate average waiting time and average turnaround time
        double averageWaitingTime = Arrays.stream(waitingTime).average().orElse(0);
        double averageTurnaroundTime = Arrays.stream(turnaroundTime).average().orElse(0);

        // Display the results in the JTextArea
        StringBuilder result = new StringBuilder("SRTF Output:\n");
        for (int i = 0; i < n; i++) {
            result.append("Process ").append(i + 1).append(": Waiting time = ").append(waitingTime[i]).append(", Turnaround time = ").append(turnaroundTime[i]).append("\n");
        }

        // Append average waiting time and average turnaround time
        result.append("Average Waiting Time: ").append(averageWaitingTime).append("\n");
        result.append("Average Turnaround Time: ").append(averageTurnaroundTime).append("\n");

        outputArea.setText(result.toString());
    }


    private void onSSTFSubmit() {
        int currentPosition = Integer.parseInt(JOptionPane.showInputDialog("Enter the current position:"));
        int trackSize = Integer.parseInt(JOptionPane.showInputDialog("Enter the track size:"));
        int seekRate = Integer.parseInt(JOptionPane.showInputDialog("Enter the seek rate:"));
        int numberOfRequests = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of requests (max 10):"));

        if (numberOfRequests > 10) {
            JOptionPane.showMessageDialog(this, "Maximum number of requests is 10.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] requests = new int[numberOfRequests];

        for (int i = 0; i < numberOfRequests; i++) {
            requests[i] = Integer.parseInt(JOptionPane.showInputDialog("Enter the location of request " + (i + 1) + ":"));
        }

        // Call the SSTF logic with the input values
        sstfLogic(currentPosition, trackSize, seekRate, requests);
    }

    private void sstfLogic(int currentPosition, int trackSize, int seekRate, int[] requests) {
        int n = requests.length;

        // Copy the requests array to track the remaining requests
        int[] remainingRequests = Arrays.copyOf(requests, n);

        // Variables to store total head movement and seek time
        int totalHeadMovement = 0;
        double seekTime = 0;

        // Process requests until all are completed
        while (n != 0) {
            int closest = Integer.MAX_VALUE;
            int index = -1;

            // Find the request closest to the current position
            for (int i = 0; i < n; i++) {
                int distance = Math.abs(currentPosition - remainingRequests[i]);
                if (distance < closest) {
                    closest = distance;
                    index = i;
                }
            }

            // Update total head movement and seek time
            totalHeadMovement += closest;
            seekTime += (double) closest / seekRate;

            // Move to the next request
            currentPosition = remainingRequests[index];

            // Remove the completed request
            for (int i = index; i < n - 1; i++) {
                remainingRequests[i] = remainingRequests[i + 1];
            }
            n--;
        }

        // Display the results in the JTextArea
        StringBuilder result = new StringBuilder("SSTF Output:\n");
        result.append("Total Head Movement: ").append(totalHeadMovement).append("\n");
        result.append("Seek Time: ").append(seekTime).append("\n");

        outputArea.setText(result.toString());
    }

    public static void main(String[] args) {
        new SchedulerGUI();
    }
}
