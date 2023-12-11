import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

public class SchedulerGUI extends JFrame {
    private JTextArea outputArea;

    public SchedulerGUI() {
        setTitle("Scheduling Algorithm Selector");
        setSize(600, 400);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the JFrame on the display
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center-align the title
        panel.add(inputPanel, BorderLayout.NORTH);

        // Add padding to the top using an EmptyBorder
        int paddingTop = 10; // Adjust this value as needed
        inputPanel.setBorder(BorderFactory.createEmptyBorder(paddingTop, 0, 0, 0));

        // Grouping code for JLabel, JComboBox, and JButton
        JLabel algorithmLabel = new JLabel("Choose a scheduling algorithm:");
        inputPanel.add(algorithmLabel);

        String[] algorithms = {"Shortest Job First", "Shortest Remaining Time First", "Shortest Seek Time First"};
        JComboBox<String> algorithmComboBox = new JComboBox<>(algorithms);
        inputPanel.add(algorithmComboBox);

        JButton submitButton = new JButton("Submit");
        inputPanel.add(submitButton);


        outputArea = new JTextArea();
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding inside the JTextArea
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add padding to the sides using an EmptyBorder
        int paddingSize = 10; // Adjust this value as needed
        scrollPane.setBorder(BorderFactory.createEmptyBorder(paddingSize, paddingSize, paddingSize, paddingSize));

        // Set JTextArea to occupy the center of the layout
        panel.add(scrollPane, BorderLayout.CENTER);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                switch (Objects.requireNonNull(selectedAlgorithm)) {
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
        inputFrame.setSize(500, 300);
        inputFrame.setMinimumSize(new Dimension(500, 300));
        inputFrame.setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Add padding to the sides using EmptyBorder
        int paddingSize = 10; // Adjust this value as needed
        inputPanel.setBorder(BorderFactory.createEmptyBorder(paddingSize, paddingSize, paddingSize, paddingSize));

        // Get the number of processes
        int numberOfProcesses = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of processes:"));

        for (int i = 0; i < numberOfProcesses; i++) {
            JPanel processPanel = new JPanel(new GridLayout(1, 3, 10, 10));

            processPanel.add(new JLabel("Process " + (i + 1)));
            processPanel.add(new JLabel("Arrival Time"));
            processPanel.add(new JLabel("Burst Time"));

            JTextField arrivalField = new JTextField();
            JTextField burstField = new JTextField();

            processPanel.add(arrivalField);
            processPanel.add(burstField);

            inputPanel.add(processPanel); // Add the processPanel directly

            // Add a vertical strut for even spacing, except after the last process
            if (i < numberOfProcesses - 1) {
                inputPanel.add(Box.createVerticalStrut(10));
            }
        }

        JButton submitInputButton = new JButton("Submit Input");
        submitInputButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add padding below submit input
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(submitInputButton);

        submitInputButton.addActionListener(e -> {
            try {
                int[] arrivalTime = new int[numberOfProcesses];
                int[] burstTime = new int[numberOfProcesses];

                Component[] components = inputPanel.getComponents();
                int count = 0;

                for (Component component : components) {
                    if (component instanceof JPanel) {
                        JPanel processPanel = (JPanel) component;
                        JTextField arrivalField = (JTextField) processPanel.getComponent(3);
                        JTextField burstField = (JTextField) processPanel.getComponent(4);

                        arrivalTime[count] = Integer.parseInt(arrivalField.getText());
                        burstTime[count] = Integer.parseInt(burstField.getText());
                        count++;
                    }
                }

                // Call the SJF logic with the input values
                sjfLogic(arrivalTime, burstTime);
                // Close the input frame after processing
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Please enter valid integers for arrival and burst time.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Create a JScrollPane and set its view to the inputPanel
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setBorder(null);  // Remove the border from the scroll pane
        inputFrame.setContentPane(scrollPane);

        inputFrame.setVisible(true);
    }


    private void sjfLogic(int[] arrivalTime, int[] burstTime) {
        int n = arrivalTime.length;

        // Variables to store waiting time and turnaround time
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];

        // Array to track whether a process has been executed
        boolean[] executed = new boolean[n];

        int currentTime = 0;
        int completedProcesses = 0;

        while (completedProcesses < n) {
            int shortestJob = -1;
            int shortestBurst = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!executed[i] && arrivalTime[i] <= currentTime && burstTime[i] < shortestBurst) {
                    shortestJob = i;
                    shortestBurst = burstTime[i];
                }
            }

            if (shortestJob != -1) {
                waitingTime[shortestJob] = currentTime - arrivalTime[shortestJob];
                turnaroundTime[shortestJob] = waitingTime[shortestJob] + burstTime[shortestJob];
                currentTime += burstTime[shortestJob];
                executed[shortestJob] = true;
                completedProcesses++;
            } else {
                currentTime++;
            }
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
        inputFrame.setSize(500, 300);
        inputFrame.setMinimumSize(new Dimension(500, 300));

        // Center the JFrame on the display
        inputFrame.setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Add padding to the sides using EmptyBorder
        int paddingSize = 10; // Adjust this value as needed
        inputPanel.setBorder(BorderFactory.createEmptyBorder(paddingSize, paddingSize, paddingSize, paddingSize));

        // Get the number of processes
        int numberOfProcesses = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of processes:"));

        for (int i = 0; i < numberOfProcesses; i++) {
            JPanel processPanel = new JPanel(new GridLayout(1, 3, 10, 10));

            processPanel.add(new JLabel("Process " + (i + 1)));
            processPanel.add(new JLabel("Arrival Time"));
            processPanel.add(new JLabel("Burst Time"));

            JTextField arrivalField = new JTextField();
            JTextField burstField = new JTextField();

            processPanel.add(arrivalField);
            processPanel.add(burstField);

            inputPanel.add(processPanel);

            // Add a vertical strut for even spacing, except after the last process
            if (i < numberOfProcesses - 1) {
                inputPanel.add(Box.createVerticalStrut(10));
            }
        }

        JButton submitInputButton = new JButton("Submit Input");

        // Center the submit button
        submitInputButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add a vertical strut for even spacing below the "Submit Input" button
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(submitInputButton);

        submitInputButton.addActionListener(e -> {
            // Validate and collect input from the fields
            try {
                int[] arrivalTime = new int[numberOfProcesses];
                int[] burstTime = new int[numberOfProcesses];

                Component[] components = inputPanel.getComponents();
                int processPanelCount = 0; // Track the number of valid process panels

                for (Component component : components) {
                    if (component instanceof JPanel) {
                        JPanel processPanel = (JPanel) component;
                        JTextField arrivalField = (JTextField) processPanel.getComponent(3);
                        JTextField burstField = (JTextField) processPanel.getComponent(4);

                        // Validate input as integers
                        arrivalTime[processPanelCount] = Integer.parseInt(arrivalField.getText());
                        burstTime[processPanelCount] = Integer.parseInt(burstField.getText());

                        processPanelCount++;
                    }
                }

                // Call the SRTF logic with the input values
                srtfLogic(arrivalTime, burstTime);
                // Close the input frame after processing
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Please enter valid integers for arrival and burst time.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Create a JScrollPane and set its view to the inputPanel
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setBorder(null);  // Remove the border from the scroll pane
        inputFrame.setContentPane(scrollPane);

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
        // Get the current position
        int currentPosition = 0;

        try {
            currentPosition = Integer.parseInt(JOptionPane.showInputDialog("Enter the current position:"));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for the current position.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if an invalid input is provided
        }

        // Get the track size
        int trackSize = 0;

        try {
            trackSize = Integer.parseInt(JOptionPane.showInputDialog("Enter the track size:"));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for the track size.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if an invalid input is provided
        }

        // Get the seek rate
        int seekRate = 0;

        try {
            seekRate = Integer.parseInt(JOptionPane.showInputDialog("Enter the seek rate:"));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for the seek rate.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if an invalid input is provided
        }

        // Get the number of requests
        int numberOfRequests = 0;

        try {
            numberOfRequests = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of requests (max 10):"));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for the number of requests.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if an invalid input is provided
        }

        // Validate the number of requests
        if (numberOfRequests > 10) {
            JOptionPane.showMessageDialog(this, "Maximum number of requests is 10.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if the number of requests exceeds the limit
        }

        int[] requests = new int[numberOfRequests];

        for (int i = 0; i < numberOfRequests; i++) {
            try {
                requests[i] = Integer.parseInt(JOptionPane.showInputDialog("Enter the location of request " + (i + 1) + ":"));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers for the location of requests.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if an invalid input is provided
            }
        }

        // Call the SSTF logic with the input values
        sstfLogic(currentPosition, trackSize, seekRate, requests);
    }

    private void sstfLogic(int currentPosition, int trackSize, int seekRate, int[] requests) {
        int n = requests.length;

        // Copy the requests array to track the remaining requests
        int[] remainingRequests = Arrays.copyOf(requests, n);

        // Variable to store total head movement and seek time
        int totalHeadMovement = 0;
        double totalSeekTime = 0;

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
            totalSeekTime += (double) closest / seekRate;

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
        result.append("Total Seek Time: ").append(totalSeekTime).append("\n");

        outputArea.setText(result.toString());
    }

    public static void main(String[] args) {
        new SchedulerGUI();
    }
}
