#include <iostream>
#include <queue>
#include <stack>
using namespace std;

// Structure for Order
struct Order {
    int id;
    string name;
    string item;
};

// Linked List Node for Order History
struct Node {
    Order data;
    Node* next;
};

// Global Variables
queue<Order> normalQueue;
queue<Order> vipQueue;
stack<Order> cancelledStack;
Node* historyHead = NULL;

// Predefined Menu (Array)
string menu[] = {"Pizza", "Burger", "Fries", "Coke"};
int menuSize = 4;

// Add to Linked List (Order History)
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

// Place Order
void placeOrder(int id, string name, int choice, bool isVIP) {
    Order o;
    o.id = id;
    o.name = name;
    o.item = menu[choice - 1];

    if(isVIP) {
        vipQueue.push(o);
        cout << "VIP Order Placed!\n";
    } else {
        normalQueue.push(o);
        cout << "Normal Order Placed!\n";
    }
}

// Process Order
void processOrder() {
    Order o;

    if(!vipQueue.empty()) {
        o = vipQueue.front();
        vipQueue.pop();
        cout << "Processing VIP Order: " << o.name << " - " << o.item << endl;
    }
    else if(!normalQueue.empty()) {
        o = normalQueue.front();
        normalQueue.pop();
        cout << "Processing Normal Order: " << o.name << " - " << o.item << endl;
    }
    else {
        cout << "No Orders to Process!\n";
        return;
    }

    addToHistory(o);
}

// Cancel Order (only from normal queue for simplicity)
void cancelOrder() {
    if(normalQueue.empty()) {
        cout << "No Order to Cancel!\n";
        return;
    }

    Order o = normalQueue.front();
    normalQueue.pop();
    cancelledStack.push(o);

    cout << "Order Cancelled: " << o.name << " - " << o.item << endl;
}

// Search Order
void searchOrder(int id) {
    queue<Order> temp = normalQueue;
    bool found = false;

    while(!temp.empty()) {
        Order o = temp.front();
        temp.pop();

        if(o.id == id) {
            cout << "Found in Normal Queue: " << o.name << " - " << o.item << endl;
            found = true;
        }
    }

    temp = vipQueue;
    while(!temp.empty()) {
        Order o = temp.front();
        temp.pop();

        if(o.id == id) {
            cout << "Found in VIP Queue: " << o.name << " - " << o.item << endl;
            found = true;
        }
    }

    if(!found) {
        cout << "Order Not Found!\n";
    }
}

// Display Order History
void displayHistory() {
    Node* temp = historyHead;

    cout << "\n--- Order History ---\n";
    while(temp != NULL) {
        cout << temp->data.name << " - " << temp->data.item << endl;
        temp = temp->next;
    }
}
void clearHistory() {
    Node* temp;

    while(historyHead != NULL) {
        temp = historyHead;
        historyHead = historyHead->next;
        delete temp;
    }

    cout << "Order History Cleared Successfully!\n";
}

// Main Function
int main() {
    int choice, id, itemChoice;
    string name;
    bool isVIP;
    

    while(true) {
        cout << "\n1. Place Order\n2. Process Order\n3. Cancel Order\n4. Search Order\n5. Display History\n";
        cout << "6. Clear History\n7. Exit\n";
        cout << "Enter choice: ";
        cin >> choice;

        switch(choice) {
            case 1:
                cout << "Enter Order ID: ";
                cin >> id;
                cout << "Enter Customer Name: ";
                cin >> name;

                displayMenu();
                cout << "Choose Item: ";
                cin >> itemChoice;

                cout << "VIP Order? (1=Yes, 0=No): ";
                cin >> isVIP;

                placeOrder(id, name, itemChoice, isVIP);
                break;

            case 2:
                processOrder();
                break;

            case 3:
                cancelOrder();
                break;

            case 4:
                cout << "Enter Order ID to Search: ";
                cin >> id;
                searchOrder(id);
                break;

            case 5:
                displayHistory();
                break;
            case 6:
                clearHistory();
                 break;

            case 7:
                 return 0;

            default:
                cout << "Invalid Choice!\n";
        }
    }
}

//g++ FoodOrdaringSystem.cpp -o FoodOrdaringSystem
//.\FoodOrdaringSystem.cpp