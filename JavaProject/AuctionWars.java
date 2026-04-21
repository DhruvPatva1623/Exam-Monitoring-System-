import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * ⚡ AUCTION WARS - Complete Real-Time Bidding System with Auto-Bid
 * Single File Version - Java AWT GUI Application
 */

public class AuctionWars extends JFrame {
    private static final long serialVersionUID = 1L;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AuctionManager auctionManager;
    private User currentUser;

    public AuctionWars() {
        setTitle("⚡ AUCTION WARS - Real-Time Bidding System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        auctionManager = new AuctionManager();

        mainPanel.add(new LoginScreen(this), "login");
        mainPanel.add(new DashboardScreen(this), "dashboard");
        mainPanel.add(new BiddingScreen(this), "bidding");
        mainPanel.add(new HistoryScreen(this), "history");

        add(mainPanel);
        showLoginScreen();
        setVisible(true);
    }

    public void showLoginScreen() { cardLayout.show(mainPanel, "login"); }
    public void showDashboardScreen() { ((DashboardScreen) mainPanel.getComponent(1)).refresh(); cardLayout.show(mainPanel, "dashboard"); }
    public void showBiddingScreen(AuctionItem item) { ((BiddingScreen) mainPanel.getComponent(2)).setAuctionItem(item); cardLayout.show(mainPanel, "bidding"); }
    public void showHistoryScreen() { ((HistoryScreen) mainPanel.getComponent(3)).refresh(); cardLayout.show(mainPanel, "history"); }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
    public AuctionManager getAuctionManager() { return auctionManager; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AuctionWars());
    }
}

// ===================== USER CLASS =====================
class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username, password;
    private double balance;
    private ArrayList<Bid> bidHistory;
    private ArrayList<AuctionItem> wonItems;

    public User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.bidHistory = new ArrayList<>();
        this.wonItems = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public void addBid(Bid bid) { bidHistory.add(bid); }
    public ArrayList<Bid> getBidHistory() { return bidHistory; }
    public void addWonItem(AuctionItem item) { wonItems.add(item); }
    public ArrayList<AuctionItem> getWonItems() { return wonItems; }
    public boolean verifyPassword(String pwd) { return this.password.equals(pwd); }
    @Override
    public String toString() { return username + " | Balance: ₹" + String.format("%.2f", balance); }
}

// ===================== BID CLASS =====================
class Bid implements Comparable<Bid>, Serializable {
    private static final long serialVersionUID = 1L;
    private User bidder;
    private double amount;
    private long timestamp;
    private String auctionItemId;
    private boolean isAutoBid;

    public Bid(User bidder, double amount, String auctionItemId, boolean isAutoBid) {
        this.bidder = bidder;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.auctionItemId = auctionItemId;
        this.isAutoBid = isAutoBid;
    }

    public User getBidder() { return bidder; }
    public double getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
    public String getAuctionItemId() { return auctionItemId; }
    public boolean isAutoBid() { return isAutoBid; }

    @Override
    public int compareTo(Bid other) {
        int amountComparison = Double.compare(other.amount, this.amount);
        if (amountComparison != 0) return amountComparison;
        return Long.compare(this.timestamp, other.timestamp);
    }

    @Override
    public String toString() { return String.format("%s - ₹%.2f %s", bidder.getUsername(), amount, isAutoBid ? "(Auto)" : ""); }
}

// ===================== AUCTION ITEM CLASS =====================
class AuctionItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String itemId, itemName, description, category;
    private double startPrice, currentBid;
    private User highestBidder;
    private long startTime, endTime;
    private PriorityQueue<Bid> bids;
    private boolean active;

    public AuctionItem(String itemId, String itemName, String description, double startPrice, long durationMillis, String category) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.startPrice = startPrice;
        this.currentBid = startPrice;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + durationMillis;
        this.bids = new PriorityQueue<>();
        this.active = true;
        this.category = category;
        this.highestBidder = null;
    }

    public synchronized boolean placeBid(Bid bid) throws InvalidBidException {
        if (!active) throw new InvalidBidException("Auction has ended!");
        if (System.currentTimeMillis() > endTime) {
            active = false;
            throw new InvalidBidException("Auction time expired!");
        }
        if (bid.getAmount() <= currentBid) {
            throw new InvalidBidException("Bid must be higher than current bid: ₹" + currentBid);
        }
        if (bid.getAmount() > bid.getBidder().getBalance()) {
            throw new InvalidBidException("Insufficient balance!");
        }

        bids.add(bid);
        this.currentBid = bid.getAmount();
        this.highestBidder = bid.getBidder();
        return true;
    }

    public synchronized void endAuction() {
        active = false;
        endTime = System.currentTimeMillis();
    }

    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public double getStartPrice() { return startPrice; }
    public double getCurrentBid() { return currentBid; }
    public User getHighestBidder() { return highestBidder; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public boolean isActive() { return active && System.currentTimeMillis() < endTime; }
    public PriorityQueue<Bid> getBids() { return bids; }
    public String getCategory() { return category; }
    public long getTimeRemaining() { return Math.max(0, endTime - System.currentTimeMillis()); }

    @Override
    public String toString() { return itemName + " | Current: ₹" + String.format("%.2f", currentBid); }
}

