package library_management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
 
import javax.swing.*;
import net.proteanit.sql.DbUtils;
 
public class Library_management  {
     
    public static class ex{
        public static int days=0;
            }
 
    public static void main(String[] args) {
        
        login();
        //create();
        
    }

    
    
    public static void login(){
        JFrame f = new JFrame("Login");
        JLabel l1,l2;
        l1=new JLabel("Username");
        l1.setBounds(30,15,100,30);
        
        l2=new JLabel("Password");
        l2.setBounds(30,50, 100,30); 
        
        JTextField F_user = new JTextField();
        F_user.setBounds(110,15, 200, 30);
        
        
        
        JPasswordField F_password = new JPasswordField();
        F_password.setBounds(110, 50, 200, 30);
        
        JButton login_button=new JButton("Login");
        login_button.setBounds(130,90,80,25);
        login_button.addActionListener(new ActionListener(){                
                public void actionPerformed(ActionEvent e){
                    String username= F_user.getText();
                    String password= F_password.getText();
                    
                    if(username.equals("")){
                        JOptionPane.showMessageDialog(null,"Please enter username!" );                        
                    }
                    else if(password.equals("")){
                        JOptionPane.showMessageDialog(null,"Please enter password!" );                                                
                    }
                    else{//ikisi de dogruysa db connection kur
                        Connection connection = connect();
                        try{
                            Statement stmnt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            stmnt.executeUpdate("USE library_management");
                            String str = ("SELECT * FROM USERS WHERE USERNAME = '"+username+"' AND PASSWORD = 'admin'");
                            ResultSet rs= stmnt.executeQuery(str);
                            rs.beforeFirst(); // Now you can move the cursor to the beginning

                            if(rs.next()==false){
                               System.out.print("User cannot found");
                               JOptionPane.showMessageDialog(null, "Wrong username or password");
                            }
                            else{
                                f.dispose();
                                rs.beforeFirst();
                                while(rs.next()){
                                    String admin=rs.getString("ADMIN");
                                    String userID=rs.getString("UID");
                                    if(admin.equals("1")){
                                        admin_menu();//admin menuye don
                                    }
                                    else{
                                        user_menu(userID);
                                    }
                                }
                            }
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }
                    } 
                }
             }
        );
        
    
    f.add(login_button); 
    f.add(F_user);
    f.add(F_password); 
    f.add(l1); 
    f.add(l2); 
        
    f.setSize(400,180); 
    f.setLayout(null);
    f.setVisible(true);
    f.setLocationRelativeTo(null);
                    
        
    }
    
    
    
