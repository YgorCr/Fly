package flygui;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Igor
 */
public class CreationScreen extends javax.swing.JFrame {

    /**
     * Creates new form CreationScreen
     * @param f
     */
    public CreationScreen(File f) {
        this.file1 = f;
        
        initComponents();
        
        this.jTextField1.setEditable(false);
        
        this.jComboBox3.removeAllItems();
        this.jComboBox3.addItem("Year");
        for(int i = Calendar.getInstance().get(Calendar.YEAR); i >= 2000; --i)
            this.jComboBox3.addItem(i);
    }
    
    public void convertion(String station, String day, String month, String year, int dependence) throws Exception{
        Connection con = Jsoup.connect("http://www.wunderground.com/weatherstation/WXDailyHistory.asp?"
                + "ID=" + station + "&"
                + "day=" + day + "&"
                + "month=" + month + "&"
                + "year=" + year + "&"
                + "graphspan=day&"
                + "format=1");
        
        String human_readable = "";
        try {
            human_readable = con.ignoreContentType(true).get().body().html()
                    .replaceAll("<br>", "")
                    .replaceAll(", ", "");
        } catch (IOException ex) {
            Logger.getLogger(CreationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int dayI = Integer.parseInt(day);
        int monthI = Integer.parseInt(month);
        int yearI = Integer.parseInt(year);
        
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.set(yearI, monthI, dayI);
        dayBefore.add(Calendar.DAY_OF_YEAR, -1);
        
        con = Jsoup.connect("http://www.wunderground.com/weatherstation/WXDailyHistory.asp?"
                + "ID=" + station + "&"
                + "day=" + dayBefore.get(Calendar.DAY_OF_MONTH) + "&"
                + "month=" + dayBefore.get(Calendar.MONTH) + "&"
                + "year=" + dayBefore.get(Calendar.YEAR) + "&"
                + "graphspan=day&"
                + "format=1");
        
        String human_readable2 = "";
        try {
            human_readable2 = con.ignoreContentType(true).get().body().html()
                    .replaceAll("<br>", "")
                    .replaceAll(", ", "");
        } catch (IOException ex) {
            Logger.getLogger(CreationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        CSVparser parserWeather = new CSVparser(human_readable);
        parserWeather.removeColumn(4);
        parserWeather.removeColumn(9);
        parserWeather.removeColumn(9);
        parserWeather.removeColumn(11);
        parserWeather.removeColumn(11);
        
        CSVparser parserWeather2 = new CSVparser(human_readable2);
        parserWeather2.removeColumn(4);
        parserWeather2.removeColumn(9);
        parserWeather2.removeColumn(9);
        parserWeather2.removeColumn(11);
        parserWeather2.removeColumn(11);
        
        for(int l = parserWeather2.getNumberOfLines()-1; l > 0; --l){
            parserWeather.insertLine(parserWeather2.getLine(l), 1);
        }
        parserWeather2 = null;
        
        String file1 = "";
        for (String line : Files.readAllLines(Paths.get(this.file1.getAbsolutePath()))) {
            file1 = file1 + line + "\n";
        }
        
        CSVparser parserOviposition = new CSVparser(file1);
        
        String weka = "";
                
        for(int dep = 0; dep <= dependence; ++dep){
            CSVparser parserFinal = new CSVparser(parserOviposition.getNumberOfLines(), parserWeather.getNumberOfColumns()+parserOviposition.getNumberOfColumns()-1);
            
            for(int i = 1; i < parserOviposition.getNumberOfLines(); ++i){
                String[] oviTime = parserOviposition.getCell(i, 2).split(":");
                String[] oviDate = parserOviposition.getCell(i, 1).split("/");

                int oviDay = Integer.parseInt(oviDate[1]);
                int oviMonth = Integer.parseInt(oviDate[0]);
                int oviYear = Integer.parseInt(oviDate[2]);
                if(oviYear < 2000){
                    oviYear += 2000;
                }

                int oviHour = Integer.parseInt(oviTime[0]);
                int oviMinutes = Integer.parseInt(oviTime[1]);
                int oviSeconds = Integer.parseInt(oviTime[2]);

                Calendar time = Calendar.getInstance();
                time.set(Calendar.AM_PM, 0);
                time.set(Calendar.HOUR, oviHour);
                time.set(Calendar.MINUTE, oviMinutes);
                time.set(Calendar.SECOND, oviSeconds);
                time.set(Calendar.DAY_OF_MONTH, oviDay);
                time.set(Calendar.MONTH, oviMonth);
                time.set(Calendar.YEAR, oviYear);
                
                time.add(Calendar.HOUR, -dep);
                
                long oviTotalSeconds = time.getTimeInMillis();

                for(int j = 1; j < parserWeather.getNumberOfLines(); ++j){
                    String[] weatherTime1 = parserWeather.getCell(j, 0).split(" ");
                    String[] weatherDate1 = weatherTime1[1].split("-");
                    weatherTime1 = weatherTime1[2].split(":");

                    int weatherDay1 = Integer.parseInt(weatherDate1[2]);
                    int weatherMonth1 = Integer.parseInt(weatherDate1[1]);
                    int weatherYear1 = Integer.parseInt(weatherDate1[0]);

                    int weatherHour1 = Integer.parseInt(weatherTime1[0]);
                    int weatherMinutes1 = Integer.parseInt(weatherTime1[1]);
                    int weatherSeconds1 = Integer.parseInt(weatherTime1[2]);
                    
                    time.set(Calendar.AM_PM, 0);
                    time.set(Calendar.HOUR, weatherHour1);
                    time.set(Calendar.MINUTE, weatherMinutes1);
                    time.set(Calendar.SECOND, weatherSeconds1);
                    time.set(Calendar.DAY_OF_MONTH, weatherDay1);
                    time.set(Calendar.MONTH, weatherMonth1);
                    time.set(Calendar.YEAR, weatherYear1);

                    long weatherTotalSeconds1 = time.getTimeInMillis();

                    if(oviTotalSeconds < weatherTotalSeconds1)
                        continue;

                    boolean oneIsCloserThanTwo = true;
                    if(j+1 != parserWeather.getNumberOfLines()){
                        String[] weatherTime2 = parserWeather.getCell(j+1, 0).split(" ");
                        String[] weatherDate2 = weatherTime2[1].split("-");
                        weatherTime2 = weatherTime2[2].split(":");

                        int weatherDay2 = Integer.parseInt(weatherDate2[2]);
                        int weatherMonth2 = Integer.parseInt(weatherDate2[1]);
                        int weatherYear2 = Integer.parseInt(weatherDate2[0]);

                        int weatherHour2 = Integer.parseInt(weatherTime2[0]);
                        int weatherMinutes2 = Integer.parseInt(weatherTime2[1]);
                        int weatherSeconds2 = Integer.parseInt(weatherTime2[2]);

                        time.set(Calendar.AM_PM, 0);
                        time.set(Calendar.HOUR, weatherHour2);
                        time.set(Calendar.MINUTE, weatherMinutes2);
                        time.set(Calendar.SECOND, weatherSeconds2);
                        time.set(Calendar.DAY_OF_MONTH, weatherDay2);
                        time.set(Calendar.MONTH, weatherMonth2);
                        time.set(Calendar.YEAR, weatherYear2);

                        long weatherTotalSeconds2 = time.getTimeInMillis();

                        long dif1 = Math.abs(oviTotalSeconds - weatherTotalSeconds1);
                        long dif2 = Math.abs(weatherTotalSeconds2 - oviTotalSeconds);

                        oneIsCloserThanTwo = dif2 > dif1;
                    }

                    String[] line = new String[parserFinal.getNumberOfColumns()];
                    
                    
                    int cellNumber = 0;
                    for (int k = 0; k < parserOviposition.getNumberOfColumns()-1; ++k)
                        line[cellNumber++] = parserOviposition.getCell(i, k);


                    if(oneIsCloserThanTwo)
                        for (int k = 1; k < parserWeather.getNumberOfColumns(); ++k)
                            line[cellNumber++] = parserWeather.getCell(j, k);
                    else
                        for (int k = 1; k < parserWeather.getNumberOfColumns(); ++k)
                            line[cellNumber++] = parserWeather.getCell(j+1, k);
                    
                    line[line.length-1] = parserOviposition.getCell(i, parserOviposition.getNumberOfColumns() - 1);

                    parserFinal.setLine(line, i);
                }
            }
            
            if(dep == dependence){
                parserOviposition.removeColumn(1);
                parserOviposition.removeColumn(1);
                parserFinal.removeColumn(1);
                parserFinal.removeColumn(1);
            }
            
            //Building the header
            String line[] = new String[parserFinal.getNumberOfColumns()];
            weka = "@relation fly_eggs\n";
            human_readable = "";
            int cellNumber = 0;
            for (int k = 0; k < parserOviposition.getNumberOfColumns() - 1; ++k){
                String headTitle = parserOviposition.getCell(0, k);
                weka = weka +  "@attribute '" + headTitle + "' real\n";
                human_readable = human_readable + headTitle + ",";
                line[cellNumber++] = headTitle;
            }

            for (int k = 1; k < parserWeather.getNumberOfColumns(); ++k){
                String headTitle = parserWeather.getCell(0, k);
                int hbf = 1;
                while(weka.contains(headTitle)){
                    headTitle = parserWeather.getCell(0, k) + " " + hbf + "h bf";
                    hbf++;
                }
                weka = weka +  "@attribute '" + headTitle + "' real\n";
                human_readable = human_readable + headTitle + ",";
                line[cellNumber++] = headTitle;
            }
            
            weka = weka +  "@attribute '" + parserOviposition.
                    getCell(0, parserOviposition.getNumberOfColumns() - 1)
                    + "' { FALSE, TRUE}\n"
                    + "@data\n";
            human_readable = human_readable + parserOviposition.
                    getCell(0, parserOviposition.getNumberOfColumns() - 1) + "\n";
            line[cellNumber++] = parserOviposition.
                    getCell(0, parserOviposition.getNumberOfColumns() - 1);
            
            parserFinal.setLine(line, 0);
            parserOviposition = parserFinal;
            
            if(dep == dependence){
                parserFinal.removeLine(0);
            }
            
            human_readable = human_readable + parserFinal.toString();
            weka = weka + parserFinal.toString();
        }
        
        try{
            //Saves the human readible version
            String humanReadableFileName = this.file2.getAbsolutePath();
            int ext = humanReadableFileName.lastIndexOf(".");
            humanReadableFileName = humanReadableFileName.substring(0, ext) + "_humanReadable.csv";
            Path outputPath = Paths.get(humanReadableFileName);
            BufferedWriter writer = Files.newBufferedWriter(outputPath, Charset.forName("UTF-8"));
            writer.write(human_readable);
            writer.close();
            
            //Saves the Weka compatible version
            Path outputPath2 = Paths.get(this.file2.getAbsolutePath());
            BufferedWriter writer2 = Files.newBufferedWriter(outputPath2, Charset.forName("UTF-8"));
            writer2.write(weka);
            writer2.close();
            
        } catch (IOException x) {
            x.printStackTrace();
            System.err.format("IOException: %s%n", x);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jButton3 = new javax.swing.JButton();
        jSpinner1 = new javax.swing.JSpinner();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();

        jButton2.setText("jButton2");

        jTextField2.setText("jTextField2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Date (MM/DD/YYY):");

        jLabel2.setText("Dependence:");

        jButton1.setText("Create");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        jButton4.setText("Save to:");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Month", "January (1)", "February (2)", "March (3)", "April (4)", "May (5)", "June (6)", "July (7)", "August (8)", "September (9)", "October (10)", "November (11)", "December (12)" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Day", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Hour(s)");

        jLabel4.setText("Station:");

        jTextField3.setText("KINVALPA27");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 115, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addGap(37, 37, 37)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.setVisible(false);
        this.dispose();
        FirstScreen first = new FirstScreen();
        first.setLocationRelativeTo(null);
        first.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        first.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        this.jComboBox2.removeAllItems();
        this.jComboBox2.addItem("Day");
        int monthNumber = this.jComboBox1.getSelectedIndex();
        if(monthNumber == 2){
            for(int i = 1; i < 30 ; ++i){
                this.jComboBox2.addItem(i);
            }
        }
        else if(monthNumber%2!=0 && monthNumber <= 7){
            for(int i = 1; i < 32 ; ++i){
                this.jComboBox2.addItem(i);
            }
        }
        else if(monthNumber%2==0 && monthNumber > 7){
            for(int i = 1; i < 32 ; ++i){
                this.jComboBox2.addItem(i);
            }
        }
        else{
            for(int i = 1; i < 31 ; ++i){
                this.jComboBox2.addItem(i);
            }
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser saveDialog = new JFileChooser();
        saveDialog.setFileFilter(new FileNameExtensionFilter("ARFF - weka file", "arff"));
        int option = saveDialog.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
                file2 = saveDialog.getSelectedFile();
                if(!file2.getAbsolutePath().endsWith(".arff")){
                    String fullPath = file2.getAbsolutePath();
                    file2.delete();
                    file2 = new File(fullPath + ".arff");
                }
                this.jTextField1.setText(file2.getAbsolutePath());
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(file2 == null){
            JOptionPane.showMessageDialog(null, "Please! Select a place to save and a name for the result file before start converting!");
        }

        String station = jTextField3.getText();
        String day = "" + jComboBox2.getSelectedItem();
        String month = "" + jComboBox1.getSelectedIndex();
        String year = "" + jComboBox3.getSelectedItem();
        int dependence = (int) jSpinner1.getValue();
        
        
        try {
            convertion(station, day, month, year, dependence);
        } catch (Exception ex) {
            Logger.getLogger(CreationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JOptionPane.showMessageDialog(null, "Convertion finished sucessfully!");
        
        this.setVisible(false);
        this.dispose();
        FirstScreen first = new FirstScreen();
        first.setLocationRelativeTo(null);
        first.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        first.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreationScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreationScreen(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
    private File file1;
    private File file2;
}