// ===================== CUSTOM EXCEPTION =====================
class InvalidBidException extends Exception {
    private static final long serialVersionUID = 1L;
    public InvalidBidException(String message) { super(message); }
}

// ===================== AUCTION MANAGER =====================
class AuctionManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<AuctionItem> activeAuctions;
    private ArrayList<AuctionItem> completedAuctions;
    private ArrayList<User> registeredUsers;
    private static final String USERS_FILE = "users.dat";
    private static final String AUCTIONS_FILE = "auctions.dat";

    public AuctionManager() {
        this.activeAuctions = new ArrayList<>();
        this.completedAuctions = new ArrayList<>();
        this.registeredUsers = new ArrayList<>();
        loadData();
        initializeSampleAuctions();
    }

    public synchronized void addUser(User user) throws Exception {
        for (User u : registeredUsers) {
            if (u.getUsername().equals(user.getUsername())) {
                throw new Exception("Username already exists!");
            }
        }
        registeredUsers.add(user);
        saveData();
    }

    public User authenticateUser(String username, String password) throws Exception {
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username) && user.verifyPassword(password)) {
                return user;
            }
        }
        throw new Exception("Invalid username or password!");
    }

    public synchronized void createAuction(AuctionItem item) {
        activeAuctions.add(item);
        saveData();
    }

    public synchronized void placeBid(AuctionItem item, User bidder, double amount, boolean isAutoBid) 
            throws InvalidBidException {
        Bid bid = new Bid(bidder, amount, item.getItemId(), isAutoBid);
        item.placeBid(bid);
        bidder.addBid(bid);
        saveData();
    }

    public ArrayList<AuctionItem> getActiveAuctions() {
        updateAuctionStatus();
        return activeAuctions;
    }

    public ArrayList<AuctionItem> getCompletedAuctions() {
        return completedAuctions;
    }

    private synchronized void updateAuctionStatus() {
        ArrayList<AuctionItem> toRemove = new ArrayList<>();
        for (AuctionItem item : activeAuctions) {
            if (!item.isActive()) {
                toRemove.add(item);
                completedAuctions.add(item);
                if (item.getHighestBidder() != null) {
                    item.getHighestBidder().addWonItem(item);
                }
            }
        }
        activeAuctions.removeAll(toRemove);
        if (!toRemove.isEmpty()) saveData();
    }

    private void initializeSampleAuctions() {
        if (activeAuctions.isEmpty() && completedAuctions.isEmpty()) {
            activeAuctions.add(new AuctionItem("A001", "Vintage Camera", "Rare Canon film camera from 1970s", 500, 300000, "Electronics"));
            activeAuctions.add(new AuctionItem("A002", "Rolex Watch", "Classic automatic watch with sapphire crystal", 8000, 400000, "Luxury"));
            activeAuctions.add(new AuctionItem("A003", "Antique Painting", "Oil painting by unknown artist, 1850s", 2000, 350000, "Art"));
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUCTIONS_FILE))) {
            oos.writeObject(new Object[]{activeAuctions, completedAuctions});
        } catch (IOException e) { System.err.println("Error saving auctions: " + e.getMessage()); }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(registeredUsers);
        } catch (IOException e) { System.err.println("Error saving users: " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AUCTIONS_FILE))) {
            Object[] data = (Object[]) ois.readObject();
            activeAuctions = (ArrayList<AuctionItem>) data[0];
            completedAuctions = (ArrayList<AuctionItem>) data[1];
        } catch (IOException | ClassNotFoundException e) { }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            registeredUsers = (ArrayList<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            registeredUsers.add(new User("alice", "password123", 50000));
            registeredUsers.add(new User("bob", "password456", 75000));
            registeredUsers.add(new User("charlie", "password789", 100000));
        }
    }

    public ArrayList<User> getAllUsers() { return registeredUsers; }
}

// ===================== LOGIN SCREEN =====================
class LoginScreen extends Panel {
    private static final long serialVersionUID = 1L;
    private AuctionWars auctionSystem;
    private TextField usernameField, passwordField;
    private Label messageLabel;
    private boolean showRegister = false;
    private TextField regUsernameField, regPasswordField, regBalanceField;

    public LoginScreen(AuctionWars auctionSystem) {
        this.auctionSystem = auctionSystem;
        setLayout(null);
        setBackground(new Color(15, 23, 42));
        drawLoginUI();
    }

