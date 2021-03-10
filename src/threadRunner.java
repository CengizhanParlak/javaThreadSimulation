import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

public class threadRunner {

    static int i = 0, threadOrder = 0, l = 0;
    static long delay = 0;
    static int clientOrder = 1;
    static long intervalPeriod = 100;
    static int isAcceptingMC = 0;         //1 accepting -1 responding 0 does nothing
    static int isAcceptingSC = 0;        //accordin to time: 1 accepting -1 responding 0 does nothing
    static boolean isSubClientsOver2 = false;
    static boolean isOver70 = false, isMoreThan2SubClient = false, isBelow0 = false;
    static boolean isDeleted = false;
    static ArrayList<Integer> subCliRequestCount = new ArrayList<>();
    static ArrayList<Integer> whichSubThreadPercentage = new ArrayList<>();
    static int random = 0;
    static int acceptingMC = 100;
    static int respondingMC = 50;
    static int acceptingSC = 50;
    static int respondingSC = 50;
    static ArrayList<Thread> subThreadList = new ArrayList<>();

    static Timer timer = new Timer();
    //static subThreadCreator subThreadManager = new subThreadCreator();      //subthread manager sub thread
    //1st subthread

    ///GUI VARIABLES' INITIALIZATION///
    static JFrame Frame = new JFrame();
    static JPanel container = new JPanel();
    static JScrollPane scrPane;
    static JProgressBar mcPB = new JProgressBar();
    static JPanel mcP = new JPanel();
    static JPanel clientDatas = new JPanel();
    static JPanel buttonPanel = new JPanel();
    static List<JPanel> panels = new ArrayList<>();
    static int c = 175, d = 100;
    static List<JPanel> subpanels = new ArrayList<>();
    static int c1 = 175, d1 = 30;
    static List<JProgressBar> progressBars = new ArrayList<>();

    ///LABELS FOR SUB CLIENTS///
    static JLabel jlabel_1_1 = new JLabel();
    static JLabel jlabel_1_2 = new JLabel();
    static JLabel jlabel_1_3 = new JLabel();
    static JLabel jlabel_1_4 = new JLabel();
    static JLabel sunucuThreadSayisi = new JLabel();
    static JLabel anaSunucuYog = new JLabel();

    static List<JLabel> scLabels = new ArrayList<>();
    static List<JLabel> percentageLabels = new ArrayList<>();

    ///PANEL LOCATIONS' INITIALIZATION///
    static List<Integer> xAxis = new ArrayList<>();
    static List<Integer> yAxis = new ArrayList<>();

    public static class createMainThread extends Thread {

        static subThreadCreator subThrManag = new subThreadCreator();
        static updateThreadClientInfo updThrManag = new updateThreadClientInfo();

        static int mainClientCapacity = 10000;

        static int mainClientRequestCount = 0;