    public static Connection connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con =DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=1234");
            return con;
        }catch(Exception ex){
            ex.printStackTrace();
        }       
        return null;
    }
    
    public static void create(){
        try{
            Connection connection=connect();
            ResultSet rs = connection.getMetaData().getCatalogs();
            while(rs.next()){
                String dbName =rs.getString(1);
                if(dbName.equals("library_management")){
                    Statement stmnt =connection.createStatement();
                    String sql ="DROP DATABASE library_management";
                    stmnt.executeUpdate(sql);
                }
            }
            
            Statement stmnt = connection.createStatement();
            
            String sql = "CREATE DATABASE library_management";
            stmnt.executeUpdate(sql);
            String sql1="CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(30), PASSWORD VARCHAR(30),ADMIN BOOLEAN)";
            stmnt.executeUpdate(sql1);          
            stmnt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, ADMIN) VALUES('admin','admin',TRUE)");
            stmnt.executeUpdate("CREATE TABLE BOOKS(BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, BNAME VARCHAR(50), GENRE VARCHAR(20), PRICE INT)");            
            stmnt.executeUpdate("CREATE TABLE ISSUED(IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UID INT, BID INT, ISSUED_DATE VARCHAR(20), RETURN_DATE VARCHAR(20), PERIOD INT, FINE INT)");
            stmnt.executeUpdate("INSERT INTO BOOKS(BNAME, GENRE, PRICE) VALUES ('War and Peace', 'Mystery', 200),  ('The Guest Book', 'Fiction', 300), ('The Perfect Murder','Mystery', 150), ('Accidental Presidents', 'Biography', 250), ('The Wicked King','Fiction', 350)");
            rs.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void admin_menu(){
        JFrame f = new JFrame("Admin Functions");      
        
        JButton create_button=new JButton("Create/Reset");
        create_button.setBounds(450,60,120,25);
        create_button.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e){

                create(); 
                JOptionPane.showMessageDialog(null,"Database Created/Reset!"); 

            }
        });
        

        JButton view_button=new JButton("View Books");
        view_button.setBounds(20,20,120,25);
        view_button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
             
            JFrame f = new JFrame("Books Available");              
             
            Connection connection = connect(); 
            String sql="select * from BOOKS"; 
            try {
                Statement stmnt = connection.createStatement();
                stmnt.executeUpdate("USE library_management"); 
                stmnt=connection.createStatement();
                ResultSet rs=stmnt.executeQuery(sql);
                JTable book_list= new JTable(); 
                book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                JScrollPane scrollPane = new JScrollPane(book_list); 
                f.add(scrollPane);
                f.setSize(800, 400); 
                f.setVisible(true);
                f.setLocationRelativeTo(null);
            } catch (SQLException e1) {

                 JOptionPane.showMessageDialog(null, e1);
            }               
             
    }
    }
    );
        
        JButton users_button= new JButton("View Users");
        users_button.setBounds(150,20,120,25);
        users_button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame f = new JFrame("User List");
                
                Connection connection=connect();
                String sql ="SELECT * FROM USERS";
                try {
                    Statement stmnt = connection.createStatement();
                    stmnt.executeUpdate("USE library_management"); //use database
                    stmnt=connection.createStatement();
                    ResultSet rs=stmnt.executeQuery(sql);
                    JTable book_list= new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                    
                    JScrollPane scrollPane = new JScrollPane(book_list);
 
                    f.add(scrollPane); 
                    f.setSize(800, 400); 
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                     JOptionPane.showMessageDialog(null, e1);
                }  
            }
        });
        JButton issued_button=new JButton("View Issued Books");//creating instance of JButton to view the issued books
        issued_button.setBounds(280,20,160,25);//x axis, y axis, width, height 
        issued_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                JFrame f = new JFrame("Users List");
                
                Connection connection = connect();
                String sql="select * from issued";
                try {
                    Statement stmnt = connection.createStatement();
                    stmnt.executeUpdate("USE library_management");
                    stmnt=connection.createStatement();
                    ResultSet rs=stmnt.executeQuery(sql);
                    JTable book_list= new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 

                    JScrollPane scrollPane = new JScrollPane(book_list);

                    f.add(scrollPane);
                    f.setSize(800, 400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                }catch (SQLException e1) {
                         JOptionPane.showMessageDialog(null, e1);
                }       

            }
        });
        
        JButton add_user=new JButton("Add User");
        add_user.setBounds(20,60,120,25);
        add_user.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame g = new JFrame("Enter User Details");
                
                JLabel l1,l2;
                l1=new JLabel("Username");
                l1.setBounds(20,15,100,30);
                l2=new JLabel("Password");
                l2.setBounds(30,50,100,30);
                
                JTextField F_user=new JTextField();
                F_user.setBounds(110, 15, 200, 30);
                JPasswordField F_password=new JPasswordField();
                F_password.setBounds(110, 50, 200, 30);
                JRadioButton a1= new JRadioButton("Admin");
                a1.setBounds(110,50,200,30);
                JRadioButton a2 = new JRadioButton("User");
                a2.setBounds(130, 80, 200,30);
                ButtonGroup bg=new ButtonGroup();
                bg.add(a1);
                bg.add(a1);
                
                JButton create_button=new JButton("Create");
                create_button.setBounds(130,130,80,25);
                create_button.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        String username=F_user.getText();
                        String password=F_password.getText();
                        Boolean admin=false;
                        if(a1.isSelected()) {
                            admin=true;
                        }
                        
                        Connection connection=connect();
                        try {
                            Statement stmnt = connection.createStatement();
                             stmnt.executeUpdate("USE library_management");
                             stmnt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('"+username+"','"+password+"',"+admin+")");
                             JOptionPane.showMessageDialog(null,"User succesfully added!");
                             g.dispose();

                        }catch (SQLException e1) {
                             JOptionPane.showMessageDialog(null, e1);
                        }                         
                       
                    }
                });
                g.add(create_button);
                g.add(a2);
                g.add(a1);
                g.add(l1);
                g.add(l2);
                g.add(F_user);
                g.add(F_password);
                g.setSize(350,200);
                g.setLayout(null); 
                g.setVisible(true);
                g.setLocationRelativeTo(null);                

            }
        
        });
    JButton add_book=new JButton("Add Book"); 
    add_book.setBounds(150,60,120,25); 
     
    add_book.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
            
            JFrame g = new JFrame("Enter Book Details");
            
            JLabel l1,l2,l3;  
            l1=new JLabel("Book Name");
            l1.setBounds(30,15, 100,30);      
               
            l2=new JLabel("Genre");
            l2.setBounds(30,53, 100,30); 
             
            l3=new JLabel("Price");
            l3.setBounds(30,90, 100,30); 
                
            JTextField F_bname = new JTextField();
            F_bname.setBounds(110, 15, 200, 30);                
                
            JTextField F_genre=new JTextField();
            F_genre.setBounds(110, 53, 200, 30);
                
            JTextField F_price=new JTextField();
            F_price.setBounds(110, 90, 200, 30);
                            
            JButton create_button=new JButton("Submit"); 
            create_button.setBounds(130,130,80,25); 
            create_button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){                    
                    String bname = F_bname.getText();
                    String genre = F_genre.getText();
                    String price = F_price.getText();

                    int price_int = Integer.parseInt(price);
                    Connection connection = connect();

                    try {
                        Statement stmnt = connection.createStatement();
                        stmnt.executeUpdate("USE library_management");
                        stmnt.executeUpdate("INSERT INTO BOOKS(BNAME,GENRE,PRICE) VALUES ('"+bname+"','"+genre+"',"+price_int+")");
                        JOptionPane.showMessageDialog(null,"Book added!");
                        g.dispose();
                    }                   
                    catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, e1);
                    }   
                     
                }
                    
            });
                                 
            g.add(l3);
            g.add(create_button);
            g.add(l1);
            g.add(l2);
            g.add(F_bname);
            g.add(F_genre);
            g.add(F_price);
            g.setSize(350,200); 
            g.setLayout(null);  
            g.setVisible(true);
            g.setLocationRelativeTo(null);
                             
        }
    });
    
    JButton issue_book=new JButton("Issue Book");
    issue_book.setBounds(450,20,120,25);
    
    issue_book.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
            JFrame g =new JFrame("Enter Details");
            
            JLabel l1,l2,l3,l4;  
            l1=new JLabel("Book ID(BID)");
            l1.setBounds(30,15, 100,30);      
               
            l2=new JLabel("USER ID");
            l2.setBounds(30,53,100,30); 
             
            l3=new JLabel("Period(days)");
            l3.setBounds(30,90, 100,30);
            
            l4=new JLabel("Issued Date(DD-MM-YYYY)");
            l4.setBounds(110,127,150,30); 
                
            JTextField F_bid = new JTextField();
            F_bid.setBounds(110, 15, 200, 30);                
                
            JTextField F_uid=new JTextField();
            F_uid.setBounds(110, 53, 200, 30);
                
            JTextField F_period=new JTextField();
            F_period.setBounds(110, 90, 200, 30);
            
            JTextField F_issue=new JTextField();
            F_issue.setBounds(100,130,130, 30);
            
            
            JButton create_button = new JButton("Submit");
            create_button.setBounds(130, 170, 80, 25);
            
            create_button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    String uid = F_uid.getText();
                    String bid = F_bid.getText();
                    String period = F_period.getText();
                    String issued_date = F_issue.getText();
                    
                    int period_int = Integer.parseInt(period);
                    
                    Connection connection=connect();
                    
                    try{
                        Statement stmnt = connection.createStatement();
                        stmnt.executeUpdate("USE library_management");
                        stmnt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,PERIOD) VALUES ('"+uid+"', '"+bid+"', '"+issued_date+"', "+period_int+")");
                        JOptionPane.showMessageDialog(null,"Book has been successfully issued!");
                        g.dispose();
                    }catch(SQLException e1){
                        JOptionPane.showMessageDialog(null, e1);
                    }
                                                         
                }
         
            });
            
                g.add(l3);
                g.add(l4);
                g.add(create_button);
                g.add(l1);
                g.add(l2);
                g.add(F_uid);           
                g.add(F_bid);
                g.add(F_period);
                g.add(F_issue);
                g.add(l4);
                g.setSize(350,250); 
                g.setLayout(null);  
                g.setVisible(true);
                g.setLocationRelativeTo(null);                    
        }
    
    
    });
    
    
    JButton return_book=new JButton("Return Book"); 
    return_book.setBounds(280,60,160,25);
    
    return_book.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
                 
                JFrame g = new JFrame("Enter Details");
                
                JLabel l1,l2,l3,l4;  
                l1=new JLabel("Issue ID()"); 
                l1.setBounds(30,15, 100,30); 
                                 
                l4=new JLabel("Return Date(DD-MM-YYYY)");  
                l4.setBounds(30,50, 150,30); 
                 
                JTextField F_iid = new JTextField();
                F_iid.setBounds(110, 15, 200, 30);
                 
                JTextField F_return=new JTextField();
                F_return.setBounds(180, 50, 130, 30);
             
                JButton create_but=new JButton("Return");
                create_but.setBounds(130,170,80,25);
                create_but.addActionListener(new ActionListener() {             
                    public void actionPerformed(ActionEvent e){                 
                     
                    String iid = F_iid.getText();
                    String return_date = F_return.getText();
                     
                    Connection connection = connect();
                     
                    try {
                        Statement stmnt = connection.createStatement();
                        stmnt.executeUpdate("USE library_management");

                        String date1=null;
                        String date2=return_date; 


                        ResultSet rs = stmnt.executeQuery("SELECT ISSUED_DATE FROM ISSUED WHERE IID="+iid);
                        while (rs.next()) {
                            date1 = rs.getString(1);

                        }
                      
                    try {
                        Date date_1=new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                        Date date_2=new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                        
                        long diff = date_2.getTime() - date_1.getTime();
                        
                        ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                                                         
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                      
                     
                     
                    stmnt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='"+return_date+"' WHERE IID="+iid);
                    g.dispose();
                      
 
                    Connection connection1 = connect();
                    Statement stmt1 = connection1.createStatement();
                    stmt1.executeUpdate("USE library_management");                
                    ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID="+iid);
                    String diff=null; 
                    while (rs1.next()){
                        diff = rs1.getString(1);
                          
                    }
                    int diff_int = Integer.parseInt(diff);
                    if (ex.days > diff_int) {
                        int fine = (ex.days - diff_int) * 10;
                        stmt1.executeUpdate("UPDATE ISSUED SET FINE=" + fine + " WHERE IID=" + iid);
                        String fine_str = "Fine: Rs. " + fine;
                        JOptionPane.showMessageDialog(null, fine_str);
}
 
                     JOptionPane.showMessageDialog(null,"Book Returned!");
                      
                    }

                    catch (SQLException e1) {
                        
                        JOptionPane.showMessageDialog(null, e1);
                    }   
                     
                    }
                     
                }); 
                    g.add(l4);
                    g.add(create_but);
                    g.add(l1);
                    g.add(F_iid);
                    g.add(F_return);
                    g.setSize(350,250);
                    g.setLayout(null);  
                    g.setVisible(true); 
                    g.setLocationRelativeTo(null);              
    }
    });
     
    f.add(create_button);
    f.add(return_book);
    f.add(issue_book);
    f.add(add_book);
    f.add(issued_button);
    f.add(users_button);
    f.add(view_button);
    f.add(add_user);
    f.setSize(600,200);
    f.setLayout(null);  
    f.setVisible(true); 
    f.setLocationRelativeTo(null);
     
    }
        
    
    public static void user_menu(String userID){
        JFrame f =new JFrame("User Functions");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton view_button=new JButton("View Books");
        view_button.setBounds(20,20,120,25);
        view_button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame f = new JFrame("Books Available");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Connection connection=connect();
                String sql = "SELECT * FROM BOOKS"; //dbden books bilgilerini çek
                try{
                    Statement stmnt = connection.createStatement();
                    stmnt.executeUpdate("USE library_management");
                    stmnt=connection.createStatement();
                    ResultSet rs = stmnt.executeQuery(sql);
                    JTable book_list = new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(book_list); //enable scroll bar
                    f.add(scrollPane); 
                    f.setSize(800, 400); 
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException ex) {
                     JOptionPane.showMessageDialog(null, ex);
                }                    
            }
        });
        JButton my_book=new JButton("My Books");
        my_book.setBounds(150,20,120,25);
        my_book.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
               JFrame f = new JFrame ("My Books");
               f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               int userID_int = Integer.parseInt(userID);
               
               Connection connection = connect();

               String sql="SELECY DISTINCT ISSUED.*, books.bname,books,genre,books.price from issued,books \" + \"where ((issued.userid=\" + userID_int + \") and (books.bid in (select bid from issued where issued.userid=\"+userID_int+\"))) group by iid";
               String sql1 = "select bid from issued where uid="+userID_int;
               
               try{
                   Statement stmnt = connection.createStatement();
                   stmnt.executeUpdate("USE library_management");
                   stmnt=connection.createStatement();
                   ArrayList books_list=new ArrayList();
                   ResultSet rs= stmnt.executeQuery(sql);
                   JTable book_list= new JTable();
                   book_list.setModel(DbUtils.resultSetToTableModel(rs));
                   JScrollPane scrollPane = new JScrollPane(book_list);
                   f.add(scrollPane);
                   f.setSize(800, 400); 
                   f.setVisible(true);
                   f.setLocationRelativeTo(null);
                   
               }catch (SQLException e1) {
                 JOptionPane.showMessageDialog(null, e1);
            }  
               
           }  
        });
        
        f.add(my_book); 
        f.add(view_button); 
        f.setSize(300,100);  
        f.setLayout(null);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }
}
    
    