    private void drawLoginUI() {
        removeAll();
        Label titleLabel = new Label("⚡ AUCTION WARS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(150, 80, 700, 60);
        add(titleLabel);

        Label subtitleLabel = new Label("Real-Time Bidding System");
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setBounds(250, 140, 500, 30);
        add(subtitleLabel);

        if (!showRegister) {
            drawLoginForm();
        } else {
            drawRegisterForm();
        }
    }

    private void drawLoginForm() {
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(226, 232, 240));
        usernameLabel.setBounds(250, 220, 100, 30);
        add(usernameLabel);

        usernameField = new TextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBounds(400, 220, 250, 35);
        usernameField.setBackground(new Color(30, 41, 59));
        usernameField.setForeground(new Color(226, 232, 240));
        add(usernameField);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(226, 232, 240));
        passwordLabel.setBounds(250, 270, 100, 30);
        add(passwordLabel);

        passwordField = new TextField();
        passwordField.setEchoChar('•');
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBounds(400, 270, 250, 35);
        passwordField.setBackground(new Color(30, 41, 59));
        passwordField.setForeground(new Color(226, 232, 240));
        add(passwordField);

        messageLabel = new Label("");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(248, 113, 113));
        messageLabel.setBounds(250, 320, 400, 30);
        add(messageLabel);

        Button loginButton = createStyledButton("LOGIN", 350, 380, 300, 50);
        loginButton.addActionListener(e -> handleLogin());
        add(loginButton);

        Button registerButton = createStyledButton("CREATE NEW ACCOUNT", 350, 450, 300, 40);
        registerButton.addActionListener(e -> {
            showRegister = true;
            drawLoginUI();
        });
        add(registerButton);
    }

    private void drawRegisterForm() {
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(226, 232, 240));
        usernameLabel.setBounds(250, 200, 100, 30);
        add(usernameLabel);

        regUsernameField = new TextField();
        regUsernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        regUsernameField.setBounds(400, 200, 250, 35);
        regUsernameField.setBackground(new Color(30, 41, 59));
        regUsernameField.setForeground(new Color(226, 232, 240));
        add(regUsernameField);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(226, 232, 240));
        passwordLabel.setBounds(250, 250, 100, 30);
        add(passwordLabel);

        regPasswordField = new TextField();
        regPasswordField.setEchoChar('•');
        regPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        regPasswordField.setBounds(400, 250, 250, 35);
        regPasswordField.setBackground(new Color(30, 41, 59));
        regPasswordField.setForeground(new Color(226, 232, 240));
        add(regPasswordField);

        Label balanceLabel = new Label("Initial Balance (₹):");
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        balanceLabel.setForeground(new Color(226, 232, 240));
        balanceLabel.setBounds(250, 300, 150, 30);
        add(balanceLabel);

        regBalanceField = new TextField();
        regBalanceField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        regBalanceField.setBounds(400, 300, 250, 35);
        regBalanceField.setBackground(new Color(30, 41, 59));
        regBalanceField.setForeground(new Color(226, 232, 240));
        add(regBalanceField);

        messageLabel = new Label("");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(248, 113, 113));
        messageLabel.setBounds(250, 350, 400, 30);
        add(messageLabel);

        Button registerButton = createStyledButton("REGISTER", 350, 410, 300, 50);
        registerButton.addActionListener(e -> handleRegister());
        add(registerButton);

        Button backButton = createStyledButton("BACK TO LOGIN", 350, 480, 300, 40);
        backButton.addActionListener(e -> {
            showRegister = false;
            drawLoginUI();
        });
        add(backButton);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            messageLabel.setForeground(new Color(248, 113, 113));
            return;
        }

        // ===== ADMIN LOGIN =====
        if(username.equals("admin") && password.equals("admin123")) {
            User adminUser = new User("admin", "admin123", 999999);
            auctionSystem.setCurrentUser(adminUser);
            openAdminPanel();
            return;
        }

        try {
            User user = auctionSystem.getAuctionManager().authenticateUser(username, password);
            auctionSystem.setCurrentUser(user);
            messageLabel.setText("Login successful! Welcome " + username);
            messageLabel.setForeground(new Color(34, 197, 94));
            
            javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
                auctionSystem.showDashboardScreen();
            });
            timer.setRepeats(false);
            timer.start();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
            messageLabel.setForeground(new Color(248, 113, 113));
        }
    }

    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText();
        String balanceStr = regBalanceField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || balanceStr.isEmpty()) {
            messageLabel.setText("Please fill all fields");
            messageLabel.setForeground(new Color(248, 113, 113));
            return;
        }

        try {
            double balance = Double.parseDouble(balanceStr);
            if (balance <= 0) {
                messageLabel.setText("Balance must be positive");
                messageLabel.setForeground(new Color(248, 113, 113));
                return;
            }

            User newUser = new User(username, password, balance);
            auctionSystem.getAuctionManager().addUser(newUser);
            messageLabel.setText("Account created successfully! Please login.");
            messageLabel.setForeground(new Color(34, 197, 94));
            
            javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
                showRegister = false;
                drawLoginUI();
            });
            timer.setRepeats(false);
            timer.start();
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid balance amount");
            messageLabel.setForeground(new Color(248, 113, 113));
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
            messageLabel.setForeground(new Color(248, 113, 113));
        }
    }

    private Button createStyledButton(String label, int x, int y, int width, int height) {
        Button button = new Button(label) {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 165, 0));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(label)) / 2;
                int ty = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(label, tx, ty);
            }
        };
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(255, 165, 0));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    private void openAdminPanel() {
        Frame adminFrame = new Frame("⚙️ ADMIN PANEL");
        adminFrame.setSize(500, 350);
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setLayout(new GridLayout(6, 2, 10, 10));

        Label nameLabel = new Label("Item Name:");
        TextField nameField = new TextField();
        adminFrame.add(nameLabel);
        adminFrame.add(nameField);

        Label descLabel = new Label("Description:");
        TextField descField = new TextField();
        adminFrame.add(descLabel);
        adminFrame.add(descField);

        Label priceLabel = new Label("Start Price (₹):");
        TextField priceField = new TextField();
        adminFrame.add(priceLabel);
        adminFrame.add(priceField);

        Label categoryLabel = new Label("Category:");
        TextField categoryField = new TextField();
        adminFrame.add(categoryLabel);
        adminFrame.add(categoryField);

        Label durationLabel = new Label("Duration (minutes):");
        TextField durationField = new TextField();
        adminFrame.add(durationLabel);
        adminFrame.add(durationField);

        Button addButton = new Button("➕ ADD AUCTION");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                String category = categoryField.getText().trim();
                double price = Double.parseDouble(priceField.getText());
                int durationMinutes = Integer.parseInt(durationField.getText());

                if (name.isEmpty() || desc.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(adminFrame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                AuctionItem newAuction = new AuctionItem("A" + System.currentTimeMillis(), name, desc, price, durationMinutes * 60000L, category);
                auctionSystem.getAuctionManager().createAuction(newAuction);
                
                JOptionPane.showMessageDialog(adminFrame, "Auction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                descField.setText("");
                priceField.setText("");
                categoryField.setText("");
                durationField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(adminFrame, "Invalid price or duration!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        adminFrame.add(addButton);

        Button closeButton = new Button("❌ CLOSE");
        closeButton.addActionListener(e -> {
            adminFrame.dispose();
            auctionSystem.showDashboardScreen();
        });
        adminFrame.add(closeButton);

        adminFrame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(15, 23, 42));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
    }
}

// ===================== DASHBOARD SCREEN =====================
class DashboardScreen extends Panel {
    private static final long serialVersionUID = 1L;
    private AuctionWars auctionSystem;
    private Panel auctionListPanel;
    private Label userInfoLabel;
    private ScrollPane scrollPane;
    private java.util.Timer updateTimer;

    public DashboardScreen(AuctionWars auctionSystem) {
        this.auctionSystem = auctionSystem;
        setLayout(null);
        setBackground(new Color(15, 23, 42));
        initializeUI();
        startAutoUpdate();
    }

    private void initializeUI() {
        Panel headerPanel = new Panel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBounds(0, 0, 1000, 80);
        add(headerPanel);

        Label titleLabel = new Label("⚡ ACTIVE AUCTIONS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(20, 15, 400, 40);
        headerPanel.add(titleLabel);

        userInfoLabel = new Label("");
        userInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userInfoLabel.setForeground(new Color(148, 163, 184));
        userInfoLabel.setBounds(700, 20, 280, 50);
        headerPanel.add(userInfoLabel);

        auctionListPanel = new Panel();
        auctionListPanel.setLayout(new GridLayout(0, 1, 10, 10));
        auctionListPanel.setBackground(new Color(15, 23, 42));

        scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.add(auctionListPanel);
        scrollPane.setBounds(20, 100, 960, 450);
        add(scrollPane);

        Panel bottomPanel = new Panel();
        bottomPanel.setLayout(null);
        bottomPanel.setBackground(new Color(30, 41, 59));
        bottomPanel.setBounds(0, 600, 1000, 100);
        add(bottomPanel);

        Button historyButton = createNavButton("📜 BID HISTORY", 120, 20, 200, 50);
        historyButton.addActionListener(e -> auctionSystem.showHistoryScreen());
        bottomPanel.add(historyButton);

        Button refreshButton = createNavButton("🔄 REFRESH", 350, 20, 200, 50);
        refreshButton.addActionListener(e -> refresh());
        bottomPanel.add(refreshButton);

        Button logoutButton = createNavButton("🚪 LOGOUT", 580, 20, 200, 50);
        logoutButton.addActionListener(e -> handleLogout());
        bottomPanel.add(logoutButton);
    }

    public void refresh() {
        User currentUser = auctionSystem.getCurrentUser();
        userInfoLabel.setText("👤 " + currentUser.getUsername() + " | Balance: ₹" + String.format("%.2f", currentUser.getBalance()));

        auctionListPanel.removeAll();
        ArrayList<AuctionItem> auctions = auctionSystem.getAuctionManager().getActiveAuctions();

        if (auctions.isEmpty()) {
            Label noAuctionsLabel = new Label("No active auctions at the moment");
            noAuctionsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noAuctionsLabel.setForeground(new Color(148, 163, 184));
            auctionListPanel.add(noAuctionsLabel);
        } else {
            for (AuctionItem auction : auctions) {
                Panel auctionCard = createAuctionCard(auction);
                auctionListPanel.add(auctionCard);
            }
        }

        auctionListPanel.setSize(auctionListPanel.getPreferredSize());
        scrollPane.setScrollPosition(0, 0);
        repaint();
    }

    private Panel createAuctionCard(AuctionItem auction) {
        Panel card = new Panel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 41, 59));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.setColor(new Color(255, 165, 0, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paint(g);
            }
        };

        card.setLayout(null);
        card.setBackground(new Color(30, 41, 59));
        card.setPreferredSize(new Dimension(920, 100));
        card.setSize(920, 100);

        Label nameLabel = new Label(auction.getItemName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(new Color(255, 215, 0));
        nameLabel.setBounds(15, 10, 400, 25);
        card.add(nameLabel);

        Label descLabel = new Label(auction.getDescription());
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLabel.setForeground(new Color(148, 163, 184));
        descLabel.setBounds(15, 35, 400, 20);
        card.add(descLabel);

        Label bidLabel = new Label("Current: ₹" + String.format("%.2f", auction.getCurrentBid()));
        bidLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bidLabel.setForeground(new Color(34, 197, 94));
        bidLabel.setBounds(450, 20, 200, 30);
        card.add(bidLabel);

        long timeRemaining = auction.getTimeRemaining();
        String timeStr = formatTime(timeRemaining);
        Label timeLabel = new Label("⏱ " + timeStr);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timeLabel.setForeground(new Color(248, 113, 113));
        timeLabel.setBounds(450, 55, 200, 20);
        card.add(timeLabel);

        Button bidButton = new Button("PLACE BID") {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 165, 0));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("PLACE BID", (getWidth() - fm.stringWidth("PLACE BID")) / 2, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());
            }
        };
        bidButton.setBounds(780, 30, 120, 50);
        bidButton.setBackground(new Color(255, 165, 0));
        bidButton.setForeground(Color.WHITE);
        bidButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bidButton.addActionListener(e -> auctionSystem.showBiddingScreen(auction));
        card.add(bidButton);

        return card;
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    private Button createNavButton(String label, int x, int y, int width, int height) {
        Button button = new Button(label) {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(100, 116, 139));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(label, (getWidth() - fm.stringWidth(label)) / 2, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());
            }
        };
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(100, 116, 139));
        button.setForeground(Color.WHITE);
        return button;
    }

    private void handleLogout() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        auctionSystem.setCurrentUser(null);
        auctionSystem.showLoginScreen();
    }

    private void startAutoUpdate() {
        updateTimer = new java.util.Timer();
        updateTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refresh());
            }
        }, 1000, 5000);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(15, 23, 42));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
    }
}

