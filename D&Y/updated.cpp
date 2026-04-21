#include <iostream>
#include <queue>
#include <stack>
#include <vector>
using namespace std;

// Structure for Order
struct Order {
    int id;
    string name;
    vector<string> items; // multiple items
    bool isVIP;
};

// Linked List Node (History)
struct Node {
    Order data;
    Node* next;
};

// Global Variables
queue<Order> normalQueue;
queue<Order> vipQueue;
stack<Order> cancelledStack;
Node* historyHead = NULL;

// Completed Orders
vector<Order> completedOrders;

// Menu
string menu[] = {"Pizza", "Burger", "Fries", "Coke"};
int menuSize = 4;

// Add to History
void addToHistory(Order o) {
    Node* newNode = new Node;
    newNode->data = o;
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

// Place Order (Multiple Items + Repeat)
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

        char addMoreItems;
        do {
            cout << "Choose Item: ";
            cin >> itemChoice;
            o.items.push_back(menu[itemChoice - 1]);

            cout << "Add more items? (y/n): ";
            cin >> addMoreItems;

        } while(addMoreItems == 'y' || addMoreItems == 'Y');

        cout << "VIP Order? (1=Yes, 0=No): ";
        cin >> o.isVIP;

        if(o.isVIP) {
            vipQueue.push(o);
        } else {
            normalQueue.push(o);
        }

        addToHistory(o);

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

    cout << "Processing Order ID: " << o.id << " (" << o.name << ")\nItems: ";
    for(string item : o.items) {
        cout << item << " ";
    }
    cout << endl;

    completedOrders.push_back(o);
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
        cout << "Order ID: " << temp->data.id << " (" << temp->data.name << ")\n";
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