        @Override
        public synchronized void run() {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    random = (int) ((acceptingMC * Math.random()) + 1);
                    if (mainClientRequestCount + random > mainClientCapacity) {
                        random = mainClientCapacity - mainClientRequestCount;
                    }
                    for (i = 0; i < random; i++) {
                        mainClientRequestCount++;
                        mcPB.setValue(mainClientRequestCount);
                        Frame.repaint();
                        Frame.revalidate();
                    }

                    jlabel_1_3.setText("Incoming requests: " + Integer.toString(random));
                    jlabel_1_2.setText("Processes on server: " + mainClientRequestCount);
                    Frame.repaint();
                    Frame.revalidate();
                }
            }, delay, 500);

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    isAcceptingSC = 1;
                    subThrManag.run();
                    updThrManag.run();
                }
            }, delay, 500);

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    random = (int) (respondingMC * Math.random() + 1);
                    if (mainClientRequestCount - random < 0) {
                        random = mainClientRequestCount;
                    }

                    for (i = 0; i < random; i++) {
                        mainClientRequestCount--;
                        mcPB.setValue(mainClientRequestCount);
                        Frame.repaint();
                        Frame.revalidate();
                    }

                    jlabel_1_4.setText("Answered requests: " + Integer.toString(random));
                    jlabel_1_2.setText("Processes on server: " + mainClientRequestCount);
                    Frame.repaint();
                    Frame.revalidate();
                }
            }, delay, 200);

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    isAcceptingSC = -1;
                    subThrManag.run();
                    updThrManag.run();
                }

            }, delay, 300);

        }
    }

    public static class createSubThread extends Thread {

        private static int subCliCap = 5000;
        private static int maxTaskCount = subCliCap * 70 / 100;
        int overflowCheck;

        @Override
        public synchronized void run() {
            if (isAcceptingSC == 1) {
                random = (int) ((acceptingSC * Math.random()) + 1);
                if (createMainThread.mainClientRequestCount - random <= 0) {
                    random = createMainThread.mainClientRequestCount;
                }

                overflowCheck = random + subCliRequestCount.get(threadOrder);

                for (int j = 0; j < random; j++) {
                    subCliRequestCount.set(threadOrder, subCliRequestCount.get(threadOrder) + 1);
                    progressBars.get(threadOrder).setValue(subCliRequestCount.get(threadOrder));
                    Frame.repaint();
                    Frame.revalidate();
                }
                if (overflowCheck > maxTaskCount) {
                    subCliRequestCount.set(threadOrder, overflowCheck / 2);

                    subThreadList.add(createMainThread.subThrManag.createFirstSubThreads());
                    try {
                        subThreadList.get(subThreadList.size() - 1).join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(threadRunner.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    subCliRequestCount.add(overflowCheck / 2);

                    addNewSCPanel(subCliRequestCount.size() - 1);

                    progressBars.get(threadOrder).setValue(subCliRequestCount.get(threadOrder));
                    Frame.repaint();
                    Frame.revalidate();
                }
                scLabels.get(threadOrder * 3).setText("Total processes: " + subCliRequestCount.get(threadOrder));
                scLabels.get(threadOrder * 3 + 1).setText("Incoming processes: " + random);
                Frame.repaint();
                Frame.revalidate();

                createMainThread.mainClientRequestCount -= random;
                mcPB.setValue(createMainThread.mainClientRequestCount);

            } else if (isAcceptingSC == -1) {
                random = (int) ((respondingSC * Math.random()) + 1);

                if (subCliRequestCount.get(threadOrder) - random <= 0) {
                    random = subCliRequestCount.get(threadOrder);
                }
                for (int j = 0; j < random; j++) {
                    subCliRequestCount.set(threadOrder, subCliRequestCount.get(threadOrder) - 1);
                    progressBars.get(threadOrder).setValue(subCliRequestCount.get(threadOrder));
                    Frame.repaint();
                    Frame.revalidate();
                }
                scLabels.get(threadOrder * 3).setText("Total processes: " + subCliRequestCount.get(threadOrder));
                scLabels.get(threadOrder * 3 + 2).setText("Answered processes: " + random);
                Frame.repaint();
                Frame.revalidate();
                if (subCliRequestCount.get(threadOrder) <= 0 && subCliRequestCount.size() > 2) {
                    if (subThreadList.size() - 1 == threadOrder) {
                    } else {
                        l--;
                    }
                    for (int loop3times = 0; loop3times < 3; loop3times++) {
                        scLabels.remove(threadOrder * 3);
                    }
                    subCliRequestCount.remove(threadOrder);
                    subThreadList.remove(threadOrder);
                    clientDatas.remove(percentageLabels.get(threadOrder));
                    Frame.getContentPane().remove(panels.get(threadOrder));
                    Frame.getContentPane().remove(subpanels.get(threadOrder));
                    percentageLabels.remove(threadOrder);
                    whichSubThreadPercentage.remove(threadOrder);
                    progressBars.remove(threadOrder);
                    panels.remove(threadOrder);
                    subpanels.remove(threadOrder);
                    Frame.repaint();
                    Frame.revalidate();

                    shiftThePanels();
                }
            }
        }
    }

    public static class subThreadCreator extends Thread {

        @Override
        public synchronized void run() {
            for (l = 0; l < subThreadList.size(); l++) {
                threadOrder = l;
                subThreadList.get(l).run();
            }
        }

        public static Thread createFirstSubThreads() {
            createSubThread subThr = new createSubThread();
            return subThr;
        }
    }

    private static void setSCPanelLocations() {
        xAxis.add(50);
        xAxis.add(250);
        xAxis.add(450);
        xAxis.add(650);
        xAxis.add(50);
        xAxis.add(250);
        xAxis.add(450);
        xAxis.add(650);
        xAxis.add(50);
        xAxis.add(250);
        xAxis.add(450);
        xAxis.add(650);

        yAxis.add(250);
        yAxis.add(250);
        yAxis.add(250);
        yAxis.add(250);
        yAxis.add(450);
        yAxis.add(450);
        yAxis.add(450);
        yAxis.add(450);
        yAxis.add(650);
        yAxis.add(650);
        yAxis.add(650);
        yAxis.add(650);

    }

    private static void addNewSCPanel(int g) {
        try {
            if (g >= 0) {
                JPanel panel = new JPanel();
                JPanel subpanel = new JPanel();
                JProgressBar progressBar = new JProgressBar();
                JLabel label = new JLabel();
                JLabel label2 = new JLabel();
                label.setText(clientOrder + ". SUB SERVER");
                panel.add(label);
                for (int l = 0; l < 3; l++) {
                    JLabel label3 = new JLabel();
                    scLabels.add(label3);
                    panel.add(scLabels.get(scLabels.size() - 1));
                }

                panel.setBackground(Color.GRAY);                 //setting up Sub Client 1's background color and boundaries

                panel.setBounds(xAxis.get(g), yAxis.get(g), c, d);                     //50,250,175,100

                subpanel.setBackground(new Color(107, 106, 104));//and colors
                subpanel.setBounds(xAxis.get(g), yAxis.get(g) + 100, c1, d1);             //50,350,175,30

                progressBar.setForeground(Color.lightGray);            //setting up Color

                progressBar.setMinimum(0);                             //Minimum and
                progressBar.setMaximum(createSubThread.subCliCap);         //Maximum value for Sub Client 1's Progress bar
                progressBar.setValue(subCliRequestCount.get(g));                //Setting starting value to 0
                progressBar.setBounds(1, 5, 10, 20);                   //Setting boundaries
                progressBar.setStringPainted(true);                    //Progress Bar String granted for permission

                progressBars.add(progressBar);

                subpanel.add(progressBars.get(g));

                percentageLabels.add(label2);

                whichSubThreadPercentage.add(clientOrder);
                clientOrder++;

                clientDatas.add(percentageLabels.get(g));

                panels.add(panel);

                subpanels.add(subpanel);

                Frame.add(panel);

                Frame.add(subpanel);

                Frame.repaint();

                Frame.revalidate();
            }
        } catch (Exception ex) {
            System.out.println("Reached the maximum amount of servers.");
        }
    }

    public static class updateThreadClientInfo extends Thread {

        @Override
        public synchronized void run() {
            sunucuThreadSayisi.setText("Thread-server count: " + (subThreadList.size() + 3)
                    + "-" + (subCliRequestCount.size() + 1));
            anaSunucuYog.setText("main server density: %"
                    + (createMainThread.mainClientRequestCount * 100 / createMainThread.mainClientCapacity));
            for (int l = 0; l < subCliRequestCount.size(); l++) {
                percentageLabels.get(l).setText(whichSubThreadPercentage.get(l) + ". sub server density: %"
                        + ((subCliRequestCount.get(l)) * 100 / createSubThread.subCliCap));
            }
            Frame.repaint();
            Frame.revalidate();
        }
    }

    private static void addButtonsForRandomValueChange() {
        int carpan = 30;
        char eksi = '-';
        char arti = '+';
        List<JLabel> taskCountLabels = new ArrayList<>();
        List<JButton> butonlar = new ArrayList<>();
        for (int l = 0; l < 8; l++) {
            JButton buton = new JButton();
            butonlar.add(buton);
            if (l % 2 == 0) {
                butonlar.get(l).setText(arti + "" + carpan);
            } else {
                butonlar.get(l).setText(eksi + "" + carpan);
            }
        }
        for (int j = 0; j < 4; j++) {
            JLabel label = new JLabel();
            taskCountLabels.add(label);
        }
        for (int j = 0; j < 4; j++) {
            buttonPanel.add(taskCountLabels.get(j));
            buttonPanel.add(butonlar.get(j * 2));
            buttonPanel.add(butonlar.get(j * 2 + 1));
        }
        taskCountLabels.get(0).setText("MAIN SERVER INCOMING (1-" + (acceptingMC + 1) + ")");
        butonlar.get(0).addActionListener((ActionEvent e) -> {
            acceptingMC += 30;
            taskCountLabels.get(0).setText("MAIN SERVER INCOMING (1-" + (acceptingMC + 1) + ")");
        });

        butonlar.get(1).addActionListener((ActionEvent e) -> {
            acceptingMC -= 30;
            if (acceptingMC <= 0) {
                acceptingMC = 0;
            }
            taskCountLabels.get(0).setText("MAIN SERVER INCOMING (1-" + (acceptingMC + 1) + ")");
        });
        taskCountLabels.get(1).setText("MAIN SERVER OUTGOING (1-" + (respondingMC + 1) + ")");
        butonlar.get(2).addActionListener((ActionEvent e) -> {
            respondingMC += 30;
            taskCountLabels.get(1).setText("MAIN SERVER OUTGOING (1-" + (respondingMC + 1) + ")");
        });
        butonlar.get(3).addActionListener((ActionEvent e) -> {
            respondingMC -= 30;
            if (respondingMC <= 0) {
                respondingMC = 0;
            }
            taskCountLabels.get(1).setText("MAIN SERVER OUTGOING (1-" + (respondingMC + 1) + ")");
        });
        taskCountLabels.get(2).setText("SUB SERVER INCOMING (1-" + (acceptingSC + 1) + ")");
        butonlar.get(4).addActionListener((ActionEvent e) -> {
            acceptingSC += 30;
            taskCountLabels.get(2).setText("SUB SERVER INCOMING (1-" + (acceptingSC + 1) + ")");
        });
        butonlar.get(5).addActionListener((ActionEvent e) -> {
            acceptingSC -= 30;
            if (acceptingSC <= 0) {
                acceptingSC = 0;
            }
            taskCountLabels.get(2).setText("SUB SERVER INCOMING (1-" + (acceptingSC + 1) + ")");
        });
        taskCountLabels.get(3).setText("SUB SERVER OUTGOING (1-" + (respondingSC + 1) + ")");
        butonlar.get(6).addActionListener((ActionEvent e) -> {
            respondingSC += 30;
            taskCountLabels.get(3).setText("SUB SERVER OUTGOING (1-" + (respondingSC + 1) + ")");
        });
        butonlar.get(7).addActionListener((ActionEvent e) -> {
            respondingSC -= 30;
            if (respondingSC <= 0) {
                respondingSC = 0;
            }
            taskCountLabels.get(3).setText("SUB SERVER OUTGOING (1-" + (respondingSC + 1) + ")");
        });
    }

    private static void removeSCPanels(int i) {
        scLabels.remove(i * 3);
        scLabels.remove(i * 3 + 1);
        scLabels.remove(i * 3 + 2);
        subCliRequestCount.remove(i);
        subThreadList.remove(i);
        clientDatas.remove(percentageLabels.get(i));
        Frame.getContentPane().remove(panels.get(i));
        Frame.getContentPane().remove(subpanels.get(i));
        percentageLabels.remove(i);
        whichSubThreadPercentage.remove(i);
        progressBars.remove(i);
        panels.remove(i);
        subpanels.remove(i);
        Frame.repaint();
        Frame.revalidate();

    }

    private static void shiftThePanels() {
        for (int i = 0; i < subCliRequestCount.size(); i++) {
            panels.get(i).setBounds(xAxis.get(i), yAxis.get(i), c, d);
            subpanels.get(i).setBounds(xAxis.get(i), yAxis.get(i) + 100, c1, d1);
        }
        Frame.repaint();
        Frame.revalidate();

    }

    public static void main(String[] args) {
        setSCPanelLocations();
        Frame.setLocationRelativeTo(null);
        Frame.setLayout(null);
        Frame.setBounds(100, 50, 1000, 700);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.setResizable(false);

        buttonPanel.setBounds(275, 20, 175, 225);
        buttonPanel.setBackground(Color.GRAY);

        clientDatas.setBounds(550, 20, 250, 225);
        clientDatas.setBackground(Color.GRAY);
        clientDatas.setLayout(new FlowLayout());
        clientDatas.add(sunucuThreadSayisi);
        clientDatas.add(anaSunucuYog);

        JPanel mcP_add = new JPanel();
        JLabel label_1_1 = new JLabel();

        label_1_1.setText("MAIN SERVER");
        mcP.add(label_1_1);
        mcP.add(jlabel_1_2);
        mcP.add(jlabel_1_3);
        mcP.add(jlabel_1_4);

        mcP.setBackground(Color.GRAY);                  //setting up the Main Client panel and the additional
        mcP.setBounds(50, 50, 175, 100);                //Main Client's Progress Bar panel for the given bounds
        mcP_add.setBackground(new Color(107, 106, 104));//and colors
        mcP_add.setBounds(50, 150, 175, 30);            //

        mcPB.setForeground(Color.lightGray);            //setting up Color
        mcPB.setMinimum(0);                             //Minimum and
        mcPB.setMaximum(createMainThread.mainClientCapacity);                         //Maximum value for Main Client's Progress bar
        mcPB.setValue(0);                               //Setting starting value to 0
        mcPB.setBounds(1, 5, 10, 20);                   //Setting boundaries
        mcPB.setStringPainted(true);                    //Progress Bar String granted for permission

        mcP_add.add(mcPB);

        addButtonsForRandomValueChange();

        createMainThread isParMain = new createMainThread();

        try {
            isParMain.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(threadRunner.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try {                                                               //1st sub thread created at the very beginning
            createMainThread.subThrManag.join();                                             //of the program
        } catch (InterruptedException ex) {
            Logger.getLogger(threadRunner.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try {                                                               //1st sub thread created at the very beginning
            createMainThread.updThrManag.join();                                           //of the program
        } catch (InterruptedException ex) {
            Logger.getLogger(threadRunner.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        for (int j = 0; j < 2; j++) {
            subThreadList.add(createMainThread.subThrManag.createFirstSubThreads());
            subCliRequestCount.add(0);
            try {
                subThreadList.get(j).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(threadRunner.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        addNewSCPanel(0);
        addNewSCPanel(1);
        Frame.add(buttonPanel);
        Frame.add(clientDatas);
        Frame.add(mcP);
        Frame.add(mcP_add);

        Frame.repaint();
        Frame.revalidate();
        Frame.setVisible(true);

        isParMain.run();
    }

}