// ===================== BIDDING SCREEN =====================
class BiddingScreen extends Panel {
    private static final long serialVersionUID = 1L;
    private AuctionWars auctionSystem;
    private AuctionItem currentItem;
    private TextField bidAmountField;
    private TextField autoBidLimitField;
    private Checkbox autoBidCheckbox;
    private Label messageLabel;
    private Label bidHistoryTextArea;
    private Label currentBidLabel;
    private Label timeRemainingLabel;
    private java.util.Timer updateTimer;
    private AutoBidder autoBidder;

    public BiddingScreen(AuctionWars auctionSystem) {
        this.auctionSystem = auctionSystem;
        setLayout(null);
        setBackground(new Color(15, 23, 42));
        initializeUI();
    }

    private void initializeUI() {
        Panel headerPanel = new Panel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBounds(0, 0, 1000, 60);
        add(headerPanel);

        Label titleLabel = new Label("PLACE YOUR BID");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(30, 15, 400, 35);
        headerPanel.add(titleLabel);

        Button backButton = new Button("← BACK") {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(100, 116, 139));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("← BACK", (getWidth() - fm.stringWidth("← BACK")) / 2, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());
            }
        };
        backButton.setBounds(880, 10, 100, 40);
        backButton.setBackground(new Color(100, 116, 139));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            if (updateTimer != null) updateTimer.cancel();
            if (autoBidder != null) autoBidder.stopAutobidding();
            auctionSystem.showDashboardScreen();
        });
        headerPanel.add(backButton);

        Panel detailsPanel = new Panel();
        detailsPanel.setLayout(null);
        detailsPanel.setBackground(new Color(30, 41, 59));
        detailsPanel.setBounds(30, 80, 450, 280);
        add(detailsPanel);

        Label itemNameLabel = new Label("");
        itemNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        itemNameLabel.setForeground(new Color(255, 215, 0));
        itemNameLabel.setBounds(15, 15, 420, 30);
        detailsPanel.add(itemNameLabel);

        Label descriptionLabel = new Label("");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setForeground(new Color(148, 163, 184));
        descriptionLabel.setBounds(15, 50, 420, 40);
        detailsPanel.add(descriptionLabel);

        currentBidLabel = new Label("");
        currentBidLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        currentBidLabel.setForeground(new Color(34, 197, 94));
        currentBidLabel.setBounds(15, 100, 420, 25);
        detailsPanel.add(currentBidLabel);

        timeRemainingLabel = new Label("");
        timeRemainingLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        timeRemainingLabel.setForeground(new Color(248, 113, 113));
        timeRemainingLabel.setBounds(15, 130, 420, 20);
        detailsPanel.add(timeRemainingLabel);

        Panel biddingPanel = new Panel();
        biddingPanel.setLayout(null);
        biddingPanel.setBackground(new Color(30, 41, 59));
        biddingPanel.setBounds(520, 80, 450, 280);
        add(biddingPanel);

        Label bidTitle = new Label("PLACE BID");
        bidTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        bidTitle.setForeground(new Color(255, 215, 0));
        bidTitle.setBounds(15, 10, 420, 25);
        biddingPanel.add(bidTitle);

        Label bidAmountLabel = new Label("Bid Amount (₹):");
        bidAmountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bidAmountLabel.setForeground(new Color(226, 232, 240));
        bidAmountLabel.setBounds(15, 50, 150, 25);
        biddingPanel.add(bidAmountLabel);

        bidAmountField = new TextField();
        bidAmountField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bidAmountField.setBounds(180, 50, 250, 30);
        bidAmountField.setBackground(new Color(30, 41, 59));
        bidAmountField.setForeground(new Color(226, 232, 240));
        biddingPanel.add(bidAmountField);

        autoBidCheckbox = new Checkbox("Enable Auto-Bid?", false);
        autoBidCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        autoBidCheckbox.setForeground(new Color(226, 232, 240));
        autoBidCheckbox.setBounds(15, 95, 180, 25);
        autoBidCheckbox.addItemListener(e -> toggleAutoBidOptions());
        biddingPanel.add(autoBidCheckbox);

        Label autoBidLimitLabel = new Label("Max Bid Limit (₹):");
        autoBidLimitLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        autoBidLimitLabel.setForeground(new Color(226, 232, 240));
        autoBidLimitLabel.setBounds(15, 130, 150, 25);
        biddingPanel.add(autoBidLimitLabel);

        autoBidLimitField = new TextField();
        autoBidLimitField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        autoBidLimitField.setBounds(180, 130, 250, 30);
        autoBidLimitField.setBackground(new Color(30, 41, 59));
        autoBidLimitField.setForeground(new Color(226, 232, 240));
        autoBidLimitField.setEnabled(false);
        biddingPanel.add(autoBidLimitField);

        messageLabel = new Label("");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        messageLabel.setForeground(new Color(248, 113, 113));
        messageLabel.setBounds(15, 170, 420, 30);
        biddingPanel.add(messageLabel);

        Button placeBidButton = new Button("SUBMIT BID") {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(34, 197, 94));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString("SUBMIT BID", (getWidth() - fm.stringWidth("SUBMIT BID")) / 2, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());
            }
        };
        placeBidButton.setBounds(15, 230, 415, 40);
        placeBidButton.setBackground(new Color(34, 197, 94));
        placeBidButton.setForeground(Color.WHITE);
        placeBidButton.addActionListener(e -> handlePlaceBid());
        biddingPanel.add(placeBidButton);

        Panel historyPanel = new Panel();
        historyPanel.setLayout(null);
        historyPanel.setBackground(new Color(30, 41, 59));
        historyPanel.setBounds(30, 380, 940, 200);
        add(historyPanel);

        Label historyTitle = new Label("RECENT BIDS");
        historyTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        historyTitle.setForeground(new Color(255, 215, 0));
        historyTitle.setBounds(15, 10, 910, 25);
        historyPanel.add(historyTitle);

        bidHistoryTextArea = new Label("");
        bidHistoryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        bidHistoryTextArea.setForeground(new Color(148, 163, 184));
        bidHistoryTextArea.setAlignment(Label.LEFT);
        bidHistoryTextArea.setBounds(15, 40, 910, 150);
        historyPanel.add(bidHistoryTextArea);
    }

    public void setAuctionItem(AuctionItem item) {
        this.currentItem = item;
        updateUI();
        startAutoUpdate();
    }

    private void updateUI() {
        if (currentItem == null) return;
        currentBidLabel.setText("Current Bid: ₹" + String.format("%.2f", currentItem.getCurrentBid()));
        timeRemainingLabel.setText("⏱ Time Remaining: " + formatTime(currentItem.getTimeRemaining()));
        updateBidHistory();
    }

    private void updateBidHistory() {
        java.util.Queue<Bid> bidsQueue = new java.util.PriorityQueue<>(currentItem.getBids());
        StringBuilder sb = new StringBuilder();
        int count = 0;
        while (!bidsQueue.isEmpty() && count < 5) {
            Bid bid = bidsQueue.poll();
            String autoBidStr = bid.isAutoBid() ? " [AUTO]" : "";
            sb.append(bid.getBidder().getUsername()).append(" - ₹").append(String.format("%.2f", bid.getAmount())).append(autoBidStr).append("\n");
            count++;
        }
        bidHistoryTextArea.setText(sb.toString());
    }

    private void toggleAutoBidOptions() {
        autoBidLimitField.setEnabled(autoBidCheckbox.getState());
    }

    private void handlePlaceBid() {
        try {
            String bidStr = bidAmountField.getText().trim();
            if (bidStr.isEmpty()) {
                messageLabel.setText("Please enter a bid amount");
                messageLabel.setForeground(new Color(248, 113, 113));
                return;
            }

            double bidAmount = Double.parseDouble(bidStr);
            User currentUser = auctionSystem.getCurrentUser();

            if (bidAmount > currentUser.getBalance()) {
                messageLabel.setText("Insufficient balance!");
                messageLabel.setForeground(new Color(248, 113, 113));
                return;
            }

            boolean isAutoBid = autoBidCheckbox.getState();
            double autoBidLimit = 0;

            if (isAutoBid) {
                String limitStr = autoBidLimitField.getText().trim();
                if (limitStr.isEmpty()) {
                    messageLabel.setText("Please enter auto-bid limit");
                    messageLabel.setForeground(new Color(248, 113, 113));
                    return;
                }
                autoBidLimit = Double.parseDouble(limitStr);
                if (autoBidLimit < bidAmount) {
                    messageLabel.setText("Auto-bid limit must be >= current bid");
                    messageLabel.setForeground(new Color(248, 113, 113));
                    return;
                }
            }

            auctionSystem.getAuctionManager().placeBid(currentItem, currentUser, bidAmount, false);

            if (isAutoBid) {
                if (autoBidder != null) autoBidder.stopAutobidding();
                autoBidder = new AutoBidder(currentUser, currentItem, autoBidLimit, auctionSystem);
                autoBidder.start();
            }

            messageLabel.setText("✓ Bid placed successfully!");
            messageLabel.setForeground(new Color(34, 197, 94));
            bidAmountField.setText("");
            autoBidLimitField.setText("");
            updateUI();

        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid number format");
            messageLabel.setForeground(new Color(248, 113, 113));
        } catch (InvalidBidException e) {
            messageLabel.setText(e.getMessage());
            messageLabel.setForeground(new Color(248, 113, 113));
        }
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    private void startAutoUpdate() {
        updateTimer = new java.util.Timer();
        updateTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateUI());
            }
        }, 1000, 2000);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(15, 23, 42));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
    }
}

