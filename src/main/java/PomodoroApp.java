import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PomodoroApp {
    private static String userId;
    private static int timeLeft = 25 * 60;
    private static Timer pomodoroTimer;

    // UI Components for the 4 Quadrants holding the full Task objects
    private static DefaultListModel<TaskRepository.Task> doFirstModel = new DefaultListModel<>();
    private static DefaultListModel<TaskRepository.Task> scheduleModel = new DefaultListModel<>();
    private static DefaultListModel<TaskRepository.Task> delegateModel = new DefaultListModel<>();
    private static DefaultListModel<TaskRepository.Task> eliminateModel = new DefaultListModel<>();

    public static void main(String[] args) {
        // 1. Get User ID on Startup
        userId = JOptionPane.showInputDialog(null, "Enter User ID to load tasks:");
        if (userId == null || userId.trim().isEmpty()) System.exit(0);

        // 2. Setup Main Window
        JFrame frame = new JFrame("Pomodoro Matrix - " + userId);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setLayout(new BorderLayout());

        // 3. Top Panel: Timer & Inputs
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel timerLabel = new JLabel("25:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.RED);

        JButton startTimerBtn = new JButton("Start Pomodoro");
        JTextField taskInput = new JTextField(15);
        JComboBox<TaskRepository.Category> categoryBox = new JComboBox<>(TaskRepository.Category.values());
        JButton addTaskBtn = new JButton("Add Task");

        topPanel.add(timerLabel);
        topPanel.add(startTimerBtn);
        topPanel.add(new JLabel("  |  New Task:"));
        topPanel.add(taskInput);
        topPanel.add(categoryBox);
        topPanel.add(addTaskBtn);

        frame.add(topPanel, BorderLayout.NORTH);

        // 4. Center Panel: Eisenhower Matrix (2x2 Grid)
        JPanel matrixPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        matrixPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        matrixPanel.add(createQuadrant("DO FIRST (Urgent/Important)", doFirstModel, new Color(255, 200, 200)));
        matrixPanel.add(createQuadrant("SCHEDULE (Not Urgent/Important)", scheduleModel, new Color(200, 220, 255)));
        matrixPanel.add(createQuadrant("DELEGATE (Urgent/Not Important)", delegateModel, new Color(255, 230, 180)));
        matrixPanel.add(createQuadrant("ELIMINATE (Not Urgent/Not Important)", eliminateModel, new Color(220, 220, 220)));

        frame.add(matrixPanel, BorderLayout.CENTER);

        // 5. Load Existing Data from Supabase
        loadDataIntoUI();

        // 6. Action Listeners
        addTaskBtn.addActionListener(e -> {
            String text = taskInput.getText();
            if (!text.isEmpty()) {
                TaskRepository.Category cat = (TaskRepository.Category) categoryBox.getSelectedItem();
                // Creates a new task (generates a UUID automatically)
                TaskRepository.Task newTask = new TaskRepository.Task(text, cat);

                // Save to Cloud
                TaskRepository.saveTask(userId, newTask);

                // Add to Screen
                addTaskToList(newTask);
                taskInput.setText("");
            }
        });

        // Timer Logic
        pomodoroTimer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                timerLabel.setText(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
            } else {
                pomodoroTimer.stop();
                JOptionPane.showMessageDialog(frame, "Pomodoro complete! Take a break.");
                timeLeft = 25 * 60;
                timerLabel.setText("25:00");
                startTimerBtn.setText("Start Pomodoro");
            }
        });

        startTimerBtn.addActionListener(e -> {
            if (pomodoroTimer.isRunning()) {
                pomodoroTimer.stop();
                startTimerBtn.setText("Start Pomodoro");
            } else {
                pomodoroTimer.start();
                startTimerBtn.setText("Pause Pomodoro");
            }
        });

        frame.setVisible(true);
    }

    // Helper: Creates a styled JList quadrant with double-click deletion
    private static JPanel createQuadrant(String title, DefaultListModel<TaskRepository.Task> model, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(bgColor);

        JList<TaskRepository.Task> list = new JList<>(model);
        list.setBackground(bgColor);

        // Double-Click to Delete/Complete
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    TaskRepository.Task selected = list.getSelectedValue();
                    if (selected != null) {
                        int confirm = JOptionPane.showConfirmDialog(
                                panel,
                                "Mark '" + selected.name + "' as completed?",
                                "Complete Task",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            TaskRepository.deleteTask(selected.id);
                            model.removeElement(selected);
                        }
                    }
                }
            }
        });

        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    // Helper: Fetch from DB and populate UI
    private static void loadDataIntoUI() {
        List<TaskRepository.Task> tasks = TaskRepository.loadTasks(userId);
        for (TaskRepository.Task task : tasks) {
            addTaskToList(task);
        }
    }

    // Helper: Route task to correct UI quadrant
    private static void addTaskToList(TaskRepository.Task task) {
        switch (task.category) {
            case URGENT_IMPORTANT: doFirstModel.addElement(task); break;
            case NOT_URGENT_IMPORTANT: scheduleModel.addElement(task); break;
            case URGENT_NOT_IMPORTANT: delegateModel.addElement(task); break;
            case NOT_URGENT_NOT_IMPORTANT: eliminateModel.addElement(task); break;
        }
    }
}