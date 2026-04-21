#include <iostream>
#include <queue>
#include <stack>
#include <vector>
using namespace std;

// Structure for Order
struct Order {
    int id;
    string name;
    vector<string> items;
    bool isVIP;
};

// Linked List Node (History)
struct Node {
    Order data;
    string status;
    Node* next;
};

// Global Variables
queue<Order> normalQueue;
queue<Order> vipQueue;
stack<Order> cancelledStack;
Node* historyHead = NULL;

vector<Order> completedOrders;

// Menu
string menu[] = {"Pizza", "Burger", "Fries", "Coke"};
int menuSize = 4;

// Add to History
void addToHistory(Order o, string status) {
    Node* newNode = new Node;
    newNode->data = o;
    newNode->status = status;
    newNode->next = historyHead;
    historyHead = newNode;
}

// Display Menu
void displayMenu() {
    cout << "\n--- MENU ---\n";
    for(int i = 0; i < menuSize; i++) {
        cout << i+1 << ". " << menu[i] << endl;
    }
}

// Place Order
void placeOrder() {
    char more;

    do {
        Order o;
        int itemChoice;

        cout << "Enter Order ID: ";
        cin >> o.id;

        cout << "Enter Customer Name: ";
        cin >> o.name;

        displayMenu();

        cout << "Enter item numbers (e.g. 1 2 3, press 0 to stop): ";
        while(true) {
            cin >> itemChoice;

            if(itemChoice == 0)
                break;

            if(itemChoice >= 1 && itemChoice <= menuSize) {
                o.items.push_back(menu[itemChoice - 1]);
            } else {
                cout << "Invalid item! Try again\n";
            }
        }

        cout << "VIP Order? (1=Yes, 0=No): ";
        cin >> o.isVIP;

        if(o.isVIP) {
            vipQueue.push(o);
        } else {
            normalQueue.push(o);
        }

        addToHistory(o, "Placed");

        cout << "Order Placed Successfully!\n";

        cout << "Place another order? (y/n): ";
        cin >> more;

    } while(more == 'y' || more == 'Y');
}

// Process Order
void processOrder() {
    Order o;

    if(!vipQueue.empty()) {
        o = vipQueue.front();
        vipQueue.pop();
    }
    else if(!normalQueue.empty()) {
        o = normalQueue.front();
        normalQueue.pop();
    }
    else {
        cout << "No Orders to Process!\n";
        return;
    }

    cout << "\nProcessing Order ID: " << o.id << " (" << o.name << ")\nItems: ";
    for(string item : o.items) {
        cout << item << " ";
    }
    cout << endl;

    completedOrders.push_back(o);
    addToHistory(o, "Completed");

    cout << "Order Completed!\n";
}

// Cancel Order by ID
void cancelOrderByID(int id) {
    queue<Order> temp;
    bool found = false;

    // Check Normal Queue
    while(!normalQueue.empty()) {
        Order o = normalQueue.front();
        normalQueue.pop();

        if(o.id == id && !found) {
            cancelledStack.push(o);
            addToHistory(o, "Cancelled");
            found = true;
            cout << "Order Cancelled!\n";
        } else {
            temp.push(o);
        }
    }
    normalQueue = temp;

    // Check VIP Queue
    temp = queue<Order>();
    while(!vipQueue.empty()) {
        Order o = vipQueue.front();
        vipQueue.pop();

        if(o.id == id && !found) {
            cancelledStack.push(o);
            addToHistory(o, "Cancelled");
            found = true;
            cout << "VIP Order Cancelled!\n";
        } else {
            temp.push(o);
        }
    }
    vipQueue = temp;

    if(!found) {
        cout << "Order ID Not Found!\n";
    }
}

// Show Current Status
void showStatus() {
    cout << "\n--- Ongoing Orders ---\n";

    queue<Order> temp = vipQueue;
    while(!temp.empty()) {
        cout << "VIP Order ID: " << temp.front().id << endl;
        temp.pop();
    }

    temp = normalQueue;
    while(!temp.empty()) {
        cout << "Normal Order ID: " << temp.front().id << endl;
        temp.pop();
    }

    cout << "\n--- Completed Orders ---\n";
    for(auto o : completedOrders) {
        cout << "Order ID: " << o.id << endl;
    }
}

// Display History
void displayHistory() {
    Node* temp = historyHead;

    cout << "\n--- Full Order History ---\n";
    while(temp != NULL) {
        cout << "Order ID: " << temp->data.id
             << " | Name: " << temp->data.name
             << " | Status: " << temp->status << endl;

        temp = temp->next;
    }
}

// Main
int main() {
    int choice, id;

    while(true) {
        cout << "\n1. Place Order\n2. Process Order\n3. Cancel Order\n4. Show Status\n5. Show History\n6. Exit\n";
        cout << "Enter choice: ";
        cin >> choice;

        switch(choice) {

            case 1:
                placeOrder();
                break;

            case 2:
                processOrder();
                break;

            case 3:
                cout << "Enter Order ID to Cancel: ";
                cin >> id;
                cancelOrderByID(id);
                break;

            case 4:
                showStatus();
                break;

            case 5:
                displayHistory();
                break;

            case 6:
                return 0;

            default:
                cout << "Invalid Choice!\n";
        }
    }
}