// ===================== AUTO BIDDER THREAD =====================
class AutoBidder extends Thread {
    private User user;
    private AuctionItem item;
    private double maxLimit;
    private AuctionWars auctionSystem;
    private volatile boolean running = true;

    public AutoBidder(User user, AuctionItem item, double maxLimit, AuctionWars auctionSystem) {
        this.user = user;
        this.item = item;
        this.maxLimit = maxLimit;
        this.auctionSystem = auctionSystem;
    }

    @Override
    public void run() {
        while (running && item.isActive()) {
            try {
                Thread.sleep(3000);

                if (!item.isActive()) break;

                synchronized (item) {
                    double nextBidAmount = item.getCurrentBid() + 500;
                    if (nextBidAmount <= maxLimit && nextBidAmount <= user.getBalance()) {
                        auctionSystem.getAuctionManager().placeBid(item, user, nextBidAmount, true);
                    } else {
                        stopAutobidding();
                    }
                }
            } catch (InvalidBidException | InterruptedException e) {
                stopAutobidding();
            }
        }
    }

    public void stopAutobidding() {
        running = false;
    }
}

// ===================== HISTORY SCREEN =====================
class HistoryScreen extends Panel {
    private static final long serialVersionUID = 1L;
    private AuctionWars auctionSystem;
    private Panel bidHistoryPanel;
    private ScrollPane bidScrollPane;
    private Label balanceLabel;

    public HistoryScreen(AuctionWars auctionSystem) {
        this.auctionSystem = auctionSystem;
        setLayout(null);
        setBackground(new Color(15, 23, 42));
        initializeUI();
    }

    private void initializeUI() {
        Panel headerPanel = new Panel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBounds(0, 0, 1000, 70);
        add(headerPanel);

        Label titleLabel = new Label("📊 ACCOUNT HISTORY");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(30, 15, 400, 40);
        headerPanel.add(titleLabel);

        balanceLabel = new Label("");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        balanceLabel.setForeground(new Color(34, 197, 94));
        balanceLabel.setBounds(700, 20, 280, 30);
        headerPanel.add(balanceLabel);

        bidHistoryPanel = new Panel();
        bidHistoryPanel.setLayout(new GridLayout(0, 1, 8, 8));
        bidHistoryPanel.setBackground(new Color(15, 23, 42));

        bidScrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        bidScrollPane.add(bidHistoryPanel);
        bidScrollPane.setBounds(30, 90, 940, 490);
        add(bidScrollPane);

        Panel bottomPanel = new Panel();
        bottomPanel.setLayout(null);
        bottomPanel.setBackground(new Color(30, 41, 59));
        bottomPanel.setBounds(0, 600, 1000, 100);
        add(bottomPanel);

        Button backButton = createNavButton("← BACK TO DASHBOARD", 350, 25, 300, 50);
        backButton.addActionListener(e -> auctionSystem.showDashboardScreen());
        bottomPanel.add(backButton);
    }

    public void refresh() {
        User currentUser = auctionSystem.getCurrentUser();
        balanceLabel.setText("💰 Balance: ₹" + String.format("%.2f", currentUser.getBalance()));
        ArrayList<Bid> bidHistory = currentUser.getBidHistory();

        bidHistoryPanel.removeAll();

        if (bidHistory.isEmpty()) {
            Label noBidsLabel = new Label("No bids placed yet");
            noBidsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            noBidsLabel.setForeground(new Color(148, 163, 184));
            bidHistoryPanel.add(noBidsLabel);
        } else {
            for (Bid bid : bidHistory) {
                Panel bidCard = createBidCard(bid);
                bidHistoryPanel.add(bidCard);
            }
        }

        bidHistoryPanel.setSize(bidHistoryPanel.getPreferredSize());
        bidScrollPane.setScrollPosition(0, 0);
        repaint();
    }

    private Panel createBidCard(Bid bid) {
        Panel card = new Panel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 41, 59));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.setColor(new Color(255, 165, 0, 80));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paint(g);
            }
        };

        card.setLayout(null);
        card.setBackground(new Color(30, 41, 59));
        card.setPreferredSize(new Dimension(900, 70));
        card.setSize(900, 70);

        Label itemLabel = new Label("Item: " + bid.getAuctionItemId());
        itemLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        itemLabel.setForeground(new Color(255, 215, 0));
        itemLabel.setBounds(15, 10, 300, 20);
        card.add(itemLabel);

        Label amountLabel = new Label("Bid: ₹" + String.format("%.2f", bid.getAmount()));
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        amountLabel.setForeground(new Color(34, 197, 94));
        amountLabel.setBounds(330, 10, 200, 20);
        card.add(amountLabel);

        String autoBidStr = bid.isAutoBid() ? "🤖 Auto-Bid" : "👤 Manual Bid";
        Label autoBidLabel = new Label(autoBidStr);
        autoBidLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        autoBidLabel.setForeground(bid.isAutoBid() ? new Color(168, 85, 247) : new Color(148, 163, 184));
        autoBidLabel.setBounds(570, 10, 150, 20);
        card.add(autoBidLabel);

        Label statusLabel = new Label("✓ Placed");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLabel.setForeground(new Color(34, 197, 94));
        statusLabel.setBounds(850, 25, 50, 20);
        card.add(statusLabel);

        return card;
    }

    private Button createNavButton(String label, int x, int y, int width, int height) {
        Button button = new Button(label) {
            private static final long serialVersionUID = 1L;
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(100, 116, 139));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(label, (getWidth() - fm.stringWidth(label)) / 2, ((getHeight() - fm.getHeight()) / 2) + fm.getAscent());
            }
        };
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(100, 116, 139));
        button.setForeground(Color.WHITE);
        return button;
    }
            
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(15, 23, 42));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g);
    }
}